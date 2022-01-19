package com.example.tpc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import fragments.a_contests;
import fragments.a_dashboard;
import fragments.a_profile;
import fragments.profile;
import fragments.u_contests;
import fragments.u_dashboard;
import fragments.u_profile;

public class index extends AppCompatActivity {

    private LinearLayout bn_dashboard,bn_contests,bn_profile,bn_logout;
    private SlidingRootNav slidingRootNav;

    private String userID,username;
    private FirebaseUser user;
    private DatabaseReference reference;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Toolbar toolbar = findViewById(R.id.u_toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.nav_drawer)
                .inject();

        getSupportFragmentManager().beginTransaction().replace(R.id.u_fragment_container,new u_dashboard()).commit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    username = userProfile.name;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bn_dashboard = findViewById(R.id.bn_dashboard);
        bn_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu();
                getSupportFragmentManager().beginTransaction().replace(R.id.u_fragment_container,new u_dashboard()).commit();
            }
        });
        bn_contests = findViewById(R.id.bn_contests);
        bn_contests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu();
                getSupportFragmentManager().beginTransaction().replace(R.id.u_fragment_container,new u_contests()).commit();
            }
        });
        bn_profile = findViewById(R.id.bn_profile);
        bn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu();
                Bundle bundle = new Bundle();
                bundle.putString("username",username);
                bundle.putString("callingAct","index");
                Fragment prof = new profile();
                prof.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.u_fragment_container,prof).commit();
            }
        });
        bn_logout = findViewById(R.id.bn_logout);
        bn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(index.this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                startActivity(new Intent(index.this, login.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}