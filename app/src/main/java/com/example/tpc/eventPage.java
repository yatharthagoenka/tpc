package com.example.tpc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

import cdflynn.android.library.checkview.CheckView;

public class eventPage extends AppCompatActivity {

    private ImageView ep_cover,ep_backbutton;
    private TextView ep_name,ep_domain,ep_date,ep_duration,ep_desc,ep_rsvpButton,ep_successText,ep_regCount;
    private CheckView ep_rsvpCheck;
    private RelativeLayout ep_successOverlay;
    private ConstraintLayout ep_usersegment;
    private RecyclerView ep_userlistRV;
    ArrayList<String> data,dataHost;
    ArrayList<Vector<String>> dataRV;


    private String isAdmin,userID;
    private FirebaseUser user;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_page);

        Fade fade = new Fade();
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    isAdmin = userProfile.isAdmin;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Intent i = getIntent();


        ep_successOverlay = findViewById(R.id.ep_successOverlay);
        ep_rsvpCheck = findViewById(R.id.ep_rsvpCheck);
        ep_successText = findViewById(R.id.ep_successText);

        ep_regCount = findViewById(R.id.ep_regCount);
        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        DocumentReference docRef2 = db2.collection("events").document(i.getStringExtra("docID"));
        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ep_regCount.setText(document.get("regCount")+" Attending");
                    }
                }
            }
        });

        ep_name = findViewById(R.id.ep_name);
        ep_name.setText(i.getStringExtra("name"));

        ep_domain = findViewById(R.id.ep_domain);
        ep_domain.setText(i.getStringExtra("domain"));

        ep_desc = findViewById(R.id.ep_desc);
        ep_desc.setText(i.getStringExtra("desc"));

        data = new ArrayList<>();
        dataHost = new ArrayList<>();

        ep_date = findViewById(R.id.ep_date);
        ep_date.setText(i.getStringExtra("date"));
        ep_duration = findViewById(R.id.ep_duration);
        ep_duration.setText("DURATION - "+i.getStringExtra("duration"));

        ep_cover = findViewById(R.id.ep_cover);
        if(i.getStringExtra("domain").equals("Web Development")){
            ep_cover.setImageResource(R.drawable.web_cardview);
        }else if(i.getStringExtra("domain").equals("App Development")){
            ep_cover.setImageResource(R.drawable.android_cardview);
        }else if(i.getStringExtra("domain").equals("Club Session")){
            ep_cover.setImageResource(R.drawable.session_cardview);
        }else if(i.getStringExtra("domain").equals("AI/ML")){
            ep_cover.setImageResource(R.drawable.ai_cardview);
        }else{
            ep_cover.setImageResource(R.drawable.cp_cardview);
        }

//        ArrayList<String> regCount = docRef.get("regCount")

        ep_usersegment = findViewById(R.id.ep_usersegment);
        ep_usersegment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.ep_userswindow, null);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                dataRV = new ArrayList<>();

                ep_userlistRV = (RecyclerView) popupView.findViewById(R.id.epusers_rv);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("events").document(i.getStringExtra("docID"));
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                data = (ArrayList<String>) document.get("rsvp");
                                dataHost = (ArrayList<String>) document.get("hosts");
                                for(String s: data){
                                    Vector<String> tmp = new Vector<>();
                                    String roll = s.split("\\|")[1];
                                    tmp.add(s);
                                    boolean contains = false;
                                    for (String c : dataHost) {
                                        if (c.trim().equals(roll.trim()))
                                            contains = true;
                                    }
                                    tmp.add(String.valueOf(contains));
                                    tmp.add(i.getStringExtra("docID"));
                                    tmp.add(isAdmin);
                                    dataRV.add(tmp);
                                }

                                try {
                                    epRSVPAdapter adapter = new epRSVPAdapter(getApplicationContext(),dataRV);
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                    ep_userlistRV.setLayoutManager(mLayoutManager);
                                    ep_userlistRV.setAdapter(adapter);
                                }catch (Exception e){
                                    Log.d("testrv",e.toString());
                                }

                            } else {
                                Log.d("eventPage", "No such document");
                            }
                        } else {
                            Log.d("eventPage", "Connection failed with ", task.getException());
                        }
                    }
                });

                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
                popupWindow.setAnimationStyle(R.style.popUpAnimation);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });

        ep_rsvpButton = findViewById(R.id.ep_rsvpButton);
        ep_rsvpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(i.getStringExtra("currUserRoll")==null)){

//                  Toast.makeText(v.getContext(),model.getEventName(),Toast.LENGTH_SHORT).show();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docref = db.collection("events").document(i.getStringExtra("docID"));
                    docref.update("rsvp", FieldValue.arrayUnion(i.getStringExtra("currUsername")+" | "+i.getStringExtra("currUserRoll")));

                    docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Map<String,Object> data = document.getData();
                                docref.update("regCount", ((ArrayList<?>) data.get("rsvp")).size());
//                                Toast.makeText(getApplicationContext(), "RSVP Confirmed", Toast.LENGTH_SHORT).show();
//                                ep_successOverlay.animate().translationY(-1200f).setDuration(500);
                                Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                                ep_successOverlay.startAnimation(slideIn);
                                slideIn.setAnimationListener(new Animation.AnimationListener(){
                                    @Override
                                    public void onAnimationStart(Animation arg0) {
                                        ep_successOverlay.setAlpha(1);
                                    }
                                    @Override
                                    public void onAnimationRepeat(Animation arg0) {
                                    }
                                    @Override
                                    public void onAnimationEnd(Animation arg0) {
                                        ep_rsvpCheck.check();
                                        ep_successText.setAlpha(1);
//                                        ep_successText;

                                    }
                                });
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ep_successOverlay.setAlpha(0);
                                        ep_rsvpCheck.setAlpha(0);
                                        ep_successText.setAlpha(0);
                                    }
                                },3000);
                            } else {
                                Log.d("rsvp", "got failed with ", task.getException());
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "RSVP failed. Try restarting the app.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ep_backbutton = findViewById(R.id.ep_backbutton);
        ep_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }



    public interface DialogListener{
        void eventInfo(String docID);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.mock_anim, R.anim.slide_out_right);
    }

}