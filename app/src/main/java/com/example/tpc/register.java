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
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity implements View.OnClickListener {
    private TextView linklogin,regbutton;
    private EditText regname,regroll,regemail,regpass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        linklogin = (TextView)findViewById(R.id.linklogin);
        regname = (EditText)findViewById(R.id.regname);
        regroll = (EditText)findViewById(R.id.regroll);
        regemail = (EditText)findViewById(R.id.regemail);
        regpass = (EditText)findViewById(R.id.regpassword);

        regbutton = (Button)findViewById(R.id.regbutton);
        regbutton.setOnClickListener(this);

        linklogin.setOnClickListener(this);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.linklogin:
                startActivity(new Intent(this,login.class));
                break;
            case R.id.regbutton:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String name = regname.getText().toString().trim();
        String roll = regroll.getText().toString().trim();
        String email = regemail.getText().toString().trim();
        String pass = regpass.getText().toString().trim();
        String isAdmin = "NO";

        if(name.isEmpty()){
            regname.setError("Name is required");
            regname.requestFocus();
            return;
        }
        if(roll.isEmpty()){
            regroll.setError("Roll No is required");
            regroll.requestFocus();
            return;
        }
        if(pass.isEmpty()){
            regpass.setError("Password is required");
            regpass.requestFocus();
            return;
        }
        if(pass.length()<6){
            regpass.setError("Password must be at least 6 characters long");
            regpass.requestFocus();
            return;
        }
        if(email.isEmpty()){
            regemail.setError("Email is required");
            regemail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            regemail.setError("Provide valid Email");
            regemail.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name,roll,email,isAdmin);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.sendEmailVerification();
                                        Toast.makeText(register.this,"Registration Successful",Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(register.this,"Failed to register",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(register.this,"Failed to register",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }


}