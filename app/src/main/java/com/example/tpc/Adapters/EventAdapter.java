package com.example.tpc.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpc.Models.eventModel;
import com.example.tpc.R;
import com.example.tpc.eventPage;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            eventDay = itemView.findViewById(R.id.eventcard_day);
            eventMonth = itemView.findViewById(R.id.eventcard_month);
            eventName = itemView.findViewById(R.id.eventcard_name);
            eventDomain = itemView.findViewById(R.id.eventcard_domain);
            eventRegCount = itemView.findViewById(R.id.eventcard_reg);
            eventcard_layout = itemView.findViewById(R.id.eventcard_layout);
            eventcard_img = itemView.findViewById(R.id.eventcard_img);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.Viewholder holder, int position) {
        eventModel model = eventModelArrayList.get(position);
        holder.eventDay.setText(model.getEventDate().split(" ")[0]);
        holder.eventMonth.setText(model.getEventDate().split(" ")[1]);
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

        final View sharedElPic = holder.eventcard_img;
        final View sharedElName = holder.eventName;

        holder.eventcard_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(view.getContext(), eventPage.class);
                startIntent.putExtra("domain",model.getEventDomain());
                startIntent.putExtra("name",model.getEventName());
                startIntent.putExtra("date",model.getEventDate());
                startIntent.putExtra("duration",model.getEventDuration());
                startIntent.putExtra("docID",model.getDocID());
                startIntent.putExtra("currUserRoll",model.getCurrRollno());
                startIntent.putExtra("desc",model.getEventDesc());
                startIntent.putExtra("currUsername",model.getCurrUsername());
                startIntent.putExtra("isAdmin",model.getIsAdmin());
                view.getContext().startActivity(startIntent);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.mock_anim);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventModelArrayList.size();
    }
}
