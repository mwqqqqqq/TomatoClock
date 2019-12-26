package com.example.tomatoclock.Task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.tomatoclock.Coin;
import com.example.tomatoclock.LoginActivity;
import com.example.tomatoclock.MainActivity;
import com.example.tomatoclock.R;
import com.example.tomatoclock.StreamTools;
import com.example.tomatoclock.rankList.RankListActivity;
import com.example.tomatoclock.report.ShowReport;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

public class TasksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private List<Task> taskList = new ArrayList<>();
    private List<Task> taskList_finished = new ArrayList<>();
    private static List<Integer> task_id_finished_local = new ArrayList<>();
    private RecyclerView taskRecylerView_new;
    private RecyclerView taskRecylerView_finished;
    private TaskAdapter taskAdapter;
    private TaskAdapter taskAdapter_finished;

    private String userName;
    private final int EDITTASK = 1;
    private final int NEWTASK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_tasks);

        initDrawer();
        initTasks();

    }

    private void initDrawer(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void initTasks() {
        taskList = new ArrayList<>();
        taskRecylerView_new = (RecyclerView) findViewById(R.id.recyclerViewOfTasks_new);
        LinearLayoutManager layoutManager_new = new LinearLayoutManager(this);
        taskRecylerView_new.setLayoutManager(layoutManager_new);

        taskList_finished = new ArrayList<>();
        taskRecylerView_finished = (RecyclerView) findViewById(R.id.recyclerViewOfTasks_finished);
        LinearLayoutManager layoutManager_finished = new LinearLayoutManager(this);
        taskRecylerView_finished.setLayoutManager(layoutManager_finished);

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
                            if(TasksActivity.task_id_finished_local.contains(ddl_id)){
                                taskList_finished.add(tempTask);
                            }
                            else {
                                taskList.add(tempTask);
                            }
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

        taskAdapter_finished = new TaskAdapter(this, taskList_finished);
        taskRecylerView_finished.setAdapter(taskAdapter_finished);
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
        System.out.println("try fold finished tasks");
        android.view.ViewGroup.LayoutParams params = taskRecylerView_finished.getLayoutParams();
        if(params.height == -2)
            params.height = 0;
        else params.height = -2;
        taskRecylerView_finished.setLayoutParams(params);
    }

    public void TryEditTaskDdl(Task task, String DDL){
        DoEditTaskDdl(task, DDL);
    }

    public void TryEditTaskInfor(Task task){
        Intent intent = new Intent(TasksActivity.this, TaskEditActivity.class);
        intent.putExtra("id", task.getId());
        intent.putExtra("infor", task.getInfor());
        startActivityForResult(intent, EDITTASK);
    }

    public void TryNewTask(View view){
        Intent intent = new Intent(TasksActivity.this, TaskNewActivity.class);
        startActivityForResult(intent, NEWTASK);
    }

    public void TryRemoveTask(Task task){
        DoRemoveTask(task);
    }

    public void TryBeginTask(Task task){
        int pos = taskList_finished.indexOf(task);
        int id = task.getId();
        if(pos != -1) {
            taskList_finished.remove(pos);
            taskList.add(task);
            int pos_new = taskList.indexOf(task);
            taskAdapter.notifyItemInserted(pos_new);
            taskAdapter_finished.notifyItemRemoved(pos);
            DbStateTask(id, "new");
        }
        DbBeginTask(id);
    }

    public void TryStateTask(Task task){
        DoStateTask(task);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case EDITTASK:
                if(resultCode == RESULT_OK){
                    int id = data.getIntExtra("id", 0);
                    String newInfor = data.getStringExtra("infor");
                    DoEditTaskInfor(id, newInfor);
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

    private void DoEditTaskInfor(int id, String newInfor){
        for(int i = 0; i < taskList.size(); i++) {
            Task t = taskList.get(i);
            if (t.getId() == id){
                t.updateInfor(newInfor);
                taskAdapter.notifyItemChanged(i);
                DbEditTask(t.getId(), t.getInfor(), t.getDdl());
                return;
            }
        }
        for(int i = 0; i < taskList_finished.size(); i++) {
            Task t = taskList_finished.get(i);
            if (t.getId() == id){
                t.updateInfor(newInfor);
                taskAdapter_finished.notifyItemChanged(i);
                DbEditTask(t.getId(), t.getInfor(), t.getDdl());
                return;
            }
        }
    }

    private void DoEditTaskDdl(Task task, String DDL){
        task.updateDdl(DDL);
        int pos = taskList.indexOf(task);
        if(pos != -1) {
            taskAdapter.notifyItemChanged(pos);
        }
        else {
            pos = taskList_finished.indexOf(task);
            taskAdapter_finished.notifyItemChanged(pos);
        }

        DbEditTask(task.getId(), task.getInfor(), task.getDdl());
    }

    private void DoNewTask(String ddl, String infor){
        Task new_task = new Task(-1, ddl, infor);
        taskList.add(new_task);
        int pos = taskList.indexOf(new_task);
        taskAdapter.notifyItemInserted(pos);
        DbNewTask(pos, infor, ddl);
    }

    private void DoRemoveTask(Task task) {
        int pos = taskList.indexOf(task);
        int id = task.getId();
        if(pos != -1) {
            taskList.remove(pos);
            taskAdapter.notifyItemRemoved(pos);
        }
        else {
            pos = taskList_finished.indexOf(task);
            taskAdapter_finished.notifyItemRemoved(pos);
        }
        DbRemoveTask(id);
    }

    private void DoStateTask(Task task){
        int id = task.getId();
        int pos = taskList.indexOf(task);
        if(pos != -1) {
            taskList.remove(pos);
            taskList_finished.add(task);
            int pos_finished = taskList_finished.indexOf(task);
            taskAdapter_finished.notifyItemInserted(pos_finished);
            taskAdapter.notifyItemRemoved(pos);
            DbStateTask(id, "finished");
        }
        else{
            pos = taskList_finished.indexOf(task);
            taskList_finished.remove(pos);
            taskList.add(task);
            int pos_new = taskList.indexOf(task);
            taskAdapter.notifyItemInserted(pos_new);
            taskAdapter_finished.notifyItemRemoved(pos);
            DbStateTask(id, "new");
        }
    }

    private void DbStateTask(int id, String new_state){
        if(new_state.equals("finished")){
            TasksActivity.task_id_finished_local.add(id);
        }
        else {
            TasksActivity.task_id_finished_local.remove((Integer)id);
        }
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            String userName = this.getIntent().getStringExtra("userName");
            intent.putExtra("userName", userName);
            intent.putExtra("用户名", userName);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this, ShowReport.class);
            String userName = this.getIntent().getStringExtra("userName");
            intent.putExtra("userName", userName);
            intent.putExtra("用户名", userName);
            startActivity(intent);

        } else if (id == R.id.nav_tools) {
            Intent intent=new Intent(this, Coin.class);
            String userName = this.getIntent().getStringExtra("userName");
            intent.putExtra("userName", userName);
            intent.putExtra("用户名", userName);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow2)
        {
            Intent intent = new Intent(this, RankListActivity.class);
            String userName = this.getIntent().getStringExtra("userName");
            intent.putExtra("userName", userName);
            intent.putExtra("用户名", userName);
            startActivity(intent);
        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
