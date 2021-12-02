package com.example.tpc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import fragments.a_contests;
import fragments.a_dashboard;
import fragments.a_profile;

public class adminindex extends AppCompatActivity {

    private Button logoutbutton;
    private GoogleSignInClient mGoogleSignInClient;
    private LinearLayout bn_dashboard,bn_contests,bn_profile,bn_logout;
//    ChipNavigationBar chipNavigationBar;
    private SlidingRootNav slidingRootNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminindex);

        Toolbar toolbar = findViewById(R.id.toolbar);
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.nav_drawer)
                .inject();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new a_dashboard()).commit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        bn_dashboard = findViewById(R.id.bn_dashboard);
        bn_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new a_dashboard()).commit();
            }
        });
        bn_contests = findViewById(R.id.bn_contests);
        bn_contests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new a_contests()).commit();
            }
        });
        bn_profile = findViewById(R.id.bn_profile);
        bn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new a_profile()).commit();
            }
        });
        bn_logout = findViewById(R.id.bn_logout);
        bn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(adminindex.this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                startActivity(new Intent(adminindex.this, login.class));
            }
        });


//        chipNavigationBar = findViewById(R.id.bottom_nav_bar);
//        chipNavigationBar.setItemSelected(R.id.b_dashboard,true);


//
//        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int i) {
//                Fragment fragment = null;
//                switch (i){
//                    case R.id.b_dashboard:
//                        fragment = new a_dashboard();
//                        break;
//                    case R.id.b_contests:
//                        fragment = new a_contests();
//                        break;
//                    case R.id.b_profile:
//                        fragment = new a_profile();
//                        break;
//                }
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
//            }
//        });
    }
}