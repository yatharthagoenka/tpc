package com.example.tpc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity implements View.OnClickListener {

    private TextView linkreg;
    private Button loginbutton;
    private EditText logemail,logpassword;

    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        linkreg = (TextView)findViewById(R.id.linkreg);
        logemail = (EditText)findViewById(R.id.logemail);
        logpassword = (EditText)findViewById(R.id.logpassword);
        loginbutton = (Button)findViewById(R.id.loginbutton);
        mauth = FirebaseAuth.getInstance();

        linkreg.setOnClickListener(this);
        loginbutton.setOnClickListener(this);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.linkreg:
                startActivity(new Intent(this,register.class));
                break;
            case R.id.loginbutton:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = logemail.getText().toString().trim();
        String pass = logpassword.getText().toString().trim();

        if(email.isEmpty()){
            logemail.setError("Email is required");
            logemail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            logemail.setError("Provide valid Email");
            logemail.requestFocus();
            return;
        }
        if(pass.isEmpty()){
            logpassword.setError("Password is required");
            logpassword.requestFocus();
            return;
        }
        if(pass.length()<6){
            logpassword.setError("Password must be at least 6 characters long");
            logpassword.requestFocus();
            return;
        }

        mauth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()){
                        startActivity(new Intent(login.this,index.class));
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(login.this,"Verification Mail Sent",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(login.this,"Invalid Credentials",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}