package fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tpc.ContestAdapter;
import com.example.tpc.R;
import com.example.tpc.contestModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import static java.lang.StrictMath.abs;


public class a_contests extends Fragment {

    View view;
//    private TextView contestlisttest;
    private int tab;
    private Vector<Vector<String>> resultdata_All,resultdata_AC,resultdata_CF,resultdata_CC;
    ChipNavigationBar chipNavigationBar;
    private FloatingActionButton floatingActionButton;
    private ProgressBar progressBar;

    private RecyclerView contestRV;
    private ArrayList<contestModel> contestModelArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_a_contests, container, false);
//        contestlisttest = view.findViewById(R.id.contestlisttest);
        chipNavigationBar = view.findViewById(R.id.contest_menubar);
        chipNavigationBar.setItemSelected(R.id.contestmenu_all,true);
        contestRV = view.findViewById(R.id.contestRV);
        floatingActionButton = view.findViewById(R.id.refreshContestButton);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        all_contest_fetch();

        resultdata_All = new Vector<Vector<String>>();
        resultdata_CF = new Vector<Vector<String>>();
        resultdata_CC = new Vector<Vector<String>>();
        resultdata_AC = new Vector<Vector<String>>();

        tab = 1;

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.contestmenu_all:
                        tab = 1;
                        setToAdapter();
                        break;
                    case R.id.contestmenu_cc:
                        tab = 2;
                        setToAdapter();
                        break;
                    case R.id.contestmenu_cf:
                        tab = 3;
                        setToAdapter();
                        break;
                    case R.id.contestmenu_ac:
                        tab = 4;
                        setToAdapter();
                        break;
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(tab){
                    case 1:
                        resultdata_All.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        all_contest_fetch();
                        break;
                    case 2:
                        resultdata_CC.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        CC_list cclist = new CC_list();
                        cclist.execute("https://www.codechef.com/api/list/contests/all?sort_by=START&sorting_order=asc&offset=0&mode=premium");
                        break;
                    case 3:
                        resultdata_CF.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        CF_list cflist = new CF_list();
                        cflist.execute("https://codeforces.com/api/contest.list?gym=false");
                        break;
                    case 4:
                        resultdata_AC.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        AC_list aclist = new AC_list();
                        aclist.execute();
                        break;
                }
            }
        });

        return view;
    }

    public void all_contest_fetch(){
        CC_list cclist2 = new CC_list();
        cclist2.execute("https://www.codechef.com/api/list/contests/all?sort_by=START&sorting_order=asc&offset=0&mode=premium");
        CF_list cflist2 = new CF_list();
        cflist2.execute("https://codeforces.com/api/contest.list?gym=false");
        AC_list aclist2 = new AC_list();
        aclist2.execute();
    }


    public class CC_list extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url=new URL(urls[0]);
                httpURLConnection=(HttpURLConnection)url.openConnection();
                InputStream in=httpURLConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char curr=(char)data;
                    result+=curr;
                    if(result.length()>=18){
                        if(result.substring(result.length()-18,result.length()-1).equals("practice_contests")) {
                            result+=":[]}";
                            break;
                        }
                    }
                    data=reader.read();
                }
//                Log.d("cc result",result);
                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj=new JSONObject(s);
                JSONArray cc_future = obj.getJSONArray("future_contests");
//                Log.i("CC Future Contests", String.valueOf(cc_future));

                for (int i = 0; i < cc_future.length(); i++) {
                    JSONObject c = cc_future.getJSONObject(i);
                    String id = c.getString("contest_code");
                    String platform = "Codechef";
                    String name = c.getString("contest_name");

                    String tmp_start = c.getString("contest_start_date");
                    String[] ts = tmp_start.split(" ");
                    String ts_v;
                    if(ts[1]=="Jan") ts_v="01";
                    else if(ts[1]=="Feb") ts_v="02";
                    else if(ts[1]=="Mar") ts_v="03";
                    else if(ts[1]=="Apr") ts_v="04";
                    else if(ts[1]=="May") ts_v="05";
                    else if(ts[1]=="Jun") ts_v="06";
                    else if(ts[1]=="Jul") ts_v="07";
                    else if(ts[1]=="Aug") ts_v="08";
                    else if(ts[1]=="Sep") ts_v="09";
                    else if(ts[1]=="Oct") ts_v="10";
                    else if(ts[1]=="Nov") ts_v="11";
                    else ts_v="12";
                    String start = ts[0]+"-"+ts_v+"-"+ts[2]+" | "+ts[4];
                    String date_compare = ts[2]+ts_v+ts[0]+ts[4];

                    String duration = String.valueOf(Integer.parseInt(c.getString("contest_duration"))/60)+" H";
                    String link = "https://www.codechef.com/"+id+"?itm_campaign=contest_listing";
                    Vector<String> tmp = new Vector<String>();
                    tmp.add(date_compare);
                    tmp.add(platform);
                    tmp.add(name);
                    tmp.add(start);
                    tmp.add(duration);
                    tmp.add(link);
                        resultdata_All.add(tmp);
                        resultdata_CC.add(tmp);
                }
//                contestlisttest.setText(String.valueOf(resultdata));
                setToAdapter();
//                Log.d("cc future", String.valueOf(result));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public class CF_list extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url=new URL(urls[0]);
                httpURLConnection=(HttpURLConnection)url.openConnection();
                InputStream in=httpURLConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char curr=(char)data;
                    result+=curr;
                    if(result.length()>=9){
                        if(result.substring(result.length()-9,result.length()-1).equals("FINISHED")) {
                            result+="}]}";
                            break;
                        }
                    }
                    data=reader.read();
                }
