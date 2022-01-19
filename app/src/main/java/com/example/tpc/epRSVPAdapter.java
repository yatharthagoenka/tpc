package com.example.tpc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Vector;

import fragments.profile;
import fragments.u_dashboard;

public class epRSVPAdapter extends RecyclerView.Adapter<epRSVPAdapter.Viewholder> {

    private Context context;
    private ArrayList<Vector<String>> epUsersArrayList;
    boolean expanded = false;


    public epRSVPAdapter(Context context, ArrayList<Vector<String>> epUsersArrayList) {
        this.context = context;
        this.epUsersArrayList = epUsersArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ep_users_card, parent, false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, @SuppressLint("RecyclerView") int position) {
        String[] details = epUsersArrayList.get(position).get(0).split("\\|");
        holder.epusers_username.setText(details[0]);
        holder.epusers_rollno.setText(details[1]);
//        Log.d("test22",epUsersArrayList.get(position).toString());
        holder.epusers_tag.setText(String.valueOf(details[0].charAt(0)));

        if(epUsersArrayList.get(position).get(3).equals("NO")){
            holder.epusers_infobutton.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.detailpanel_divider.setVisibility(View.GONE);
            holder.epusers_adminbutton.setVisibility(View.GONE);
            holder.epusers_adminbutton.setClickable(false);
        }else{
            holder.epusers_infobutton.getLayoutParams().width = 420;
            holder.epusers_adminbutton.setClickable(true);
            holder.detailpanel_divider.setVisibility(View.VISIBLE);
            holder.epusers_adminbutton.setVisibility(View.VISIBLE);

        }

        if(epUsersArrayList.get(position).get(1).equals("true")){
            holder.epusers_adminbutton.setText("REMOVE ADMIN");
            holder.epusers_tag.setBackgroundTintList(ContextCompat.getColorStateList(context.getApplicationContext(), R.color.green));
        }else{
            holder.epusers_adminbutton.setText("ADD ADMIN");
            holder.epusers_tag.setBackgroundTintList(ContextCompat.getColorStateList(context.getApplicationContext(), R.color.yellow));
        }
//        holder.epusers_tag.setText(details[0].charAt(0)+details[0].split(" ")[1].charAt(0));


        // Set the visibility based on state
        holder.user_detailpanel.setVisibility(expanded ? View.VISIBLE : View.GONE);

        holder.epuser_cardview.setOnClickListener(v -> {
            // Change the state
            expanded = !expanded;
            // Notify the adapter that item has changed
            notifyItemChanged(position);
        });

        holder.epusers_infobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent startIntent = new Intent(view.getContext(), profile.class);
//                startIntent.putExtra("username",details[0]);
//                startIntent.putExtra("rollno",details[1]);
//                view.getContext().startActivity(startIntent);
//                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.mock_anim);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Bundle bundle = new Bundle();
                bundle.putString("username",details[0]);
                bundle.putString("rollno",details[1]);
                bundle.putString("callingAct","eventPage");
                Fragment uprof = new profile();
                uprof.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.eventPageContainer,uprof).commit();
            }
        });

        holder.epusers_adminbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docref = db.collection("events").document(epUsersArrayList.get(position).get(2));
                if(epUsersArrayList.get(position).get(1).equals("false")){
                    docref.update("hosts", FieldValue.arrayUnion(details[1]));
                    Toast.makeText(context.getApplicationContext(), "Added"+ details[1] +" as Admin", Toast.LENGTH_SHORT).show();
                }
                else{
                    docref.update("hosts", FieldValue.arrayRemove(details[1]));
                    Toast.makeText(context.getApplicationContext(), "Removed"+ details[1] +" as Admin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView epusers_tag, epusers_username,epusers_rollno,epusers_infobutton,epusers_adminbutton;
        private ConstraintLayout user_detailpanel;
        private CardView epuser_cardview;
        private View detailpanel_divider;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            epusers_tag = itemView.findViewById(R.id.epusers_tag);
            epusers_username = itemView.findViewById(R.id.epusers_username);
            epusers_rollno = itemView.findViewById(R.id.epusers_rollno);
            epusers_infobutton = itemView.findViewById(R.id.epusers_infobutton);
            epusers_adminbutton = itemView.findViewById(R.id.epusers_adminbutton);
            user_detailpanel = itemView.findViewById(R.id.user_detailpanel);
            epuser_cardview = itemView.findViewById(R.id.epuser_cardview);
            detailpanel_divider = itemView.findViewById(R.id.detailpanel_divider);
        }
    }

    @Override
    public int getItemCount() {
        return epUsersArrayList.size();
    }
}
