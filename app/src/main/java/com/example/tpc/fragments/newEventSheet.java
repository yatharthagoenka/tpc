package com.example.tpc.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpc.R;
import com.example.tpc.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.kofigyan.stateprogressbar.StateProgressBar.StateNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class newEventSheet extends BottomSheetDialogFragment implements AdapterView.OnItemSelectedListener {

    View view;
    private TextView eventSheetHead;
    private Spinner eventDomain;
    private ChipGroup eventTag;
    private EditText eventName,eventDesc,eventStart,eventDur;
    private FloatingActionButton eventNextButton;
    private StateProgressBar newEventProgressBar;
    private ScrollView tagscrollview;
    private DatePicker eventDate;

    private String userID,rollno,username;
    private FirebaseUser user;
    private DatabaseReference reference;

    Map<String, Object> event;
    int step;
    String eventDomainText;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public newEventSheet() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static newEventSheet newInstance(String param1, String param2) {
        newEventSheet fragment = new newEventSheet();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_event_sheet, container, false);
        eventSheetHead = view.findViewById(R.id.eventSheetHead);
        eventName = view.findViewById(R.id.eventName);

        eventDomain = view.findViewById(R.id.eventDomain);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.eventdomain_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventDomain.setAdapter(adapter);
        eventDomain.setOnItemSelectedListener(this);

        eventDesc = view.findViewById(R.id.eventDesc);
        eventDur = view.findViewById(R.id.eventDur);
//        eventStart = view.findViewById(R.id.eventStart);
        eventTag = view.findViewById(R.id.eventTag);
        eventNextButton = view.findViewById(R.id.eventNextButton);
        newEventProgressBar = view.findViewById(R.id.newEventProgressBar);
        tagscrollview = view.findViewById(R.id.tagscrollview);
        eventDate = view.findViewById(R.id.eventDate);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    rollno = userProfile.roll;
                    username = userProfile.name;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        event = new HashMap<>();
        ArrayList<String> attendees = new ArrayList<>();
        ArrayList<String> hosts = new ArrayList<>();
        attendees.add(username + " | "+rollno);
        hosts.add(rollno);
        event.put("rsvp", attendees);
        event.put("hosts", hosts);
        event.put("regCount", 1);

        step = 1;
        eventNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tags="";
                switch(step){
                    case 0:
                        event.put("name", eventName.getText().toString());
                        step+=1;
                        break;
                    case 1:
                        eventSheetHead.setText("Description");
                        eventName.animate().alpha(0f).translationY(-800f).setDuration(500);
                        eventDomain.animate().alpha(0f).translationY(-800f).setDuration(500);
                        eventDesc.animate().alpha(1f).translationX(-800f).setDuration(800);
                        step+=1;
                        newEventProgressBar.setCurrentStateNumber(StateNumber.TWO);
                        event.put("name", eventName.getText().toString());

                        break;
                    case 2:
                        eventSheetHead.setText("Duration");
                        eventDesc.animate().alpha(0f).translationY(-800f).setDuration(500);
                        eventDur.animate().alpha(1f).translationX(-800f).setDuration(800);
                        step+=1;
                        newEventProgressBar.setCurrentStateNumber(StateNumber.THREE);
                        event.put("Desc", eventDesc.getText().toString());
                        break;
                    case 3:
                        eventSheetHead.setText("Date");
                        eventDur.animate().alpha(0f).translationY(-800f).setDuration(500);
                        eventDate.animate().alpha(1f).translationX(-800f).setDuration(800);
                        eventNextButton.animate().translationY(130f).setDuration(800);
                        step+=1;
                        newEventProgressBar.setCurrentStateNumber(StateNumber.FOUR);
                        event.put("duration", eventDur.getText().toString());
                        break;
                    case 4:
                        eventSheetHead.setText("Tags");
                        tagscrollview.animate().alpha(1f).translationX(-850f).setDuration(800);
                        eventDate.animate().alpha(0f).translationY(-200f).setDuration(500);
                        eventTag.animate().alpha(1f).translationX(-10f).setDuration(800);
//                        eventNextButton.animate().translationY(130f).setDuration(800);
                        step+=1;
                        newEventProgressBar.setCurrentStateNumber(StateNumber.FIVE);

                        int day = eventDate.getDayOfMonth();
                        String day_str="";
                        if(day<10){
                            day_str="0"+String.valueOf(day);
                        }
                        int month = eventDate.getMonth();
                        String mon="";
                        int year = eventDate.getYear();
                        if(month==0) mon="Jan";
                        else if(month==1) mon="Feb";
                        else if(month==2) mon="Mar";
                        else if(month==3) mon="Apr";
                        else if(month==4) mon="May";
                        else if(month==5) mon="Jun";
                        else if(month==6) mon="Jul";
                        else if(month==7) mon="Aug";
                        else if(month==8) mon="Sep";
                        else if(month==9) mon="Oct";
                        else if(month==10) mon="Nov";
                        else if(month==11) mon="Dec";
                        String date = day_str+" "+mon+" "+String.valueOf(year);
                        event.put("eventDate", date);
                        break;
                    case 5:
                        for (int i=0; i<eventTag.getChildCount();i++){
                            Chip chip = (Chip)eventTag.getChildAt(i);
//                            Log.i("outside if ", i+ " chip = " + chip.getText().toString());
                            if (chip.isChecked()){
                                tags += " " + chip.getText().toString();
                            }
                        }
                        event.put("tags", tags);
                        db.collection("events")
                            .add(event)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getActivity(), "Event added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Error adding event", Toast.LENGTH_SHORT).show();
                                }
                            });
                        break;
                }

            }
        });


        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        eventDomainText = adapterView.getItemAtPosition(i).toString();
        event.put("domain", eventDomainText);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

