package com.example.myfriends;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfriends.chatRoomList.ChatRoomListItemVO;
import com.example.myfriends.dbHelperPackage.ProfileDBHelper;
import com.example.myfriends.friendsList.FriendsListItemVO;
import com.example.myfriends.managerPackage.NetworkManager;
import com.example.myfriends.servicePackage.ChatService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;



public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public ArrayList<FriendsListItemVO> friendsListItemVOS;
    public ArrayList<ChatRoomListItemVO> chatRoomListItemVOS;
    public HashMap<String,Integer> chatRoomListHash;
    public static OAuthLogin mOAuthLoginModule;
    OAuthLoginButton mOAuthLoginButton;
    public String userEmail;
    public String nicName;
    public String userId;
    public boolean isChatRoomListReceived;
    public boolean isFriendsListReceived;
    boolean fileReadPermission;
    boolean fileWritePermission;
    boolean cameraPermission;
    private Button loginBtn;
    private Button registerBtn;
    private EditText editEmail;
    private EditText editPassword;
    private ProfileDBHelper profileDBHelper;
    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
    //파이어베이스 인증 객체
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton google_login_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        permissionCheck();
        profileDBHelper=new ProfileDBHelper(this);

        //친구목록과 채팅방목록 디비에서 받기
        registerReceiver(friendsList,new IntentFilter("com.example.FRIENDS_LIST_RECEIVE_ACTION"));
        registerReceiver(chatRoomList,new IntentFilter("com.example.CHAT_ROOM_LIST_RECEIVE_ACTION"));
        registerReceiver(loginComplete,new IntentFilter("com.example.LOGIN_COMPLETE_ACTION"));
        registerReceiver(gotoMainActivity,new IntentFilter("com.example.ALL_COMPLETE_ACTION"));
        //채팅 service 시작'
        Intent intent=new Intent(this, ChatService.class);
        startService(intent);
        /*
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            startForegroundService(intent);
        }else{
            startService(intent);
        }
        */

        firebaseAuth=FirebaseAuth.getInstance();
        loginBtn =(Button)findViewById(R.id.loginBtn);
        registerBtn=(Button)findViewById(R.id.registerBtn);
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        editEmail=(EditText)findViewById(R.id.txtId);
        editPassword=(EditText)findViewById(R.id.txtPw);
        chatRoomListHash=new HashMap<>();
        userEmail="";
        isChatRoomListReceived=false;
        isFriendsListReceived=false;
        //네이버 연동
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                this,
                getString(R.string.NaverClientID),
                getString(R.string.NaverClientSecret),
                getString(R.string.clientName)
                );
        mOAuthLoginButton = findViewById(R.id.buttonOAuthLogin);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);

        //구글 로그인 연동
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.GoogleKey))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        google_login_btn=findViewById(R.id.Google_sign_in_button);
        google_login_btn.setOnClickListener(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.v(TAG, "Google sign in result");
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    //구글 로그인
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "인증 성공", Toast.LENGTH_SHORT).show();
                            FirebaseUser user=firebaseAuth.getCurrentUser();
                            userEmail=user.getEmail();
                            Log.v(TAG, user.getEmail());
                            Log.v(TAG, user.getUid());
                            NetworkManager.getInstance().login(userEmail);
                            Log.v(TAG, "Google sign in success");
                        } else {
                            Toast.makeText(getApplicationContext(), "인증 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void signIn(){
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        if(isValidEmail(email) && isValidPasswd(password)) {
            loginUser(email, password);
        }
    }
    // 이메일 유효성 검사
    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd(String password) {
        if (password.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }
    // 로그인
    private void loginUser(final String email, final String password)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            userEmail=email;
                            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                            NetworkManager.getInstance().login(userEmail);
                        } else {
                            // 로그인 실패
                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onClick(View v){
        if(v== loginBtn){
            signIn();
        }
        else if(v==registerBtn){
            Intent intent=new Intent(getApplicationContext(),registerActivity.class);
            intent.putExtra("email","");
            startActivity(intent);
        }else if(v==google_login_btn){
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }
    //네이버 로그인
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginModule.getAccessToken(getApplicationContext());
                String refreshToken = mOAuthLoginModule.getRefreshToken(getApplicationContext());
                long expiresAt = mOAuthLoginModule.getExpiresAt(getApplicationContext());
                String tokenType = mOAuthLoginModule.getTokenType(getApplicationContext());
                new RequestApi().execute();
            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(getApplicationContext()).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(getApplicationContext());
                Toast.makeText(getApplicationContext(), "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        };
    };
    private  class RequestApi extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = mOAuthLoginModule.getAccessToken(getApplicationContext());
            return mOAuthLoginModule.requestApi(getApplicationContext(), at, url);
        }

        @Override
        protected void onPostExecute(String content) {
            super.onPostExecute(content);
            try{
                JSONObject jsonObject = new JSONObject(content);
                JSONObject response = jsonObject.getJSONObject("response");
                userEmail = response.getString("email");
                Log.v("onPostExecute",response.getString("email"));
                NetworkManager.getInstance().login(userEmail);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
    BroadcastReceiver friendsList=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("loginActivity","friendsList");
            friendsListItemVOS=intent.getParcelableArrayListExtra("data");
            isFriendsListReceived=true;
            Intent friendsList_intent=new Intent("com.example.ALL_COMPLETE_ACTION");
            sendBroadcast(friendsList_intent);
            //setFragment(0);
            //Log.v("argumentCount",friendsListItemVOS.get(0).getStateMessage());
        }
    };
    BroadcastReceiver chatRoomList=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("loginActivity","chatRoomList");
            chatRoomListItemVOS=intent.getParcelableArrayListExtra("data");

           for(int i=0;i<chatRoomListItemVOS.size();i++){
               chatRoomListHash.put((String)chatRoomListItemVOS.get(i).getChatRoomId(),i);
           }

            isChatRoomListReceived=true;
            Intent chatRoomList_intent=new Intent("com.example.ALL_COMPLETE_ACTION");
            sendBroadcast(chatRoomList_intent);
        }
    };
    BroadcastReceiver gotoMainActivity=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("loginActivity","gotoMainActivity");
            if(isChatRoomListReceived&&isFriendsListReceived) {
                isChatRoomListReceived=false;
                isFriendsListReceived=false;
                profileDBHelper.initProfile(nicName);
                Intent mainintent = new Intent(getApplicationContext(), MainActivity.class);
                mainintent.putParcelableArrayListExtra("friendsListData", friendsListItemVOS);
                mainintent.putParcelableArrayListExtra("chatRoomListData", chatRoomListItemVOS);
                mainintent.putExtra("chatRooms",chatRoomListHash);
                mainintent.putExtra("userEmail", userEmail);
                mainintent.putExtra("nicName",nicName);
                startActivity(mainintent);
            }
        }
    };
    BroadcastReceiver loginComplete=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("type").equals("joined")){
                nicName=intent.getStringExtra("nicName");
                NetworkManager.getInstance().friendsList(userEmail);
                NetworkManager.getInstance().chatRoomList(userEmail,nicName);
            }else{
                Intent register_intent=new Intent(getApplicationContext(),registerActivity.class);
                register_intent.putExtra("email",userEmail);
                startActivity(register_intent);
            }
        }
    };
    public void permissionCheck(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            fileReadPermission=true;
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            fileWritePermission=true;
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            cameraPermission=true;
        }
        if(!fileReadPermission||!fileWritePermission||!cameraPermission){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},200);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[]permissions,int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode==200&&grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                fileReadPermission=true;
            }
            if(grantResults[1]==PackageManager.PERMISSION_GRANTED){
                fileWritePermission=true;
            }
            if(grantResults[2]==PackageManager.PERMISSION_GRANTED){
                cameraPermission=true;
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mOAuthLoginModule.logoutAndDeleteToken(getApplicationContext());
        friendsListItemVOS=null;
        chatRoomListItemVOS=null;
        chatRoomListHash=null;
        userEmail=null;
        nicName=null;
        userId=null;
        loginBtn=null;
        registerBtn=null;
        editEmail=null;
        editPassword=null;
        System.gc();
        unregisterReceiver(friendsList);
        unregisterReceiver(chatRoomList);
        unregisterReceiver(loginComplete);
        unregisterReceiver(gotoMainActivity);
        Log.v("AppStatus","LoginActivity onDestroy call");
        finish();
        //mOAuthLoginModule.logout(getApplicationContext());
    }
}
