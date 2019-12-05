package com.example.tomatoclock.Task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.tomatoclock.LoginActivity;
import com.example.tomatoclock.MainActivity;
import com.example.tomatoclock.R;
import com.example.tomatoclock.StreamTools;

import org.json.JSONArray;
import org.json.JSONObject;

public class TasksActivity extends AppCompatActivity {
    private List<Task> taskList = new ArrayList<>();
    private RecyclerView taskRecylerView_new;
    private TaskAdapter taskAdapter;

    private String userName;
    private final int EDITTASK = 1;
    private final int NEWTASK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        initTasks();

    }

    private void initTasks() {
        taskRecylerView_new = (RecyclerView) findViewById(R.id.recyclerViewOfTasks_new);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        taskRecylerView_new.setLayoutManager(layoutManager);
        userName = this.getIntent().getStringExtra("userName");

        Thread thread = new Thread(){
            public void run(){
                try{
                    String path="http://49.232.5.236:8080/test/DDLFind?user_name="+userName;
                    System.out.println(userName);
                    URL url=new URL(path);
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code=conn.getResponseCode();
                    if(code==200){
                        InputStream is=conn.getInputStream();
                        String result= StreamTools.readInputStream(is);
                        System.out.println(result);
                        JSONArray demoJson = new JSONArray(result);
                        for(int i = 0; i < demoJson.length(); ++i){
                            JSONObject tempJson = demoJson.getJSONObject(i);
                            int ddl_id = tempJson.getInt("ddl_id");
                            String ddl_date = tempJson.getString("ddl_date");
                            String ddl_desc = tempJson.getString("ddl_desc");
                            Task tempTask = new Task(ddl_id, ddl_date, ddl_desc);
                            System.out.println(ddl_desc);
                            taskList.add(tempTask);
                        }
                    }
                }catch(Exception e){
                    return;
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        taskAdapter = new TaskAdapter(this, taskList);
        taskRecylerView_new.setAdapter(taskAdapter);
    }

    public void fold_new_tasks(View view){
        System.out.println("try fold new tasks");
        android.view.ViewGroup.LayoutParams params = taskRecylerView_new.getLayoutParams();
        if(params.height == -2)
            params.height = 0;
        else params.height = -2;
        taskRecylerView_new.setLayoutParams(params);
    }

    public void fold_finished_tasks(View view){
        return;
//        System.out.println("try fold new tasks");
//        android.view.ViewGroup.LayoutParams params = taskRecylerView_new.getLayoutParams();
//        if(params.height == -2)
//            params.height = 0;
//        else params.height = -2;
//        taskRecylerView_new.setLayoutParams(params);
    }

    public void TryEditTaskDdl(int pos, String ddl){
        DoEditTaskDdl(pos, ddl);
    }

    public void TryEditTaskInfor(int pos, String infor){
        Intent intent = new Intent(TasksActivity.this, TaskEditActivity.class);
        intent.putExtra("pos", pos);
        intent.putExtra("infor", infor);
        startActivityForResult(intent, EDITTASK);
    }

    public void TryNewTask(View view){
        Intent intent = new Intent(TasksActivity.this, TaskNewActivity.class);
        startActivityForResult(intent, NEWTASK);
    }

    public void TryRemoveTask(int pos){
        DoRemoveTask(pos);
    }

    public void TryBeginTask(int pos){
        Task task = taskList.get(pos);
        DbBeginTask(task.getId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case EDITTASK:
                if(resultCode == RESULT_OK){
                    int pos = data.getIntExtra("pos", 0);
                    String newInfor = data.getStringExtra("infor");
                    DoEditTaskInfor(pos, newInfor);
                }
                break;
            case NEWTASK:
                if(resultCode == RESULT_OK){
                    String infor = data.getStringExtra("infor");
                    String ddl = data.getStringExtra("ddl");
                    DoNewTask(ddl, infor);
                }
                break;
            default:
        }
    }

    private void DoEditTaskInfor(int pos, String newInfor){
        Task task = taskList.get(pos);
        task.updateInfor(newInfor);
        taskAdapter.notifyItemChanged(pos);
        DbEditTask(task.getId(), task.getInfor(), task.getDdl());
    }

    private void DoEditTaskDdl(int pos, String newDdl){
        Task task = taskList.get(pos);
        task.updateDdl(newDdl);
        taskAdapter.notifyItemChanged(pos);
        DbEditTask(task.getId(), task.getInfor(), task.getDdl());
    }

    private void DoNewTask(String ddl, String infor){
        Task new_task = new Task(-1, ddl, infor);
        taskList.add(new_task);
        int pos = taskList.indexOf(new_task);
        taskAdapter.notifyItemInserted(pos);
        DbNewTask(pos, infor, ddl);
    }

    private void DoRemoveTask(int pos) {
        Task task = taskList.get(pos);
        int id = task.getId();
        taskList.remove(pos);
        taskAdapter.notifyItemRemoved(pos);
        DbRemoveTask(id);
    }

    private void DbEditTask(final int id, final String newInfor, final String newDdl){
        new Thread(){
            public void run(){
                try{
                    String path="http://49.232.5.236:8080/test/DDLChange?ddl_id="+id+"&ddl_name=2&ddl_desc="+newInfor+"&ddl_time0=01:01:01&ddl_time1=01:01:01&ddl_date="+newDdl+" 20:20:20";
                    URL url=new URL(path);
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code=conn.getResponseCode();
                    if(code==200){
                        InputStream is=conn.getInputStream();
                        String result= StreamTools.readInputStream(is);
                        System.out.println(result);
                    }
                }catch(Exception e){
                    return;
                }
            }
        }.start();
    }

    private void DbRemoveTask(final int id){
        new Thread(){
            public void run(){
                try{
                    String path="http://49.232.5.236:8080/test/DDLDelete?ddl_id="+id;
                    URL url=new URL(path);
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code=conn.getResponseCode();
                    if(code==200){
                        InputStream is=conn.getInputStream();
                        String result= StreamTools.readInputStream(is);
                        System.out.println(result);
                    }
                }catch(Exception e){
                    return;
                }
            }
        }.start();
    }

    private void DbNewTask(final int pos, final String newInfor, final String newDdl){
        Thread thread = new Thread(){
            public void run(){
                try{
                    String path="http://49.232.5.236:8080/test/DDLAdd?ddl_name=2&ddl_desc="+newInfor+"&ddl_time0=01:01:01&ddl_time1=01:01:01&ddl_date="+newDdl+" 20:20:20&user_name="+userName;
                    System.out.println(path);
                    URL url=new URL(path);
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code=conn.getResponseCode();
                    if(code==200){
                        InputStream is=conn.getInputStream();
                        String result= StreamTools.readInputStream(is);
                        System.out.println(result);
                        JSONObject jsonObject = new JSONObject(result);
                        int id = jsonObject.getInt("ddl_id");
                        taskList.get(pos).setId(id);
                    }
                }catch(Exception e){
                    return;
                }
            }
        };
        thread.start();
    }

    private void DbBeginTask(final int id){
        new Thread(){
            public void run(){
                try{
                    String path="http://49.232.5.236:8080/test/UserChange?user_name=" + userName + "&ddl_id="+id;
                    System.out.println(path);
                    URL url=new URL(path);
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code=conn.getResponseCode();
                    if(code==200){
                        InputStream is=conn.getInputStream();
                        String result= StreamTools.readInputStream(is);
                        System.out.println(result);
                    }
                }catch(Exception e){
                    return;
                }
            }
        }.start();
    }
}
