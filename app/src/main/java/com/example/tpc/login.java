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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity implements View.OnClickListener {

    private TextView linkreg;
    private Button loginbutton;
    private EditText logemail,logpassword;

    private String userID;
    private FirebaseUser user;
    private DatabaseReference reference;

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
                    user = FirebaseAuth.getInstance().getCurrentUser();

                    reference = FirebaseDatabase.getInstance().getReference("Users");
                    userID = user.getUid();

                    if(user.isEmailVerified()){
                        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User userProfile = snapshot.getValue(User.class);
                                if(userProfile != null){
                                    String check = userProfile.isAdmin;
                                    if(check.equals("YES")){
                                        startActivity(new Intent(login.this,adminindex.class));
                                    }else{
                                        startActivity(new Intent(login.this,index.class));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }else{
                        Toast.makeText(login.this,"Verify Email before login",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(login.this,"Invalid Credentials",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}