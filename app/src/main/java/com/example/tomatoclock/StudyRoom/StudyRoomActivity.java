package com.example.tomatoclock.StudyRoom;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tomatoclock.R;
import com.example.tomatoclock.StreamTools;
import com.example.tomatoclock.Task.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StudyRoomActivity extends AppCompatActivity {
    String userName;

    TextView roomNameText;
    TextView roomBeginTimeText;
    TextView roomDurationText;
    TextView roomMemNumText;
    TextView roomMembersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
