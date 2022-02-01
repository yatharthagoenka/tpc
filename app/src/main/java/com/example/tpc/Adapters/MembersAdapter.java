package com.example.tpc.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpc.R;
import com.example.tpc.fragments.profile;

import java.util.ArrayList;
import java.util.Vector;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.Viewholder> {

    private Context context;
    private ArrayList<Vector<String>> membersArrayList;

    public MembersAdapter(Context context, ArrayList<Vector<String>> membersArrayList) {
        this.context = context;
        this.membersArrayList = membersArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ep_users_card, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Vector<String> vec = membersArrayList.get(position);
        holder.epusers_username.setText(vec.get(0));
        holder.epusers_rollno.setText(vec.get(1));
        holder.epusers_tag.setText(String.valueOf(vec.get(0).charAt(0)));

        holder.user_detailpanel.setVisibility(View.GONE);

        holder.epuser_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Bundle bundle = new Bundle();
                bundle.putString("username",vec.get(0));
                bundle.putString("rollno",vec.get(1));
                bundle.putString("callingAct","membersPage");
                Fragment uprof = new profile();
                uprof.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentmember_container,uprof).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return membersArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView epusers_tag, epusers_username,epusers_rollno;
        private CardView epuser_cardview;
        private ConstraintLayout user_detailpanel;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            epusers_tag = itemView.findViewById(R.id.epusers_tag);
            epusers_username = itemView.findViewById(R.id.epusers_username);
            epusers_rollno = itemView.findViewById(R.id.epusers_rollno);
            epuser_cardview = itemView.findViewById(R.id.epuser_cardview);
            user_detailpanel = itemView.findViewById(R.id.user_detailpanel);
        }
    }

}
