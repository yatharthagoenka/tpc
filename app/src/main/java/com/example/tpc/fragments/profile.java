package com.example.tpc.fragments;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpc.Adapters.epRSVPAdapter;
import com.example.tpc.R;
import com.example.tpc.Models.User;
import com.example.tpc.adminindex;
import com.example.tpc.eventPage;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class profile extends Fragment {

    private TextView profName,profRollNo;
    private ImageView profBackButton,profDP,gitButton,linkedinButton,instaButton,editProfileButton;


    private String userID,isAdmin,rollno,username,fetchedRoll;
    private FirebaseUser user;
    private DatabaseReference reference,fetchRef;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profBackButton = view.findViewById(R.id.profBackButton);
        profDP = view.findViewById(R.id.profDP);
        profName = view.findViewById(R.id.profName);
        profRollNo = view.findViewById(R.id.profRollNo);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        gitButton = view.findViewById(R.id.gitButton);
        linkedinButton = view.findViewById(R.id.linkedinButton);
        instaButton = view.findViewById(R.id.instaButton);

        if(getArguments().getString("callingAct").equals("index") || getArguments().getString("callingAct").equals("adminindex")){
            profBackButton.setVisibility(View.GONE);

        }else{
            profBackButton.setVisibility(View.VISIBLE);
            profBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(),adminindex.class);
                    i.putExtra("isAdmin",isAdmin);
                    startActivity(i);
                }
            });
        }

        try{
            eventPage.popupWindow.dismiss();
        }catch (Exception e){
            // NO ACTION
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sh = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);

        rollno = sh.getString("rollno", "/");
        if(rollno.equals("/")){
//            Log.i("userSP","Fetching data from server");
            currUserProfile();
        }else{
//            Log.i("userSP","Fetching data from SP");
            username = sh.getString("username","");
        }

        fetchedRoll = getArguments().getString("rollno");
        fetchUserProfile();

        if(fetchedRoll.trim().equals(rollno.trim())){
            editProfileButton.setVisibility(View.VISIBLE);
        }else{
            editProfileButton.setVisibility(View.GONE);
        }

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View editProfView = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile, null);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;

//                    ep_userlistRV = (RecyclerView) popupView.findViewById(R.id.epusers_rv);


                PopupWindow popupProfile = new PopupWindow(editProfView, width, height, true);
                popupProfile.setAnimationStyle(R.style.popUpAnimation);
                popupProfile.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });


        gitButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(fetchedRoll.trim().equals(rollno.trim())){
                    Toast.makeText(getActivity(), "Edit Git", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Unauthorized access", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        linkedinButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(fetchedRoll.trim().equals(rollno.trim())){
                    Toast.makeText(getActivity(), "Edit Linkedin", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Unauthorized access", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        instaButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(fetchedRoll.trim().equals(rollno.trim())){
                    Toast.makeText(getActivity(), "Edit Instagram", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Unauthorized access", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    void currUserProfile(){
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
                    isAdmin = userProfile.isAdmin;
                    rollno = userProfile.roll;

                    updateSP("username",username);
                    updateSP("isAdmin",isAdmin);
                    updateSP("rollno",rollno);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    void updateSP(String setKey, String setValue){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editData = sharedPreferences.edit();
        editData.putString(setKey, setValue);
        editData.apply();
    }

    public void shareEditProfile(String name,String rollno) {
//        spin=id.trim();
//        sdate=date.trim();
    }

    void fetchUserProfile(){

        profRollNo.setText(fetchedRoll);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(fetchedRoll.trim());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d("testuserfet",document.toString());
                    if (document.exists()) {
                        Map<String,Object> data = document.getData();
                        String username = data.get("name").toString();
                        profName.setText(username);
                        new ImageLoadTask(data.get("userDP").toString(), profDP).execute();

                        if(fetchedRoll.trim().equals(rollno.trim())){
                            Log.i("userSPF","Setting socials");
                            updateSP("github",data.get("github").toString());
                            updateSP("linkedin",data.get("linkedin").toString());
                            updateSP("instagram",data.get("instagram").toString());
                        }
                    } else {
                        Log.d("fetchedUser", "No such document");
                    }
                } else {
                    Log.d("fetchedUser", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sh = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        rollno = sh.getString("rollno","/");
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

}