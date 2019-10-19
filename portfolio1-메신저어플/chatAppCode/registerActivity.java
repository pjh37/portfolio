package com.example.myfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import com.example.myfriends.managerPackage.NetworkManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class registerActivity extends AppCompatActivity implements View.OnClickListener{
    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
    //파이어베이스 인증 객체
    private FirebaseAuth firebaseAuth;
    //이메일 및 비밀번호
    private EditText editEmail;
    private EditText editPassword;
    private EditText editNicName;
    private Button registerBtn;
    private Button cancelBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent intent=getIntent();

        firebaseAuth=FirebaseAuth.getInstance();
        editEmail=(EditText)findViewById(R.id.txtEmail);
        editPassword=(EditText)findViewById(R.id.txtPw);
        editNicName=(EditText)findViewById(R.id.txtNicName);
        registerBtn=(Button)findViewById(R.id.registerBtn);
        cancelBtn=(Button)findViewById(R.id.cancelBtn);
        registerBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        if(!intent.getStringExtra("email").equals("")){
            editEmail.setText(intent.getStringExtra("email"));
        }
    }
    @Override
    public void onClick(View v) {
        if(v==registerBtn){
            String email=editEmail.getText().toString();
            String password=editPassword.getText().toString();
            if(isValidEmail(email)&&isValidPasswd(password)){
                createUser(email,password);
            }

        }else if(v==cancelBtn){
            finish();
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
    // 회원가입
    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공

                            NetworkManager.getInstance().join(editEmail.getText().toString(),
                                    editPassword.getText().toString(),
                                    editNicName.getText().toString());


                            /*
                            Intent joinIntent =new Intent("com.example.JOIN_ACTION");
                            joinIntent.putExtra("email",editEmail.getText().toString());
                            joinIntent.putExtra("password",editPassword.getText().toString());
                            joinIntent.putExtra("nicName",editNicName.getText().toString());
                            sendBroadcast(joinIntent);
                            */
                            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // 회원가입 실패
                            Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
