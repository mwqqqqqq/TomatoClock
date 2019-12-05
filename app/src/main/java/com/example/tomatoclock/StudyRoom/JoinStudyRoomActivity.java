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

import java.sql.Time;
import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.Timer;

public class JoinStudyRoomActivity extends AppCompatActivity {
    String userName;

    View join_room_view;
    View create_room_view;

    Calendar calendar = Calendar.getInstance();
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

    public void tryJoinStudyRoom(View view){
        Dialog dialog;
        LayoutInflater inflater=LayoutInflater.from(this);
        join_room_view = inflater.inflate(R.layout.join_study_room,null);//引用自定义布局
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(join_room_view);
        dialog=builder.create();
        dialog.show();
    }

    public void tryCreateStudyRoom(View view){
        beginHour = calendar.get(Calendar.HOUR_OF_DAY);
        beginMinute = calendar.get(Calendar.MINUTE);
        endHour = beginHour;
        endMinute = beginMinute;

        Dialog dialog;
        LayoutInflater inflater=LayoutInflater.from(this);
        create_room_view = inflater.inflate(R.layout.create_study_room,null);//引用自定义布局
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(create_room_view);
        dialog=builder.create();


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
        dialog.show();
    }

    public void joinStudyRoom(View view) {
        EditText roomNumText = (EditText) join_room_view.findViewById(R.id.room_num_to_join);
        String s = roomNumText.getText().toString();
        if (s.length() < 4){
            showMessage("房间号过短！");
            return;
        }
        int roomNum = Integer.parseInt(roomNumText.getText().toString());
        System.out.println("join room " + roomNum);
        EnterStudyRoom();
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
        System.out.println("create room " + name_str + " " + roomNum + " " + beginHour + ":" + beginMinute + "-" + endHour + ":" + endMinute);
        EnterStudyRoom();
    }

    public void EnterStudyRoom(){
        Intent intent = new Intent(this, StudyRoomActivity.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
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
