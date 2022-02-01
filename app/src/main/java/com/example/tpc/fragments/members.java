package com.example.tpc.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpc.Adapters.EventAdapter;
import com.example.tpc.Adapters.MembersAdapter;
import com.example.tpc.Adapters.epRSVPAdapter;
import com.example.tpc.Models.eventModel;
import com.example.tpc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class members extends Fragment {

    private View view;

    private EditText searchMember;
    private RecyclerView membersRV;
    private ArrayList<Vector<String>> membersData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_members, container, false);
        searchMember = view.findViewById(R.id.searchMember);
        membersRV = view.findViewById(R.id.membersRV);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        membersData = new ArrayList<>();
        fetchMembers();

        searchMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<Vector<String>> filteredlist = new ArrayList<>();

                // running a for loop to compare elements.
                for (Vector<String> item : membersData) {
                    // checking if the entered string matched with any item of our recycler view.
                    String t1=item.get(0).toLowerCase();
                    String t2=searchMember.getText().toString().toLowerCase();
                    if (t1.contains(t2)) {
                        filteredlist.add(item);
                    }else{
                        Log.d("testMemSearch",t1+" | "+t2);
                    }
                }
                if (filteredlist.isEmpty()) {
                    Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
                } else {
                    MembersAdapter memAdapter= new MembersAdapter(getActivity(), filteredlist);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    membersRV.setLayoutManager(linearLayoutManager);
                    membersRV.setAdapter(memAdapter);
                }
            }

            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void fetchMembers() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> data = document.getData();

                                String name = data.get("name").toString();
                                String rollno = data.get("roll").toString();
                                Vector<String> tmp = new Vector<String>();
                                tmp.add(name);
                                tmp.add(rollno);

                                membersData.add(tmp);

                            }
                            MembersAdapter adapter = new MembersAdapter(getActivity(), membersData);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            membersRV.setLayoutManager(mLayoutManager);
                            membersRV.setAdapter(adapter);

                        } else {
                            Log.w("event", "Error getting documents.", task.getException());
                        }

                    }
                });
    }

}
