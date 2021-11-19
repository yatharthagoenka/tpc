package com.example.tpc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class index extends AppCompatActivity implements View.OnClickListener {

    private Button logoutbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        logoutbutton = (Button)findViewById(R.id.logoutbutton);
        logoutbutton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logoutbutton:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,login.class));
                break;
        }
    }
}