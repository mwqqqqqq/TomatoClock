package com.example.tomatoclock.Task;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.tomatoclock.R;

public class TasksActivity extends AppCompatActivity {
    private List<Task> taskList = new ArrayList<>();
    private RecyclerView recyclerViewOfTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_tasks);
        initTasks();
        this.recyclerViewOfTask = (RecyclerView) findViewById(R.id.recyclerViewOfTasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        this.recyclerViewOfTask.setLayoutManager(layoutManager);
        TaskAdapter adapter = new TaskAdapter(taskList);
        this.recyclerViewOfTask.setAdapter(adapter);
        Button newTask = (Button) findViewById(R.id.new_task_button);
    }

    private void initTasks() {
        //TODO: 获取数据，初始化任务列表
        for(int i = 0; i < 10; ++i){
            Task task = new Task(i, "2019-10-18", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\naaa\na"+i);
            taskList.add(task);
        }
    }

    public void addTask(View view){
        Task task = new Task(11, "2019-10-18", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\naaa\na");
        //taskList.add(task);
        Log.d("aaaaaaaaaaaaa", "ddddddddddddddddddddddddddddddddddddddd");
    }
}
