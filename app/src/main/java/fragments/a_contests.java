package fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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
import android.widget.TextView;

import com.example.tpc.R;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import static java.lang.StrictMath.abs;


public class a_contests extends Fragment {

    View view;
    private TextView contestlisttest;
    private Vector<Vector<String>> resultdata;
    ChipNavigationBar chipNavigationBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_a_contests, container, false);
        contestlisttest = view.findViewById(R.id.contestlisttest);
        chipNavigationBar = view.findViewById(R.id.contest_menubar);
        resultdata = new Vector<Vector<String>>();

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.contestmenu_all:
                        resultdata.clear();
                        CC_list cclist2 = new CC_list();
                        cclist2.execute("https://www.codechef.com/api/list/contests/all?sort_by=START&sorting_order=asc&offset=0&mode=premium");
                        CF_list cflist2 = new CF_list();
                        cflist2.execute("https://codeforces.com/api/contest.list?gym=false");
                        AC_list aclist2 = new AC_list();
                        aclist2.execute();
                        break;
                    case R.id.contestmenu_cc:
                        resultdata.clear();
                        CC_list cclist = new CC_list();
                        cclist.execute("https://www.codechef.com/api/list/contests/all?sort_by=START&sorting_order=asc&offset=0&mode=premium");
                        break;
                    case R.id.contestmenu_cf:
                        resultdata.clear();
                        CF_list cflist = new CF_list();
                        cflist.execute("https://codeforces.com/api/contest.list?gym=false");
                        break;
                    case R.id.contestmenu_ac:
                        resultdata.clear();
                        AC_list aclist = new AC_list();
                        aclist.execute();
                        break;
                }
            }
        });

        return view;
    }


    public class CC_list extends AsyncTask<String,Void,String>{
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
                    String start = c.getString("contest_start_date");
                    String duration = c.getString("contest_duration");
                    String link = "https://www.codechef.com/"+id+"?itm_campaign=contest_listing";
                    Vector<String> tmp = new Vector<String>();
                    tmp.add(platform);
                    tmp.add(name);
                    tmp.add(start);
                    tmp.add(duration);
                    tmp.add(link);
                    resultdata.add(tmp);
                }
                contestlisttest.setText(String.valueOf(resultdata));
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
                    String duration = String.valueOf(Integer.parseInt(c.getString("durationSeconds"))/60);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    int t = (abs(Integer.parseInt(c.getString("relativeTimeSeconds"))))/86400;
                    cal.add(Calendar.DATE, t);
                    String start = sdf.format(cal.getTime());

                    String link = "https://codeforces.com/contestRegistration/"+id;
                    Vector<String> tmp = new Vector<String>();
                    tmp.add(platform);
                    tmp.add(name);
                    tmp.add(start);
                    tmp.add(duration);
                    tmp.add(link);
                    resultdata.add(tmp);
                }
                contestlisttest.setText(String.valueOf(resultdata));
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
                String start = e.select("td a time").text();
                String duration = e.select("td+td+td").text();

                Vector<String> tmp = new Vector<String>();
                tmp.add(platform);
                tmp.add(name);
                tmp.add(start);
                tmp.add(duration);
                tmp.add(link);

//                Log.i("tmptest",String.valueOf(tmp));
                resultdata.add(tmp);
            }
            contestlisttest.setText(String.valueOf(resultdata));
        }
    }


}