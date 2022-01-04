package com.example.tpc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.Viewholder>{

    private Context context;
    private ArrayList<eventModel> eventModelArrayList;

    public EventAdapter(Context context, ArrayList<eventModel> eventModelArrayList) {
        this.context = context;
        this.eventModelArrayList = eventModelArrayList;
    }

    @NonNull
    @Override
    public EventAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new Viewholder(view);
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView eventDay, eventMonth,eventName,eventDomain,eventRegCount;
        private ConstraintLayout eventcard_layout;
        private ImageView eventcard_img;
        private FloatingActionButton rsvpbutton;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            eventDay = itemView.findViewById(R.id.eventcard_day);
            eventMonth = itemView.findViewById(R.id.eventcard_month);
            eventName = itemView.findViewById(R.id.eventcard_name);
            eventDomain = itemView.findViewById(R.id.eventcard_domain);
            eventRegCount = itemView.findViewById(R.id.eventcard_reg);
            eventcard_layout = itemView.findViewById(R.id.eventcard_layout);
            eventcard_img = itemView.findViewById(R.id.eventcard_img);
            rsvpbutton = itemView.findViewById(R.id.rsvpbutton);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.Viewholder holder, int position) {
        eventModel model = eventModelArrayList.get(position);
        holder.eventDay.setText(model.getEventDay());
        holder.eventMonth.setText(model.getEventMonth());
        holder.eventName.setText(model.getEventName());
        holder.eventDomain.setText(model.getEventDomain());
        holder.eventRegCount.setText(model.getEventRegCount());

        if(model.getEventDomain().equals("Web Development")){
            holder.eventcard_img.setImageResource(R.drawable.web_cardview);
        }else if(model.getEventDomain().equals("App Development")){
            holder.eventcard_img.setImageResource(R.drawable.android_cardview);
        }else if(model.getEventDomain().equals("Club Session")){
            holder.eventcard_img.setImageResource(R.drawable.session_cardview);
        }else if(model.getEventDomain().equals("AI/ML")){
            holder.eventcard_img.setImageResource(R.drawable.ai_cardview);
        }else{
            holder.eventcard_img.setImageResource(R.drawable.cp_cardview);
        }

        holder.eventcard_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(model.getIsAdmin().equals("YES")){
                    new AlertDialog.Builder(view.getContext())
                        .setTitle(" "+model.getEventName())
                        .setMessage("\nConfirm deleting the selected event?")
                        .setIcon(R.drawable.app_icon)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference docref = db.collection("events").document(model.getDocID());
                                docref.delete();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                }

                return true;
            }
        });

        holder.rsvpbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(),model.getEventName(),Toast.LENGTH_SHORT).show();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docref = db.collection("events").document(model.getDocID());
                docref.update("rsvp", FieldValue.arrayUnion(model.getCurrRollno()));

                docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String,Object> data = document.getData();
                            docref.update("regCount", ((ArrayList<?>) data.get("rsvp")).size());
                            Toast.makeText(v.getContext(), "RSVP Confirmed", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("Event Data", "got failed with ", task.getException());
                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return eventModelArrayList.size();
    }
}
