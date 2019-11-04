package com.example.tomatoclock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.tomatoclock.Task.TasksActivity;
import com.example.tomatoclock.report.ShowReport;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.SystemClock;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;

import java.lang.reflect.Array;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // 金币数，背景图片总数，背景图购买数组
    // 目前是本地初始化，后续从服务器上下载
    public static int coins = 100;
    public static int BackImg_num = 5;
    public static int[] BackImg = {0,1,0,0,0};
    public static int Alarm_num = 5;
    public static int[] Alarm = {0,0,0,0,0};
    // 当前应用的背景图片ID，闹铃声ID
    public static int Current_BackImg = - 1;
    public static int Current_Alarm = - 1;

    private int startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        //Timer Code
        final Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);
        Button btnStart = (Button) findViewById(R.id.btnStart);
        Button btnStop = (Button) findViewById(R.id.btnStop);
        Button btnRest = (Button) findViewById(R.id.btnReset);
        final EditText edtSetTime = (EditText) findViewById(R.id.edt_settime);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("--开始记时---");
                String ss = edtSetTime.getText().toString();
                if (!(ss.equals("") && ss != null)) {
                    startTime = Integer.parseInt(edtSetTime.getText()
                            .toString());
                }
                // 设置开始讲时时间
                chronometer.setBase(SystemClock.elapsedRealtime());
                // 开始记时
                chronometer.start();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 停止
                chronometer.stop();
            }

        });
        // 重置
        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());

            }
        });
        chronometer
                .setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        // 如果开始计时到现在超过了startime秒
                        if (SystemClock.elapsedRealtime()
                                - chronometer.getBase() > startTime * 1000) {
                            chronometer.stop();
                            // 给用户提示
                            showDialog();
                        }
                    }
                });
    }

        protected void showDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("警告").setMessage("时间到")
                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int which) {
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
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
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, TasksActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this, ShowReport.class);
            startActivity(intent);

        } else if (id == R.id.nav_tools) {
            Intent intent=new Intent(MainActivity.this,Coin.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    // 从其他Activity返回到该Activity时进行刷新, 主要目的是为了更换背景图片、响铃声
    @Override
    protected void onResume() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        super.onResume();
        if (Current_BackImg == 0 )
            drawer.setBackgroundResource(R.mipmap.back0);
        else if (Current_BackImg == 1 )
            drawer.setBackgroundResource(R.mipmap.back1);
        else if (Current_BackImg == 2 )
            drawer.setBackgroundResource(R.mipmap.back2);
        else if (Current_BackImg == 3 )
            drawer.setBackgroundResource(R.mipmap.back3);
        else if (Current_BackImg == 4 )
            drawer.setBackgroundResource(R.mipmap.back4);

    }
}
