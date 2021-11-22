package com.example.tpc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import fragments.a_contests;
import fragments.a_dashboard;
import fragments.a_profile;
import fragments.u_contests;
import fragments.u_dashboard;
import fragments.u_profile;

public class index extends AppCompatActivity {

    private Button logoutbutton;

    ChipNavigationBar chipNavigationBar;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        chipNavigationBar = findViewById(R.id.user_bn_bar);
        chipNavigationBar.setItemSelected(R.id.bn_dashboard,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.u_fragment_container,new u_dashboard()).commit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i){
                    case R.id.bn_dashboard:
                        fragment = new u_dashboard();
                        break;
                    case R.id.bn_contests:
                        fragment = new u_contests();
                        break;
                    case R.id.bn_profile:
                        fragment = new u_profile();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.u_fragment_container,fragment).commit();
            }
        });
    }

}