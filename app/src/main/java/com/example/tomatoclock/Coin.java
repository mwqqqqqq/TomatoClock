package com.example.tomatoclock;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.tomatoclock.Task.Task;
import com.example.tomatoclock.Task.TaskAdapter;
import com.example.tomatoclock.report.ShowReport;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Coin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button[] backb = new Button[MainActivity.BackImg_num];
    private TextView CoinBalance = null;
    private String userName;
    public char[] BackImg = {'0','0','0','0','0'};
    public char[] Alarm = {'0','0','0','0','0'};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_drawer_coin);
        setContentView(R.layout.activity_coin);

        ReadCoins();
        /*
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        */


        CoinBalance = (TextView) findViewById(R.id.CoinBalance);
        backb[0] = (Button) findViewById(R.id.backb0);
        backb[1] = (Button) findViewById(R.id.backb1);
        backb[2] = (Button) findViewById(R.id.backb2);
        backb[3] = (Button) findViewById(R.id.backb3);
        backb[4] = (Button) findViewById(R.id.backb4);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        CoinBalance.setText("金币数:"+MainActivity.coins);

        // 按钮显示的初始化：购买/应用, 并设置按钮监听
        for(int i = 0; i < MainActivity.BackImg_num; i ++){
            if ( BackImg[i] == '1'){
                // 已经购买
                backb[i].setBackgroundColor(Color.parseColor("#6495ED"));
                backb[i].setText("应用");
            }
            else{
                backb[i].setText("购买");
            }
            backb[i].setOnClickListener(listener);
        }

    }
    private View.OnClickListener listener = new View.OnClickListener() {
        public void onClick(View v) {
            for (int i = 0; i < MainActivity.BackImg_num; i ++) {
                // 目前统一售价40金币，后续更新
                if (v == backb[i] && BackImg[i] == '0' && MainActivity.coins >= 40) {
                    // 正常购买
                    MainActivity.coins -= 40;
                    WriteCoins(40);
                    CoinBalance.setText("金币数：" + String.valueOf(MainActivity.coins));
                    BackImg[i] = '1';
                    WriteBackImg(i);
                    backb[i].setText("应用");
                    backb[i].setBackgroundColor(Color.parseColor("#6495ED"));
                    Toast.makeText(Coin.this,"购买成功", Toast.LENGTH_SHORT).show();
                }
                else if (v == backb[i] && BackImg[i] == '1'){
                    // 正常应用, 直接更改出现问题，修改MainActivity处的全局变量Current_BackImg
                    MainActivity.Current_BackImg = i;
                    WriteCurrentImg();
                    Toast.makeText(Coin.this,"背景更换成功", Toast.LENGTH_SHORT).show();
                }
                else if(v == backb[i] && BackImg[i] == '0' && MainActivity.coins < 40){
                    // 欲购买，金币不足
                    Toast.makeText(Coin.this,"金币不足", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void ReadCoins() {
        userName = this.getIntent().getStringExtra("userName");

        Thread thread = new Thread(){
            public void run(){
                try{
                    String path="http://49.232.5.236:8080/test/resStatus?user="+userName;
                    System.out.println(userName);
                    URL url=new URL(path);
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code=conn.getResponseCode();
                    if(code==200){
                        InputStream is=conn.getInputStream();
                        String result=StreamTools.readInputStream(is);
                        System.out.println(result);
                        JSONObject demoJson = new JSONObject(result);
                        int coins = demoJson.getInt("coins");
                        int t_background = demoJson.getInt("t_background");
                        int t_alarm = demoJson.getInt("t_alarm");
                        String background = demoJson.getString("background");
                        BackImg = background.toCharArray();
                        String alarm = demoJson.getString("alarm");
                        Alarm = alarm.toCharArray();
                        MainActivity.coins = coins + 1000;
                        MainActivity.Current_BackImg = t_background;
                        MainActivity.Current_Alarm = t_alarm;
                        //String ddl_date = tempJson.getString("ddl_date");
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
    }

    //先更新本地，再调用Write函数一次性更新数据库
    private void WriteCurrentImg() {
        userName = this.getIntent().getStringExtra("userName");

        Thread thread = new Thread(){
            public void run(){
                try{
                    String path="http://49.232.5.236:8080/test/altert_Background?user="+userName+"&which=" + MainActivity.Current_BackImg;
                    System.out.println(userName);
                    URL url=new URL(path);
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code=conn.getResponseCode();

                    System.out.println(code);

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
    }

    private void WriteBackImg(int index) {
        userName = this.getIntent().getStringExtra("userName");
        final String temp = index + "";
        Thread thread = new Thread(){
            public void run(){
                try{
                    String path="http://49.232.5.236:8080/test/buyBackground?user="+userName+"&which=" + temp;
                    System.out.println(userName);
                    URL url=new URL(path);
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code=conn.getResponseCode();

                    System.out.println(code);

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
    }

    private void WriteCoins(int index) {
        userName = this.getIntent().getStringExtra("userName");
        final String temp = index + "";
        Thread thread = new Thread(){
            public void run(){
                try{
                    String path="http://49.232.5.236:8080/test/CoinsAction?user="+userName+"&sub=" + temp;
                    System.out.println(userName);
                    URL url=new URL(path);
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code=conn.getResponseCode();

                    System.out.println(code);

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
    }
    // navigation


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

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}


