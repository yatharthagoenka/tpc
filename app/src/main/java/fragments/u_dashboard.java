package fragments;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tpc.EventAdapter;
import com.example.tpc.eventChange;
import com.example.tpc.R;
import com.example.tpc.User;
import com.example.tpc.eventModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class u_dashboard extends Fragment {
    View view;
    private TextView dash_username;
    private Button filterbutton;
    private ImageView dash_profilepic;
    private EditText searchbox;

    private String userID,isAdmin,rollno;
    private FirebaseUser user;
    private DatabaseReference reference;
    private GoogleSignInClient mGoogleSignInClient;

    private LinearLayout all_chipdash,cp_chipdash,web_chipdash,app_chipdash,ai_chipdash;


    private RecyclerView eventRV;
    private ArrayList<eventModel> eventModelArrayList;
    private Vector<Vector<String>> eventData;
    int f;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_u_dashboard, container, false);
        dash_profilepic = view.findViewById(R.id.dash_profilepic);
        dash_username = view.findViewById(R.id.dash_username);
        filterbutton = view.findViewById(R.id.filterbutton);
        searchbox = view.findViewById(R.id.searchbox);


//        getActivity().stopService(new Intent(getActivity(), eventChange.class));

//        Intent startIntent = new Intent(getActivity(), eventChange.class);
//        startIntent.setAction("te");
//        getActivity().startService(startIntent);

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
                    String username = userProfile.name;
                    dash_username.setText(username);
                    isAdmin = userProfile.isAdmin;
                    rollno = userProfile.roll;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        eventRV = view.findViewById(R.id.eventRV_user);
        eventData = new Vector<Vector<String>>();
        readEventData();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            Uri personPhoto = acct.getPhotoUrl();
            new ImageLoadTask(personPhoto.toString(), dash_profilepic).execute();
        }

        return view;
    }

    private boolean isServiceActive(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
                                f+=1;
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

    public int getVar() {
        Log.d("testf",String.valueOf(f));
        return this.f;
    }

    private void setToEventAdapter() {

        eventModelArrayList = new ArrayList<>();
//        Log.d("eventadapter",eventData.toString());

        for(int i=0;i<eventData.size();i++){
            eventModelArrayList.add(new eventModel(eventData.get(i).get(0), eventData.get(i).get(1),eventData.get(i).get(2),eventData.get(i).get(3),rollno,eventData.get(i).get(4),isAdmin,eventData.get(i).get(5),eventData.get(i).get(6)));
        }

//        Log.d("eventadapter",eventData.toString());

        EventAdapter EventAdapter = new EventAdapter(getActivity(), eventModelArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        eventRV.setLayoutManager(linearLayoutManager);
        eventRV.setAdapter(EventAdapter);

    }
}