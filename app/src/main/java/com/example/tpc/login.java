package com.example.tpc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity implements View.OnClickListener {

    private TextView linkreg,signInButton,logemailtextview,logpasswordtextview,loginbutton_text;
    private LinearLayout loginbutton;
    private ImageView loginbackarrow;
    private EditText logemail,logpassword;

    private String userID,userEmail="";
    private FirebaseUser user;
    private DatabaseReference reference;

    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "login";
    private int RC_SIGN_IN = 1,loginstage = 0;
//    SharedPreferences sharedPreferences;
//    public static final String fileName = "userauth";
//    public static final String Email = "email";
//    public static final String Password = "password";

    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        linkreg = (TextView)findViewById(R.id.linkreg);
        logemailtextview = (TextView)findViewById(R.id.logemailtextview);
        logpasswordtextview = (TextView)findViewById(R.id.logpasswordtextview);
        loginbutton_text = (TextView)findViewById(R.id.loginbutton_text);
        logemail = (EditText)findViewById(R.id.logemail);

        loginbackarrow = (ImageView) findViewById(R.id.loginbackarrow);
        loginbackarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!userEmail.equals("")){
                    logemailtextview.setVisibility(View.GONE);
                }
                loginbutton_text.setText("PASSWORD");
                loginbackarrow.animate().alpha(0).setDuration(300);
                logemail.animate().scaleXBy(0.5f).setDuration(800);
                logemailtextview.animate().translationX(30f).setDuration(800);
                logpasswordtextview.animate().translationX(960f).setDuration(900);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        logemail.animate().scaleXBy(-0.5f).setDuration(800);
                        logemail.setText(userEmail);
                    }
                },800);
                loginstage=0;
            }
        });

        logemail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    logemailtextview.animate().alpha(0).setDuration(150);
                    logpasswordtextview.animate().alpha(0).setDuration(150);
                    logemail.animate().scaleXBy(0.2f).setDuration(800);
                }else{
                    if(logemail.getText().toString().isEmpty()){
                        logemailtextview.animate().alpha(1).setDuration(200);
                        logpasswordtextview.animate().alpha(1).setDuration(200);
                    }
                    logemail.animate().scaleXBy(-0.2f).setDuration(800);
                }
            }
        });

//        logpassword = (EditText)findViewById(R.id.logpassword);
        loginbutton = (LinearLayout) findViewById(R.id.loginbutton);
        mauth = FirebaseAuth.getInstance();

        linkreg.setOnClickListener(this);
        loginbutton.setOnClickListener(this);

        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.linkreg:
                startActivity(new Intent(this,register.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;
            case R.id.loginbutton:
                if(loginstage==0){
                    userEmail = logemail.getText().toString();
                    logemail.getText().clear();
                    logemail.animate().scaleXBy(0.5f).setDuration(800);
                    logemailtextview.animate().translationX(-900f).setDuration(800);
                    logpasswordtextview.animate().translationX(-960f).alpha(1).setDuration(900);
                    loginbutton_text.setText("GET ACCESS");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            logemail.animate().scaleXBy(-0.5f).setDuration(800);
                            loginbackarrow.animate().alpha(1).setDuration(300);
                        }
                    },800);

                    loginstage+=1;
                }else{
                    userLogin();
                }
                break;
            case R.id.signInButton:
                signInButton.setTextColor(Color.parseColor("#8f8f8f"));
                googleSignIn();
                break;
        }
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

    private void googleSignIn() {
        Intent gSignIn = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(gSignIn,RC_SIGN_IN);
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
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Connection Error. Try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mauth.getCurrentUser();
                            reference = FirebaseDatabase.getInstance().getReference("Users");
                            userID = user.getUid();

                            if(user.isEmailVerified()){
                                reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User userProfile = snapshot.getValue(User.class);
                                        if(userProfile != null){
                                            String check = userProfile.isAdmin;
                                            Intent i = new Intent(login.this,adminindex.class);
                                            i.putExtra("isAdmin",check);
                                            startActivity(i);
//                                            if(check.equals("YES")){
//                                                startActivity(new Intent(login.this,adminindex.class));
//                                            }else{
//                                                startActivity(new Intent(login.this,index.class));
//                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }else{
                                Toast.makeText(login.this, "Authentication Failed", Toast.LENGTH_LONG).show();

                            }

                        } else {
                            Toast.makeText(login.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void userLogin() {
        String email = userEmail;
        String pass = logemail.getText().toString().trim();

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