package com.example.tomatoclock.StudyRoom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tomatoclock.R;
import com.example.tomatoclock.StreamTools;
import com.example.tomatoclock.Task.Task;
import com.example.tomatoclock.report.Focus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class StudyRoomActivity extends AppCompatActivity {

    String userName;
    TextView roomNameText;
    TextView roomBeginTimeText;
    TextView roomDurationText;
    TextView roomMemNumText;
    TextView roomMembersText;

    private int startTime = 0;
    Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("Asia/Shanghai"));
    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat s2 = new SimpleDateFormat("yyyy-MM-dd");
    String WorkBegin;
    String WorkEnd;
    String WorkTime;
    public int ddl_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final Focus ff = new Focus();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);
        userName = this.getIntent().getStringExtra("userName");

        roomNameText = (TextView)findViewById(R.id.study_room_name);
        roomBeginTimeText = (TextView)findViewById(R.id.study_room_start_time);
        roomDurationText = (TextView)findViewById(R.id.study_room_duration);
        roomMemNumText = (TextView) findViewById(R.id.study_room_mem_number);
        roomMembersText = (TextView)findViewById(R.id.study_room_members);

        initRoomInfor();
        initRoomMembers();


        final Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer2);
        Button btnStart = (Button) findViewById(R.id.btnStart2);
        Button btnStop = (Button) findViewById(R.id.btnStop2);
        Button btnRest = (Button) findViewById(R.id.btnReset2);
        final EditText edtSetTime = (EditText) findViewById(R.id.edt_settime2);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("--开始记时--");
                String ss = edtSetTime.getText().toString();
                if (!(ss.equals("") && ss != null)) {
                    startTime = Integer.parseInt(edtSetTime.getText()
                            .toString());
                }
                // 设置开始讲时时间
                chronometer.setBase(SystemClock.elapsedRealtime());
                // 开始记时
                chronometer.start();
                WorkBegin = s.format(cal.getTime());

                new Thread() {
                    public void run() {
                        try {
                            String path = "http://49.232.5.236:8080/test/UserFind?user_name="+userName;
                            System.out.println(path);
                            URL url = new URL(path);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                            int code = conn.getResponseCode();
                            if (code == 200) {
                                InputStream is = conn.getInputStream();
                                String result = StreamTools.readInputStream(is);
                                JSONObject demoJson = new JSONObject(result);
                                if (demoJson.getString("code").equals("1")) {
                                    ddl_id = demoJson.getInt("ddl_id");
                                } else {
                                    System.out.println("failed");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 停止
                chronometer.stop();
                ff.dura = ((int)SystemClock.elapsedRealtime()- (int)chronometer.getBase());
                WorkEnd = s.format(cal.getTime());
                WorkEnd.replace(" ", "%20");
                WorkBegin.replace(" ", "%20");
                WorkTime= getGapTime((long)ff.dura);
                final String d = s2.format(cal.getTime());
                System.out.println(ddl_id);
                System.out.println(WorkBegin);
                System.out.println(WorkEnd);
                System.out.println(WorkTime);
                System.out.println(d);
                new Thread(){
                    public void run() {
                        try {
                            String path = "http://49.232.5.236:8080/test/WorkAdd?work_begin="+WorkBegin+"&work_end="+WorkEnd+"&interruption=1&work_time="+WorkTime+"&user_name="+userName+"&ddl_id="+ddl_id+"&date="+d;
                            System.out.println(path);
                            URL url = new URL(path);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                            int code = conn.getResponseCode();
                            if (code == 200) {
                                InputStream is=conn.getInputStream();
                                String result=StreamTools.readInputStream(is);
                                JSONObject demoJson = new JSONObject(result);
                                if(demoJson.getString("code").equals("0")){
                                    System.out.println("sorry, failed");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();


            }
        });
        // 重置
        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());

            }
        });
        chronometer
                .setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        // 如果开始计时到现在超过了startime秒
                        if (SystemClock.elapsedRealtime()
                                - chronometer.getBase() > startTime * 1000) {
                            chronometer.stop();

                            new Thread(){
                                public void run() {
                                    try {
                                        String path="http://49.232.5.236:8080/test/CoinsAction?user="+userName+"&sub=-"+ startTime;
                                        System.out.println(path);
                                        URL url = new URL(path);
                                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                        conn.setRequestMethod("GET");
                                        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                                        int code = conn.getResponseCode();
                                        if (code == 200) {
                                            InputStream is=conn.getInputStream();
                                            String result=StreamTools.readInputStream(is);
                                            System.out.println(result);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                            ff.dura = ((int)SystemClock.elapsedRealtime()- (int)chronometer.getBase());
                            WorkEnd = s.format(cal.getTime());
                            WorkEnd.replace(" ", "%20");
                            WorkBegin.replace(" ", "%20");
                            WorkTime= getGapTime((long)ff.dura);
                            final String d = s2.format(cal.getTime());
                            new Thread(){
                                public void run() {
                                    try {
                                        String path = "http://49.232.5.236:8080/test/WorkAdd?work_begin="+WorkBegin+"&work_end="+WorkEnd+"&interruption=0&work_time="+WorkTime+"&user_name="+userName+"&ddl_id="+ddl_id+"&date="+d;
                                        System.out.println(path);
                                        URL url = new URL(path);
                                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                        conn.setRequestMethod("GET");
                                        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                                        int code = conn.getResponseCode();
                                        if (code == 200) {
                                            InputStream is=conn.getInputStream();
                                            String result=StreamTools.readInputStream(is);
                                            JSONObject demoJson = new JSONObject(result);
                                            if(demoJson.getString("code").equals("0")){
                                                System.out.println("sorry, failed");
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();

                            ff.dura = startTime;

                            // 给用户提示
                            showDialog();
                        }
                    }
                });

    }

    protected void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("叮铃铃").setMessage("恭喜你完成一个番茄周期，获得金币100")
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String getGapTime(long time){
        long hours = time / (1000 * 60 * 60);
        long minutes = (time-hours*(1000 * 60 * 60))/(1000* 60);
        long seconds = (time - hours * 60 * 60 * 1000 - minutes * 60 * 1000) / 1000;
        String diffTime="";
        if(minutes<10){
            if(seconds < 10)
                diffTime="0"+hours+":0"+minutes+ ":0"+seconds;
            else
                diffTime="0"+hours+":0"+minutes+":"+seconds;
        }else{
            if(seconds < 10)
                diffTime="0"+hours+":"+minutes + ":0"+seconds;
            else
                diffTime="0"+hours+":"+minutes + ":"+seconds;
        }
        return diffTime;
    }

    private void initRoomMembers(){
        Thread thread = new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/getUser?user="+userName;
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamTools.readInputStream(is);
                        System.out.println(result);
                        JSONArray demoJson = new JSONArray(result);
                        String roomMembers = "";
                        for(int i = 0; i < demoJson.length(); ++i){
                            JSONObject tempJson = demoJson.getJSONObject(i);
                            roomMembers = roomMembers.concat(tempJson.getString("user") + "  ");
                        }
                        roomMembersText.setText(roomMembers);
                    }
                } catch (Exception e) {
                    return;
                }
            }
        };
        thread.start();
    }
    private void initRoomInfor(){
        Thread thread = new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/getRoom?user="+userName;
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamTools.readInputStream(is);
                        System.out.println(result);
                        JSONObject jsonObject = new JSONObject(result);
                        String startTime0 = jsonObject.getString("start_time").split("\\.")[0];
                        String startTime1 = "开始时间："+startTime0.split(":")[0] + ":" + startTime0.split(":")[1];
                        roomBeginTimeText.setText(startTime1);
                        roomNameText.setText(jsonObject.getString("name"));
                        String duration = "自习总时长："+jsonObject.getString("duration");
                        roomDurationText.setText(duration);
                    }
                } catch (Exception e) {
                    return;
                }
            }
        };
        thread.start();
    }
    public void leaveRoom(View view){
        Thread thread = new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/URExit0?user="+userName;
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamTools.readInputStream(is);
                        System.out.println(result);
                    }
                } catch (Exception e) {
                    return;
                }
            }
        };
        thread.start();
        this.finish();
    }
}
