package com.example.tomatoclock.Task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.example.tomatoclock.R;


public class TaskEditActivity extends AppCompatActivity {
    EditText taskInforText;
    int taskPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        Intent intent = this.getIntent();
        taskInforText = (EditText) findViewById(R.id.taskEdit_editText);
        String taskinfor = intent.getStringExtra("infor");
        taskPos = intent.getIntExtra("pos", 0);
        taskInforText.setText(taskinfor);
    }

    public void onCancelButtonClick(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onSaveButtonClick(View view){
        Intent intent = new Intent();
        intent.putExtra("infor", taskInforText.getText().toString());
        intent.putExtra("pos", taskPos);
        setResult(RESULT_OK, intent);
        finish();
    }
    //@Override
    //public void onBackPressed(){
    //    setResult(RESULT_CANCELED);
    //    finish();
    //}

}
