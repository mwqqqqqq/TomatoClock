package com.example.tomatoclock.report;

import android.os.Bundle;

import com.example.tomatoclock.StreamTools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.example.tomatoclock.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class ShowAllFocusRecords extends AppCompatActivity {

    RecyclerView allfocusRecords;
    List<FocusWIthDate> focusList = new ArrayList<>();
    String finalUserName = "boot";
    boolean affFinish = false;
    String finalDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_focus_records);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        finalUserName = "boot";

        String local = "GMT+8";//获取当前时间
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(local));
        cal.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        String dateStr = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        String timeStr = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
        //finalDate = dateStr;
        finalDate = "2019-11-16";

        new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/focusByDay?user=" + finalUserName + "&date=" + finalDate;
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    // List<Focus> newFocusList = new ArrayList<>();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamTools.readInputStream(is);
                        JSONArray demo = new JSONArray(result);
                        for(int i = 0;i < demo.length();i++)
                        {
                            JSONObject item = (JSONObject) demo.get(i);
                            String beginTime = item.getString("begin");
                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                            Date date = format.parse(beginTime);
                            FocusWIthDate f = new FocusWIthDate();
                            f.startHour = date.getHours();
                            f.startMinute = date.getMinutes();
                            f.dura =(int) Double.parseDouble(item.getString("time"));
                            f.year = "2019";
                            f.month = "11";
                            f.dayOfMonth = "16";

                            focusList.add(f);
                            //updateFocusByDay(focusList);
                            System.out.println("Eren sss " + f.dura);
                        }
                        //JSONObject demoJson = new JSONObject(result);
                        affFinish = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());

                    //System.out.println();

                }
            }

        }.start();
        while(focusList.size() == 0 && !affFinish)
            continue;
        allfocusRecords = findViewById(R.id.allfocusRecords);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        allfocusRecords.setLayoutManager(layoutManager);
        FocusWIthDateRecordsAdapter fa = new FocusWIthDateRecordsAdapter(focusList);
        allfocusRecords.setAdapter(fa);
    }

}
