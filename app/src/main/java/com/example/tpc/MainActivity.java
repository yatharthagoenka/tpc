package com.example.tpc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            },1100);
        }

    }
}