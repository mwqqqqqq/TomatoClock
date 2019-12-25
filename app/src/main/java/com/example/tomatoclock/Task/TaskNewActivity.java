package com.example.tomatoclock.Task;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tomatoclock.R;


public class TaskNewActivity extends AppCompatActivity {
    EditText taskInforText;
    TextView taskDdlText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_new);

        taskInforText = (EditText) findViewById(R.id.taskNew_editText);
        taskDdlText = (TextView) findViewById(R.id.taskNew_ddlText);
        String taskinfor = "";
        String taskddl = "2019-12-25";
        taskInforText.setText(taskinfor);
        taskDdlText.setText(taskddl);
        setListener();
    }

    private void setListener(){
        taskDdlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int temp = monthOfYear + 1;
                        String monthStr = temp < 10 ? "0" + temp : "" + temp;
                        temp = dayOfMonth;
                        String dayStr = temp < 10 ? "0" + temp : "" + temp;
                        String ddl = year + "-" + monthStr + "-" + dayStr;
                        taskDdlText.setText(ddl);
                    }
                }, 2019, 12, 25).show();
            }
        });
    }
    public void onCancelButtonClick(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onSaveButtonClick(View view){
        Intent intent = new Intent();
        intent.putExtra("infor", taskInforText.getText().toString());
        intent.putExtra("ddl", taskDdlText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
    //@Override
    //public void onBackPressed(){
    //    setResult(RESULT_CANCELED);
    //    finish();
    //}

}
