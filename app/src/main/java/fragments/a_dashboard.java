package fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tpc.EventAdapter;
import com.example.tpc.R;
import com.example.tpc.User;
import com.example.tpc.contestModel;
import com.example.tpc.eventModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class a_dashboard extends Fragment {

    View view;
    private TextView dash_username, newEventButton,eventtexts;
    private Button filterbutton;
    private ImageView dash_profilepic;
    private LinearLayout all_chipdash,cp_chipdash,web_chipdash,app_chipdash,ai_chipdash;

    private String userID,isAdmin,rollno;
    private FirebaseUser user;
    private DatabaseReference reference;
    private GoogleSignInClient mGoogleSignInClient;

    private RecyclerView eventRV;
    private ArrayList<eventModel> eventModelArrayList;
    private Vector<Vector<String>> eventData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_a_dashboard, container, false);
        dash_username = view.findViewById(R.id.dash_username);
        filterbutton = view.findViewById(R.id.filterbutton);
        dash_profilepic = view.findViewById(R.id.dash_profilepic);

        eventRV = view.findViewById(R.id.eventRV);
        eventData = new Vector<Vector<String>>();
        readEventData();

        all_chipdash = view.findViewById(R.id.all_chipdash);
        cp_chipdash = view.findViewById(R.id.cp_chipdash);
        web_chipdash = view.findViewById(R.id.web_chipdash);
        app_chipdash = view.findViewById(R.id.app_chipdash);
        ai_chipdash = view.findViewById(R.id.ai_chipdash);


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

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            Uri personPhoto = acct.getPhotoUrl();
            new ImageLoadTask(personPhoto.toString(), dash_profilepic).execute();
        }

        newEventButton = view.findViewById(R.id.newEventButton);
        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEventSheet neweventsheet = new newEventSheet();
                neweventsheet.show(getActivity().getSupportFragmentManager(), neweventsheet.getTag());
            }
        });

        return view;
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

                            String[] full_date = data.get("eventDate").toString().split(" ");
                            String day = full_date[0];
                            String month = full_date[1];
                            String name = data.get("name").toString();
                            String domain = data.get("domain").toString();
                            String regCount = data.get("regCount").toString()+" Joined";

                            Vector<String> tmp = new Vector<String>();
                            tmp.add(day);
                            tmp.add(month);
                            tmp.add(name);
                            tmp.add(domain);
                            tmp.add(regCount);
                            tmp.add(document.getId());

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
            eventModelArrayList.add(new eventModel(eventData.get(i).get(0), eventData.get(i).get(1), eventData.get(i).get(2),eventData.get(i).get(3),eventData.get(i).get(4),rollno,eventData.get(i).get(5),isAdmin));
        }

//        Log.d("eventadapter",eventData.toString());

        EventAdapter EventAdapter = new EventAdapter(getActivity(), eventModelArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        eventRV.setLayoutManager(linearLayoutManager);
        eventRV.setAdapter(EventAdapter);
        
    }
}

