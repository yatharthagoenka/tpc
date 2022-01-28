package com.example.tpc.ViewModels;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tpc.Models.contestModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Vector;

public class contestViewModel extends ViewModel {

    private MutableLiveData<ArrayList<contestModel>> contestLiveData;
    private ArrayList<contestModel> contestArrayList;

    private Vector<Vector<String>> result_vector;

    public contestViewModel() {
        contestLiveData = new MutableLiveData<>();
        init();
    }

    public MutableLiveData<ArrayList<contestModel>> getContestMutableLiveData() {
//        Log.d("testarrl",String.valueOf(contestLiveData.getValue().size()));
        return contestLiveData;
    }

    public void init(){
        contestArrayList = new ArrayList<>();
        result_vector = new Vector<>();
        populateList();
        contestLiveData.setValue(contestArrayList);
    }

    private void populateList(){
        CC_list cclist2 = new CC_list();
        cclist2.execute("https://www.codechef.com/api/list/contests/future?sort_by=START&sorting_order=asc&offset=0&mode=premium    ");
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
        @RequiresApi(api = Build.VERSION_CODES.O)
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
                    String ts_v="";
                    if(ts[1].equals("Jan")) ts_v="01";
                    else if(ts[1].equals("Feb")) ts_v="02";
                    else if(ts[1].equals("Mar")) ts_v="03";
                    else if(ts[1].equals("Apr")) ts_v="04";
                    else if(ts[1].equals("May")) ts_v="05";
                    else if(ts[1].equals("Jun")) ts_v="06";
                    else if(ts[1].equals("Jul")) ts_v="07";
                    else if(ts[1].equals("Aug")) ts_v="08";
                    else if(ts[1].equals("Sep")) ts_v="09";
                    else if(ts[1].equals("Oct")) ts_v="10";
                    else if(ts[1].equals("Nov")) ts_v="11";
                    else if(ts[1].equals("Dec")) ts_v="12";
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

                    result_vector.add(tmp);
                }


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
                    if(Integer.parseInt(ts1[0])<10){
                        ts1[0]="0"+ts1[0];
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
                    result_vector.add(tmp);
                }


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

        @RequiresApi(api = Build.VERSION_CODES.O)
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
                result_vector.add(tmp);
            }
            addTOFinalList();

        }
    }
    
    void addTOFinalList(){
        Collections.sort(result_vector, new Comparator<Vector<String>>(){
            @Override  public int compare(Vector<String> v1, Vector<String> v2) {
                return v1.get(0).compareTo(v2.get(0)); //If you order by 2nd element in row
            }});

        for(int i=0;i<result_vector.size();i++){
            contestArrayList.add(new contestModel(result_vector.get(i).get(2), result_vector.get(i).get(3), result_vector.get(i).get(4),result_vector.get(i).get(5),result_vector.get(i).get(1)));
        }

    }
}