//                Log.d("cf result",result);
                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj=new JSONObject(s);
//                String cflist_result=obj.getString("result"); //ALTERNATE
                JSONArray cf_future = obj.getJSONArray("result");
                Log.i("CF Future Contests", String.valueOf(cf_future));

                for (int i = 0; i < cf_future.length()-1; i++) {
                    JSONObject c = cf_future.getJSONObject(i);
                    String platform = "Codeforces";
                    String id = c.getString("id");
                    String name = c.getString("name");
                    String duration = String.valueOf(Integer.parseInt(c.getString("durationSeconds"))/3600)+" H";

                    Instant instant = Instant.ofEpochSecond(Integer.parseInt(c.getString("startTimeSeconds")));

                    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                                                .withLocale( Locale.US )
                                                .withZone( ZoneId.systemDefault() );
                    String tmp_start = formatter.format( instant );
                    String[] ts1 = (tmp_start.split(",")[0]).split("/");
                    if(Integer.parseInt(ts1[1])<10){
                        ts1[1]="0"+ts1[1];
                    }
                    String[] startTime = tmp_start.split(",")[1].split(" ");
                    if(startTime[2].equals("PM")){
                        startTime[1]=String.valueOf(Integer.parseInt(startTime[1].split(":")[0])+12)+":"+startTime[1].split(":")[1];
                    }else{
                        startTime[1]="0"+startTime[1];
                    }

                    String start = ts1[1]+"-"+ts1[0]+"-20"+ts1[2]+" | "+startTime[1];
                    String date_compare = "20"+ts1[2]+ts1[0]+ts1[1]+startTime[1];


                    String link = "https://codeforces.com/contestRegistration/"+id;
                    Vector<String> tmp = new Vector<String>();
                    tmp.add(date_compare);
                    tmp.add(platform);
                    tmp.add(name);
                    tmp.add(start);
                    tmp.add(duration);
                    tmp.add(link);
                        resultdata_All.add(tmp);
                        resultdata_CF.add(tmp);
                }
//                contestlisttest.setText(String.valueOf(resultdata));
                setToAdapter();
//                Log.d("cf future", String.valueOf(result));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    // ATCODER CLASS
    public class AC_list extends AsyncTask<String,Void,String>{
        Elements future_table;
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            try {
                Document doc = Jsoup.connect("https://atcoder.jp/contests/").get();
                future_table = doc.select("div#contest-table-upcoming tbody");
//                Log.i("test",String.valueOf(future_table));

                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            for(Element e : future_table.select("tr")){
                String platform = "AtCoder";
                String link = "https://atcoder.jp"+e.select("td+td a").attr("href");
//                    Log.i("testlinksac",link);
                String name = e.select("td+td a").text();
                String tmp_start = (e.select("td a time").text()).split(" ")[0];
                String tmp_startTime = (e.select("td a time").text()).split(" ")[1];
                int startHour = Integer.parseInt(tmp_startTime.split(":")[0])-5;
                int startMin = Integer.parseInt(tmp_startTime.split(":")[1])+30;
                if(startMin/60<1){
                    startHour+=1;
                }
                startMin=startMin%60;

                String[] ts = tmp_start.split("-");
                String start = ts[2]+"-"+ts[1]+"-"+ts[0]+" | "+Integer.toString(startHour)+":"+Integer.toString(startMin);
                String date_compare = ts[0]+ts[1]+ts[2]+Integer.toString(startHour)+":"+Integer.toString(startMin);

                String td = e.select("td+td+td").text();
                String[] arrDuration = td.split("-");
                String tmp_dur = arrDuration[0];
                arrDuration = tmp_dur.split(":");
                tmp_dur = arrDuration[0].split("0")[1]+" H "+arrDuration[1]+"M";

                Vector<String> tmp = new Vector<String>();
                tmp.add(date_compare);
                tmp.add(platform);
                tmp.add(name);
                tmp.add(start);
                tmp.add(tmp_dur);
                tmp.add(link);

//                Log.i("tmptest",String.valueOf(tmp));
                    resultdata_All.add(tmp);
                    resultdata_AC.add(tmp);
            }
//            contestlisttest.setText(String.valueOf(resultdata));
            setToAdapter();
        }
    }

    void setToAdapter(){
        Vector<Vector<String>> resultdata = new Vector<Vector<String>>();
        switch(tab){
            case 1:
                resultdata = (Vector<Vector<String>>) resultdata_All.clone();
                break;
            case 2:
                resultdata = (Vector<Vector<String>>) resultdata_CC.clone();
                break;
            case 3:
                resultdata = (Vector<Vector<String>>) resultdata_CF.clone();
                break;
            case 4:
                resultdata = (Vector<Vector<String>>) resultdata_AC.clone();
                break;
        }
        Collections.sort(resultdata, new Comparator<Vector<String>>(){
            @Override  public int compare(Vector<String> v1, Vector<String> v2) {
                return v1.get(0).compareTo(v2.get(0)); //If you order by 2nd element in row
            }});


        contestModelArrayList = new ArrayList<>();

        for(int i=0;i<resultdata.size();i++){
            contestModelArrayList.add(new contestModel(resultdata.get(i).get(2), resultdata.get(i).get(3), resultdata.get(i).get(4),resultdata.get(i).get(5),resultdata.get(i).get(1)));
        }
        ContestAdapter contestAdapter = new ContestAdapter(getActivity(), contestModelArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        contestRV.setLayoutManager(linearLayoutManager);
        contestRV.setAdapter(contestAdapter);
        progressBar.setVisibility(View.GONE);
    }

}