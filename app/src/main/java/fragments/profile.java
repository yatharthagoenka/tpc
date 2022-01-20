package fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpc.R;
import com.example.tpc.User;
import com.example.tpc.adminindex;
import com.example.tpc.eventPage;
import com.example.tpc.index;
import com.example.tpc.register;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class profile extends Fragment {

    private TextView profName,profRollNo;
    private ImageView profBackButton,profDP;


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

        currUserProfile();

        fetchedRoll = getArguments().getString("rollno");
        fetchUserProfile();

        if(getArguments().getString("callingAct").equals("index") || getArguments().getString("callingAct").equals("adminindex")){
            profBackButton.setVisibility(View.GONE);

        }else{
            profBackButton.setVisibility(View.VISIBLE);
            profBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i;
                    if(isAdmin.equals("YES")){
                        i = new Intent(getActivity(), adminindex.class);
                    }else{
                        i = new Intent(getActivity(), index.class);
                    }
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
//        if (acct != null) {
//            Uri personPhoto = acct.getPhotoUrl();
//            new ImageLoadTask(personPhoto.toString(), profDP).execute();
//        }

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
                    if (document.exists()) {
                        Map<String,Object> data = document.getData();
                        String username = data.get("name").toString();
                        profName.setText(username);
                        new ImageLoadTask(data.get("userDP").toString(), profDP).execute();
                    } else {
                        Log.d("fetchedUser", "No such document");
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

}