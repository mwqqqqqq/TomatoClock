package com.example.tomatoclock.Task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.tomatoclock.R;

public class TasksActivity extends AppCompatActivity {
    private List<Task> taskList = new ArrayList<>();
    private RecyclerView taskRecylerView;
    private TaskAdapter taskAdapter;
    private final int EDITTASK = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        initTasks();
        taskRecylerView = (RecyclerView) findViewById(R.id.recyclerViewOfTasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        taskRecylerView.setLayoutManager(layoutManager);
        taskAdapter = new TaskAdapter(this, taskList);
        taskRecylerView.setAdapter(taskAdapter);


        Button newTask = (Button) findViewById(R.id.new_task_button);
    }

    private void initTasks() {
        //TODO: 获取数据，初始化任务列表
        for(int i = 0; i < 10; ++i){
            Task task = new Task(i, "2019-10-18", i+"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\naaa\na");
            taskList.add(task);
        }
    }

    public void addTask(View view){
        Task task = new Task(11, "2019-10-18", 11+"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\naaa\na");
        taskAdapter.addData(task);
    }

    public void editTask(int list_id, String infor){
        Intent intent = new Intent(TasksActivity.this, TaskEditActivity.class);
        intent.putExtra("list_id", list_id);
        intent.putExtra("infor", infor);
        startActivityForResult(intent, EDITTASK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case EDITTASK:
                if(resultCode == RESULT_OK){
                    int task_id = data.getIntExtra("list_id", 0);
                    String newInfor = data.getStringExtra("infor");
                    taskList.get(task_id).updateInfor(newInfor);
                    taskAdapter.notifyItemChanged(task_id);
                }
                break;
            default:
        }
    }

}
