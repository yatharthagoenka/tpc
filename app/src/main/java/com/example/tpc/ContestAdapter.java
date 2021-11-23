package com.example.tpc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpc.contestModel;

import java.util.ArrayList;

public class ContestAdapter extends RecyclerView.Adapter<ContestAdapter.Viewholder> {

    private Context context;
    private ArrayList<contestModel> contestModelArrayList;

    // Constructor
    public ContestAdapter(Context context, ArrayList<contestModel> contestModelArrayList) {
        this.context = context;
        this.contestModelArrayList = contestModelArrayList;
    }

    @NonNull
    @Override
    public ContestAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_card, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContestAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        contestModel model = contestModelArrayList.get(position);
        holder.contestName.setText(model.getContestName());
        holder.contestStart.setText(model.getContestStart());
        holder.contestDuration.setText(model.getContestDuration());

        if(model.getContestPlatform()=="Codechef"){
            holder.contestCardTopBar.setBackgroundColor(Color.parseColor("#64d1b8"));
        }else if(model.getContestPlatform()=="Codeforces"){
            holder.contestCardTopBar.setBackgroundColor(Color.parseColor("#f6ff75"));
        }else{
            holder.contestCardTopBar.setBackgroundColor(Color.parseColor("#e785ff"));
        }

        holder.cardlayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final String website = model.getContestLink();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                context.startActivity(browserIntent);
            }
        });


    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return contestModelArrayList.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView contestName, contestStart,contestDuration;
        private RelativeLayout contestCardTopBar,cardlayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            contestName = itemView.findViewById(R.id.contestName);
            contestStart = itemView.findViewById(R.id.contestStart);
            contestDuration = itemView.findViewById(R.id.contestDuration);
            contestCardTopBar = itemView.findViewById(R.id.contestCardTopBar);
            cardlayout = itemView.findViewById(R.id.cardlayout);
        }
    }
}
