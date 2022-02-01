package com.example.tpc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tpc.Adapters.ContestAdapter;
import com.example.tpc.R;
import com.example.tpc.Models.contestModel;
import com.example.tpc.ViewModels.contestViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Vector;


public class contests extends Fragment implements LifecycleOwner {

    View view;
//    private TextView contestlisttest;
    private int tab;
    private Vector<Vector<String>> resultdata_All,resultdata_AC,resultdata_CF,resultdata_CC;
//    ChipNavigationBar chipNavigationBar;
    private FloatingActionButton floatingActionButton;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;

    private contestViewModel contestViewModel;

    private RecyclerView contestRV;
    private ArrayList<contestModel> contestModelArrayList;

    boolean f=true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contests, container, false);
//        chipNavigationBar = view.findViewById(R.id.contest_menubar);
//        chipNavigationBar.setItemSelected(R.id.contestmenu_all,true);
        contestRV = view.findViewById(R.id.contestRV);
//        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.a_refreshLayout);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        return view;
    }


    private Observer<ArrayList<contestModel>> contestListUpdateObserver = new Observer<ArrayList<contestModel>>() {
        @Override
        public void onChanged(ArrayList<contestModel> userArrayList) {
            ContestAdapter contestAdapter = new ContestAdapter(requireActivity(), userArrayList);
            linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            contestRV.setLayoutManager(linearLayoutManager);
            contestRV.setAdapter(contestAdapter);
            progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contestViewModel = new ViewModelProvider(getActivity()).get(contestViewModel.class);
        contestViewModel.getContestMutableLiveData().observe(getViewLifecycleOwner(), contestListUpdateObserver);


    }

//        tab = 1;

//        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onItemSelected(int i) {
//                Fragment fragment = null;
//                switch (i) {
//                    case R.id.contestmenu_all:
//                        tab = 1;
////                        setToAdapter();
//                        break;
//                    case R.id.contestmenu_cc:
//                        tab = 2;
////                        setToAdapter();
//                        break;
//                    case R.id.contestmenu_cf:
//                        tab = 3;
////                        setToAdapter();
//                        break;
//                    case R.id.contestmenu_ac:
//                        tab = 4;
////                        setToAdapter();
//                        break;
//                }
//            }
//        });

//        swipeRefreshLayout.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        switch(tab){
//                            case 1:
//                                resultdata_All.clear();
//                                progressBar.setVisibility(View.VISIBLE);
//                                all_contest_fetch();
//                                break;
//                            case 2:
//                                resultdata_CC.clear();
//                                progressBar.setVisibility(View.VISIBLE);
//                                CC_list cclist = new CC_list();
//                                cclist.execute("https://www.codechef.com/api/list/contests/all?sort_by=START&sorting_order=asc&offset=0&mode=premium");
//                                break;
//                            case 3:
//                                resultdata_CF.clear();
//                                progressBar.setVisibility(View.VISIBLE);
//                                CF_list cflist = new CF_list();
//                                cflist.execute("https://codeforces.com/api/contest.list?gym=false");
//                                break;
//                            case 4:
//                                resultdata_AC.clear();
//                                progressBar.setVisibility(View.VISIBLE);
//                                AC_list aclist = new AC_list();
//                                aclist.execute();
//                                break;
//                        }
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                }
//        );

//    void setToAdapter(){
//        Vector<Vector<String>> resultdata = new Vector<Vector<String>>();
//        switch(tab){
//            case 1:
//                resultdata = (Vector<Vector<String>>) resultdata_All.clone();
//                break;
//            case 2:
//                resultdata = (Vector<Vector<String>>) resultdata_CC.clone();
//                break;
//            case 3:
//                resultdata = (Vector<Vector<String>>) resultdata_CF.clone();
//                break;
//            case 4:
//                resultdata = (Vector<Vector<String>>) resultdata_AC.clone();
//                break;
//        }
//        Collections.sort(resultdata, new Comparator<Vector<String>>(){
//            @Override  public int compare(Vector<String> v1, Vector<String> v2) {
//                return v1.get(0).compareTo(v2.get(0)); //If you order by 2nd element in row
//            }});
//
//
//        contestModelArrayList = new ArrayList<>();
//
//        for(int i=0;i<resultdata.size();i++){
//            contestModelArrayList.add(new contestModel(resultdata.get(i).get(2), resultdata.get(i).get(3), resultdata.get(i).get(4),resultdata.get(i).get(5),resultdata.get(i).get(1)));
//        }
//        ContestAdapter contestAdapter = new ContestAdapter(getActivity(), contestModelArrayList);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        contestRV.setLayoutManager(linearLayoutManager);
//        contestRV.setAdapter(contestAdapter);
//        progressBar.setVisibility(View.GONE);
//    }



}