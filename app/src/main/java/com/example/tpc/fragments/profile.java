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
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpc.Adapters.epRSVPAdapter;
import com.example.tpc.Client.RatingClient;
import com.example.tpc.Models.cfUserRating;
import com.example.tpc.Models.cfUserRatingResult;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class profile extends Fragment {

    private TextView profName,profRollNo;
    private ImageView profBackButton,profDP,gitButton,linkedinButton,cfButton,editProfileButton;
    private GraphView cfRatingGraph;
    private Button profileUpdateButton,profileDeleteButton;

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
        cfButton = view.findViewById(R.id.cfButton);
        cfRatingGraph = view.findViewById(R.id.cfRatingGraph);
        profileUpdateButton = view.findViewById(R.id.profileUpdateButton);
        profileDeleteButton = view.findViewById(R.id.profileDeleteButton);

        user = FirebaseAuth.getInstance().getCurrentUser();

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

        profileUpdateButton.setOnClickListener(new View.OnClickListener() {
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

        profileDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete Account")
                        .setMessage("\nThis action is irreversible. Are you sure you want to delete the account?")
                        .setIcon(R.drawable.app_icon)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("userDelete", "User account deleted.");
                                        }
                                    }
                                });
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
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

        cfButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(fetchedRoll.trim().equals(rollno.trim())){
                    Toast.makeText(getActivity(), sh.getString("codeforces", "/"), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Unauthorized access", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        cfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                getCFRating(sh.getString("codeforces", "aaa"));

                }catch (Exception e){
                    Log.d("URHE",e.toString());
                }
            }
        });
    }

    void getCFRating(String userID){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://codeforces.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RatingClient ratingClient = retrofit.create(RatingClient.class);

        Call<cfUserRating> call = ratingClient.getUserRating(userID);
        call.enqueue(new Callback<cfUserRating>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<cfUserRating> call, Response<cfUserRating> response) {
                if(!response.isSuccessful()){
//                    Toast.makeText(getActivity(), "Failed with code: "+response.code(), Toast.LENGTH_SHORT).show();
                    Log.d("onFailureRetro","Failed with code: "+response.code());
                }
                cfUserRating cfUserRatings = response.body();
                List<cfUserRatingResult> cfUserRatingResults = cfUserRatings.getResult();
                if(cfUserRatingResults.size()<1){
                    Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                    return;
                }
//                ArrayList<Integer> ratingHistory = new ArrayList<>();
                try{
                DataPoint[] dataPoints = new DataPoint[cfUserRatingResults.size()];

                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                        .withLocale( Locale.US )
                        .withZone( ZoneId.systemDefault() );
                int i = 0,currRating=0;
                double de=0.0,ds=0.0;
                for(cfUserRatingResult result: cfUserRatingResults){
//                    ratingHistory.add(result.getNewRating());

                    Instant instant = Instant.ofEpochSecond(result.getRatingUpdateTimeSeconds());

                    String tmp_start = formatter.format( instant );
                    String[] ts1 = (tmp_start.split(",")[0]).split("/");
                    if(Integer.parseInt(ts1[1])<10){
                        ts1[1]="0"+ts1[1];
                    }
                    if(Integer.parseInt(ts1[0])<10){
                        ts1[0]="0"+ts1[0];
                    }
                    String start = ts1[2]+ts1[0]+ts1[1];

                    dataPoints[i] = new DataPoint(i, (double)result.getNewRating());
                    if(i==cfUserRatingResults.size()-1) {
                        de = Double.parseDouble(start);
                        currRating = result.getNewRating();
                    }
                    if(i==0) ds=Double.parseDouble(start);
                    i+=1;
                }
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                    cfRatingGraph.setAlpha(1);
                    cfRatingGraph.setTitle(userID + " Rating History ("+ String.valueOf(currRating) +")");
                    cfRatingGraph.setTitleColor(R.color.purple_200);
                    cfRatingGraph.setTitleTextSize(28);
                    cfRatingGraph.addSeries(series);
                }catch (Exception e){
                    Log.d("usergraph",e.toString());
                }

//                Log.d("historyRat",ratingHistory.toString());
            }

            @Override
            public void onFailure(Call<cfUserRating> call, Throwable t) {
                Log.d("onFailureRetro",t.getMessage());
            }
        });
    }

    void currUserProfile(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(),gso);


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
                            updateSP("codeforces",data.get("codeforces").toString());
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