package com.example.tomatoclock.StudyRoom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tomatoclock.R;
import com.example.tomatoclock.StreamTools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.Timer;

public class JoinStudyRoomActivity extends AppCompatActivity {
    String userName;

    View join_room_view;
    Dialog join_dialog;
    View create_room_view;
    Dialog create_dialog;

    int beginHour;
    int beginMinute;
    int endHour;
    int endMinute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_study_room);
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        userName = this.getIntent().getStringExtra("userName");

    }
    Calendar calendar = Calendar.getInstance();

    public void tryJoinStudyRoom(View view){
        if(join_dialog != null)
            join_dialog.dismiss();
        LayoutInflater inflater=LayoutInflater.from(this);
        join_room_view = inflater.inflate(R.layout.join_study_room,null);//引用自定义布局
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(join_room_view);
        join_dialog=builder.create();
        join_dialog.show();
    }

    public void tryCreateStudyRoom(View view){
        beginHour = calendar.get(Calendar.HOUR_OF_DAY);
        beginMinute = calendar.get(Calendar.MINUTE);
        endHour = beginHour;
        endMinute = beginMinute;

        if(create_dialog != null)
            create_dialog.dismiss();
        LayoutInflater inflater=LayoutInflater.from(this);
        create_room_view = inflater.inflate(R.layout.create_study_room,null);//引用自定义布局
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(create_room_view);
        create_dialog=builder.create();


        final TextView beginTime = create_room_view.findViewById(R.id.create_study_room_begin_time);
        final TextView endTime = create_room_view.findViewById(R.id.create_study_room_end_time);
        String hourStr = beginHour < 10 ? "0"+beginHour : ""+beginHour;
        String minuteStr = beginMinute < 10 ? "0"+beginMinute : ""+beginMinute;
        String TimeStr = hourStr + ": " + minuteStr;
        beginTime.setText(TimeStr);
        endTime.setText(TimeStr);
        beginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        beginHour = hour;
                        beginMinute = minute;
                        String hourStr = hour < 10 ? "0"+hour : ""+hour;
                        String minuteStr = minute < 10 ? "0"+minute : ""+minute;
                        String beginTimeStr = hourStr + ": " + minuteStr;
                        beginTime.setText(beginTimeStr);
                    }
                    }, beginHour, beginMinute, true).show();
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        endHour = hour;
                        endMinute = minute;
                        String hourStr = hour < 10 ? "0"+hour : ""+hour;
                        String minuteStr = minute < 10 ? "0"+minute : ""+minute;
                        String endTimeStr = hourStr + ": " + minuteStr;
                        endTime.setText(endTimeStr);
                    }
                }, endHour, endMinute, true).show();
            }
        });
        create_dialog.show();
    }

    public void joinStudyRoom(View view) {
        EditText roomNumText = (EditText) join_room_view.findViewById(R.id.room_num_to_join);
        String s = roomNumText.getText().toString();
        if (s.length() < 4){
            showMessage("房间号过短！");
            return;
        }
        int roomNum = Integer.parseInt(roomNumText.getText().toString());
        boolean joinSuccess = doJoinStudyRoom(roomNum);
        if(!joinSuccess){
            showMessage("加入房间失败：房间号错误！");
            return;
        }
        System.out.println("join room " + roomNum);
        EnterStudyRoom();
    }

    public boolean doJoinStudyRoom(final int roomNum){
        final int[] resultCode = {0};
        Thread thread = new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/SRJoin?user=" + userName + "&room_id=" + roomNum;
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
                        resultCode[0] = jsonObject.getInt("code");
                    }
                } catch (Exception e) {
                    resultCode[0] = 0;
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resultCode[0] == 1;
    }

    public boolean doCreateStudyRoom(final int roomNum, final String roomName){
        final int nowDay = calendar.get(Calendar.DAY_OF_MONTH);
        final int nowMonth = calendar.get(Calendar.MONTH) + 1;
        final int nowYear = calendar.get(Calendar.YEAR);

        final String startTime = nowYear+"-"+nowMonth+"-"+nowDay+" "+beginHour+":"+beginMinute+":00";
        int durationHour;
        int durationMinute;
        if(endMinute >= beginMinute){
            durationHour = endHour - beginHour;
            durationMinute = endMinute - beginMinute;
        }
        else{
            durationHour = endHour - beginHour - 1;
            durationMinute = endMinute + 60 - beginMinute;
        }
        final String duration = durationHour+":"+durationMinute+":00";

        final int[] resultCode = {0};
        Thread thread = new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/SRAdd?room_id="+roomNum+"&start_time="+startTime+"&duration="+duration+"&name="+roomName+"&user="+userName;
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
                        resultCode[0] = jsonObject.getInt("code");
                    }
                } catch (Exception e) {
                    resultCode[0] = 0;
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resultCode[0] == 1;
    }

    public void createStudyRoom(View view){
        EditText roomNumText = (EditText) create_room_view.findViewById(R.id.room_num_to_create);
        String num_str = roomNumText.getText().toString();
        EditText roomNameText = (EditText) create_room_view.findViewById(R.id.room_name_to_create);
        String name_str = roomNameText.getText().toString();
        if(num_str.length() < 4) {
            showMessage("房间号过短！");
            return;
        }
        if(endHour < beginHour) {
            showMessage("结束时间错误！");
            return;
        }
        else if(endHour == beginHour && endMinute < beginMinute) {
            showMessage("结束时间错误！");
            return;
        }

        int roomNum = Integer.parseInt(roomNumText.getText().toString());
        boolean createSuccess = doCreateStudyRoom(roomNum, name_str);
        if(!createSuccess){
            showMessage("创建房间失败：房间号已存在！");
            return;
        }
        System.out.println("create room " + name_str + " " + roomNum + " " + beginHour + ":" + beginMinute + "-" + endHour + ":" + endMinute);
        EnterStudyRoom();
    }

    public void EnterStudyRoom(){
        Intent intent = new Intent(this, StudyRoomActivity.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
        if(join_dialog != null)
            join_dialog.dismiss();
        if(create_dialog != null)
            create_dialog.dismiss();
        this.finish();
    }

    public void showMessage(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        builder.show();
    }
}
