package com.example.myfriends.friendsList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfriends.managerPackage.NetworkManager;
import com.example.myfriends.R;
import com.example.myfriends.dbHelperPackage.SQLiteDBHelper;
import com.example.myfriends.dbHelperPackage.ProfileDBHelper;

public class FriendsPlusActivity extends AppCompatActivity {
    boolean isFriendsIDExist;
    boolean isTextExist;
    String nicName;
    String userEmail;
    String myNicName;
    EditText editFriendID;
    SQLiteDBHelper sqLiteDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_plus);
        Intent intent=getIntent();
        userEmail=intent.getStringExtra("userEmail");
        myNicName=intent.getStringExtra("myNicName");
        editFriendID=(EditText)findViewById(R.id.friendID);
        sqLiteDBHelper=new SQLiteDBHelper(this);
        editFriendID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(editFriendID.getText().equals("")){
                    isTextExist=false;
                }else{
                    isTextExist=true;
                    nicName=editFriendID.getText().toString();
                }
                invalidateOptionsMenu();
            }
        });
        registerReceiver(friendsPlusResult,new IntentFilter("com.example.FRIENDS_PLUS_RESULT_ACTION"));
        getSupportActionBar().setTitle("MyFriendsID로 친구추가");
    }
    BroadcastReceiver friendsPlusResult=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isFriendsIDExist=intent.getBooleanExtra("result",false);
            if(isFriendsIDExist){
                Toast.makeText(getApplicationContext(),"친구추가 완료",Toast.LENGTH_LONG).show();

                new ProfileDBHelper(getApplicationContext()).initProfile(nicName);
                Intent main_intent=new Intent("com.example.FRIENDS_LIST_UPDATE");
                main_intent.putExtra("nicName",nicName);
                sendBroadcast(main_intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"없는 사람입니다",Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.friends_plus,menu);
        MenuItem menuItem=menu.findItem(R.id.find);
        if(isTextExist){
            menuItem.setEnabled(true);

        }else{
            menuItem.setEnabled(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.find){
            NetworkManager.getInstance().friendsPlus(userEmail,nicName,myNicName);
        }
        return super.onContextItemSelected(menuItem);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(friendsPlusResult);
        System.gc();
        finish();
    }
}
