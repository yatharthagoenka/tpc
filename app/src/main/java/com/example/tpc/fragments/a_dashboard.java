package com.example.tpc.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpc.Adapters.EventAdapter;
import com.example.tpc.R;
import com.example.tpc.Models.User;
import com.example.tpc.eventChange;
import com.example.tpc.Models.eventModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

public class a_dashboard extends Fragment {

    View view;
    private TextView dash_username, newEventButton,eventtexts,filterbutton;
    private EditText dash_searchbox;
    private ImageView dash_profilepic;
    private LinearLayout all_chipdash,cp_chipdash,web_chipdash,app_chipdash,ai_chipdash;

    private String userID,isAdmin,rollno,username,fetchAdminCheck;
    private FirebaseUser user;
    private DatabaseReference reference;
    private GoogleSignInClient mGoogleSignInClient;

    private RecyclerView eventRV;
    private ArrayList<eventModel> eventModelArrayList;
    private Vector<Vector<String>> eventData;

    class domainButtonClass{
        LinearLayout l;
        int f;
    }

//    private Vector<domainButtonClass> domainButtons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_a_dashboard, container, false);
        dash_username = view.findViewById(R.id.dash_username);
        filterbutton = view.findViewById(R.id.filterbutton);
        dash_profilepic = view.findViewById(R.id.dash_profilepic);
        dash_searchbox = view.findViewById(R.id.dash_searchbox);

        fetchAdminCheck = getArguments().getString("adminCheck");

//        Intent startIntent = new Intent(getActivity(), eventChange.class);
//        startIntent.setAction("te");
//        getActivity().startService(startIntent);

        all_chipdash = view.findViewById(R.id.all_chipdash);
        cp_chipdash = view.findViewById(R.id.cp_chipdash);
        web_chipdash = view.findViewById(R.id.web_chipdash);
        app_chipdash = view.findViewById(R.id.app_chipdash);
        ai_chipdash = view.findViewById(R.id.ai_chipdash);

//        domainButtons = new Vector<>(new domainButtonClass(), cp_chipdash, web_chipdash, app_chipdash, ai_chipdash);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(),gso);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    username = userProfile.name;
                    dash_username.setText(username);
                    isAdmin = userProfile.isAdmin;
                    rollno = userProfile.roll;
                    setUserDP();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        eventRV = view.findViewById(R.id.eventRV);
        eventData = new Vector<Vector<String>>();
        readEventData();

        newEventButton = view.findViewById(R.id.newEventButton);

        if(fetchAdminCheck.equals("NO")){
            newEventButton.setVisibility(View.GONE);
            newEventButton.setClickable(false);
        }

        dash_searchbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<eventModel> filteredlist = new ArrayList<>();

                // running a for loop to compare elements.
                for (eventModel item : eventModelArrayList) {
                    // checking if the entered string matched with any item of our recycler view.
                    String t1=item.getEventName().toLowerCase();
                    String t2=dash_searchbox.getText().toString().toLowerCase();
                    if (t1.contains(t2)) {
                        filteredlist.add(item);
                    }else{

                        Log.d("testing", String.valueOf(t1.contains(t2)));
//                        Log.d("testing",item.getEventName().toLowerCase()+" | "+searchbox.getText().toString().toLowerCase());
                    }
                }
                if (filteredlist.isEmpty()) {
                    Toast.makeText(getContext(), "No related events found", Toast.LENGTH_SHORT).show();
                } else {
                    // at last we are passing that filtered
                    // list to our adapter class.
                    EventAdapter eventAdapter= new EventAdapter(getActivity(), filteredlist);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    eventRV.setLayoutManager(linearLayoutManager);
                    eventRV.setAdapter(eventAdapter);
                }
            }

            public void afterTextChanged(Editable s) {

            }
        });

        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEventSheet neweventsheet = new newEventSheet();
                neweventsheet.show(getActivity().getSupportFragmentManager(), neweventsheet.getTag());
            }
        });

        all_chipdash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDomain(0);
//                all_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashselected));
//                cp_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                web_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                app_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                cp_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                ai_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
            }
        });

        cp_chipdash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDomain(1);
//                all_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                cp_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashselected));
//                web_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                app_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                cp_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                ai_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
            }
        });

        web_chipdash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDomain(2);
//                all_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                cp_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                web_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashselected));
//                app_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                cp_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                ai_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
            }
        });

        app_chipdash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDomain(3);
//                all_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                cp_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                web_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                app_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashselected));
//                cp_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                ai_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
            }
        });

        ai_chipdash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDomain(4);
//                all_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                cp_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                web_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                app_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                cp_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//                ai_chipdash.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashselected));
            }
        });

        return view;
    }

    void selectDomain(int k){
//        for(int i=0; i<5;i++){
//            if(i==k) domainButtons.get(i).setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashselected));
//            else{
//                domainButtons.get(i).setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dashunselected));
//            }
//        }ee
    }


    void setUserDP(){
        final String[] userDP = {""};

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(rollno);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String,Object> data = document.getData();
                        try {
                            new ImageLoadTask(data.get("userDP").toString(), dash_profilepic).execute();
                            Log.d("testdp","loading dp from db");
                        }catch(Exception e){
                            Log.d("testdp","loading dp from google acc");

                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
                            if (acct != null) {
                                Uri personPhoto = acct.getPhotoUrl();
                                new ImageLoadTask(personPhoto.toString(), dash_profilepic).execute();

                                userRef.update("userDP", personPhoto.toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i("setUserDP", "User DP successfully updated!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("setUserDP", "Error updating document", e);
                                            }
                                        });
                            }
                        }
                    } else {
                    }
                } else {
                    Log.d("fetchedUser", "get failed with ", task.getException());
                }
            }
        });

    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

    private void readEventData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Log.d("event", document.getId() + " => " + document.getData());
                              Map<String,Object> data = document.getData();
//                            Log.d("eventtest2",data.get("name").toString());

//                                String[] full_date = data.get("eventDate").toString().split(" ");
//                                String day = full_date[0];
//                                String month = full_date[1];
                            String date = data.get("eventDate").toString();
                            String name = data.get("name").toString();
                            String domain = data.get("domain").toString();
                            String regCount = data.get("regCount").toString()+" Joined";
                            String duration = data.get("duration").toString();
                            String desc = data.get("Desc").toString();

                            Vector<String> tmp = new Vector<String>();
                            tmp.add(date);
//                                tmp.add(day);
//                                tmp.add(month);
                            tmp.add(name);
                            tmp.add(domain);
                            tmp.add(regCount);
                            tmp.add(document.getId());
                            tmp.add(duration);
                            tmp.add(desc);

                            eventData.add(tmp);

                        }
                    } else {
                        Log.w("event", "Error getting documents.", task.getException());
                    }
//                    Log.d("eventadapter",eventData.toString());
                    setToEventAdapter();
                }
            });
    }

    private void setToEventAdapter() {

        eventModelArrayList = new ArrayList<>();
//        Log.d("eventadapter",eventData.toString());

        for(int i=0;i<eventData.size();i++){
            eventModelArrayList.add(new eventModel(eventData.get(i).get(0), eventData.get(i).get(1),eventData.get(i).get(2),eventData.get(i).get(3),rollno,eventData.get(i).get(4),isAdmin,eventData.get(i).get(5),eventData.get(i).get(6),username));
        }

//        Log.d("eventadapter",eventData.toString());

        EventAdapter EventAdapter = new EventAdapter(getActivity(), eventModelArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        eventRV.setLayoutManager(linearLayoutManager);
        eventRV.setAdapter(EventAdapter);
        
    }
}

