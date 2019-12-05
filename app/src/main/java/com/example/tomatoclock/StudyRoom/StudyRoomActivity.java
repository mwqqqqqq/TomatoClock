package com.example.tomatoclock.StudyRoom;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tomatoclock.R;

public class StudyRoomActivity extends AppCompatActivity {
    String userName;

    TextView roomNameText;
    TextView roomTimeText;
    TextView roomMembersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);
        userName = this.getIntent().getStringExtra("userName");

        roomNameText = (TextView)findViewById(R.id.study_room_name);
        roomTimeText = (TextView)findViewById(R.id.study_room_time);
        roomMembersText = (TextView)findViewById(R.id.study_room_members);

        roomMembersText.setText(userName);
    }

    public void leaveRoom(View view){
        this.finish();
    }
}
