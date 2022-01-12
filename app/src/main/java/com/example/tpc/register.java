package com.example.tpc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity implements View.OnClickListener {
    private TextView linklogin,regnametext,regrolltext,regemailtext,regpasswordtext;
    private LinearLayout regbutton;
    private EditText regname,regroll,regemail,regpass;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        linklogin = (TextView)findViewById(R.id.linklogin);
        regnametext = (TextView)findViewById(R.id.regnametext);
        regrolltext = (TextView)findViewById(R.id.regrolltext);
        regemailtext = (TextView)findViewById(R.id.regemailtext);
        regpasswordtext = (TextView)findViewById(R.id.regpasswordtext);
        regname = (EditText)findViewById(R.id.regname);
        regroll = (EditText)findViewById(R.id.regroll);
        regemail = (EditText)findViewById(R.id.regemail);
        regpass = (EditText)findViewById(R.id.regpassword);

        regname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    regnametext.animate().alpha(0).setDuration(150);
                    regname.animate().scaleXBy(0.2f).setDuration(800);
                }else{
                    if(regname.getText().toString().isEmpty()){
                        regnametext.animate().alpha(1).setDuration(200);
                    }
                    regname.animate().scaleXBy(-0.2f).setDuration(800);
                }
            }
        });

        regroll.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    regrolltext.animate().alpha(0).setDuration(150);
                    regroll.animate().scaleXBy(0.2f).setDuration(800);
                }else{
                    if(regroll.getText().toString().isEmpty()){
                        regrolltext.animate().alpha(1).setDuration(200);
                    }
                    regroll.animate().scaleXBy(-0.2f).setDuration(800);
                }
            }
        });

        regemail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    regemailtext.animate().alpha(0).setDuration(150);
                    regemail.animate().scaleXBy(0.2f).setDuration(800);
                }else{
                    if(regemail.getText().toString().isEmpty()){
                        regemailtext.animate().alpha(1).setDuration(200);
                    }
                    regemail.animate().scaleXBy(-0.2f).setDuration(800);
                }
            }
        });

        regpass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    regpasswordtext.animate().alpha(0).setDuration(150);
                    regpass.animate().scaleXBy(0.2f).setDuration(800);
                }else{
                    if(regpass.getText().toString().isEmpty()){
                        regpasswordtext.animate().alpha(1).setDuration(200);
                    }
                    regpass.animate().scaleXBy(-0.2f).setDuration(800);
                }
            }
        });



        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        regbutton = (LinearLayout) findViewById(R.id.regbutton);
        regbutton.setOnClickListener(this);

        linklogin.setOnClickListener(this);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.linklogin:
                startActivity(new Intent(this,login.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
        String isAdmin;
        isAdmin = "NO";

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

        progressBar.setVisibility(View.VISIBLE);

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
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(register.this,"Verification Mail Sent",Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(register.this,"Failed to register",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(register.this,"Failed to register",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}