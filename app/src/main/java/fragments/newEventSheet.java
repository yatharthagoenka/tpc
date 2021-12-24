package fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.kofigyan.stateprogressbar.StateProgressBar.StateNumber;

import java.util.HashMap;
import java.util.Map;

public class newEventSheet extends BottomSheetDialogFragment {

    View view;
    private TextView eventSheetHead;
    private ChipGroup eventTag;
    private EditText eventName,eventDesc,eventStart,eventDur;
    private FloatingActionButton eventNextButton;
    private StateProgressBar newEventProgressBar;
    int step;

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
        eventDesc = view.findViewById(R.id.eventDesc);
        eventDur = view.findViewById(R.id.eventDur);
        eventStart = view.findViewById(R.id.eventStart);
        eventTag = view.findViewById(R.id.eventTag);
        eventNextButton = view.findViewById(R.id.eventNextButton);
        newEventProgressBar = view.findViewById(R.id.newEventProgressBar);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> event = new HashMap<>();
        event.put("rsvp", 1);


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
                        eventName.animate().alpha(0f).translationY(-200f).setDuration(500);
                        eventDesc.animate().alpha(1f).translationX(-800f).setDuration(800);
                        step+=1;
                        newEventProgressBar.setCurrentStateNumber(StateNumber.TWO);
                        event.put("name", eventName.getText().toString());

                        break;
                    case 2:
                        eventSheetHead.setText("Duration");
                        eventDesc.animate().alpha(0f).translationY(-200f).setDuration(500);
                        eventDur.animate().alpha(1f).translationX(-800f).setDuration(800);
                        step+=1;
                        newEventProgressBar.setCurrentStateNumber(StateNumber.THREE);
                        event.put("Desc", eventDesc.getText().toString());
                        break;
                    case 3:
                        eventSheetHead.setText("Date");
                        eventDur.animate().alpha(0f).translationY(-200f).setDuration(500);
                        eventStart.animate().alpha(1f).translationX(-800f).setDuration(800);
                        step+=1;
                        newEventProgressBar.setCurrentStateNumber(StateNumber.FOUR);
                        event.put("duration", eventDur.getText().toString());

                        break;
                    case 4:
                        eventSheetHead.setText("Tags");
                        eventStart.animate().alpha(0f).translationY(-200f).setDuration(500);
                        eventTag.animate().alpha(1f).translationX(-850f).setDuration(800);
                        eventNextButton.animate().translationY(200f).setDuration(800);
                        step+=1;
                        newEventProgressBar.setCurrentStateNumber(StateNumber.FIVE);
                        event.put("startDate", eventStart.getText().toString());
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
}
