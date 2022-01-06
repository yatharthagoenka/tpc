package com.example.tpc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private String userID;
    private DatabaseReference reference;
    private typewriter ss_heading;
    private ImageView ss_img;
    private View ss_progressBar,ss_shadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ss_heading = findViewById(R.id.ss_heading);
        ss_heading.setText("");
        ss_heading.setCharacterDelay(55);
        ss_heading.animateText("THE PROGRAMMING CLUB");

        ss_img = findViewById(R.id.ss_img);
        ss_img.animate().alpha(1f).translationY(-20f).setDuration(1000);

        ss_progressBar = findViewById(R.id.ss_progressBar);
        ss_shadow = findViewById(R.id.ss_shadow);
        Animation anim = new ScaleAnimation(
                0f, 1f, // Start and end values for the X axis scaling
                1f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(650);
        ss_progressBar.startAnimation(anim);
        ss_shadow.startAnimation(anim);

        FirebaseUser usercheck = FirebaseAuth.getInstance().getCurrentUser();

        if (usercheck != null) {
            // User is signed in
            reference = FirebaseDatabase.getInstance().getReference("Users");
            userID = usercheck.getUid();

            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile2 = snapshot.getValue(User.class);
                    if(userProfile2 != null){
                        String check = userProfile2.isAdmin;
                        if(check.equals("YES")){
                            startActivity(new Intent(MainActivity.this,adminindex.class));
                        }else{
                            startActivity(new Intent(MainActivity.this,index.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MainActivity.this,login.class);
                    startActivity(i);
                    finish();
                }
            },1600);
        }

    }
}