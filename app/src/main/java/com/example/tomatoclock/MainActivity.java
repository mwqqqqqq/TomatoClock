package com.example.tomatoclock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.example.tomatoclock.Task.Task;
import com.example.tomatoclock.Task.TasksActivity;
import com.example.tomatoclock.StudyRoom.StudyRoomActivity;
import com.example.tomatoclock.StudyRoom.JoinStudyRoomActivity;
import com.example.tomatoclock.rankList.RankListActivity;
import com.example.tomatoclock.report.ShowReport;
import com.example.tomatoclock.Coin;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.os.Message;
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
import android.text.format.Time;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import com.example.tomatoclock.report.Focus;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // 用于传专注信息
    public static ArrayList<Focus> flist = new ArrayList<Focus>();
    // 金币数，背景图片总数，背景图购买数组
    // 目前是本地初始化，后续从服务器上下载
    public static int coins = 200;
    public static int BackImg_num = 5;
    public static int Alarm_num = 5;
    // 当前应用的背景图片ID，闹铃声ID
    public static int Current_BackImg = - 1;
    public static int Current_Alarm = - 1;
    private String userName;
    private int startTime = 0;

    Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+8"));
    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat s2 = new SimpleDateFormat("yyyy-MM-dd");
    String WorkBegin;
    String WorkEnd;
    String WorkTime;
    public int ddl_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userName = this.getIntent().getStringExtra("用户名");
        // 用于传专注信息
        final Focus ff = new Focus();
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

        ReadCoins();


//        LinearLayout drawer_header = navigationView.findViewById(R.id.drawer_header);
//        TextView userNameInfoText = drawer_header.findViewById(R.id.userNameInfo);
//        userNameInfoText.setText(this.getIntent().getStringExtra("用户名"));


        //Timer Code
        final Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);
        Button btnStart = (Button) findViewById(R.id.btnStart);
        Button btnStop = (Button) findViewById(R.id.btnStop);
        Button btnRest = (Button) findViewById(R.id.btnReset);
        final EditText edtSetTime = (EditText) findViewById(R.id.edt_settime);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("--开始记时--");
                String ss = edtSetTime.getText().toString();
                if (!(ss.equals("") && ss != null)) {
                    startTime = Integer.parseInt(edtSetTime.getText()
                            .toString());
            }
                // 设置开始讲时时间
                chronometer.setBase(SystemClock.elapsedRealtime());
                // 开始记时
                chronometer.start();
                WorkBegin = s.format(cal.getTime());

                new Thread() {
                    public void run() {
                        try {
                            String path = "http://49.232.5.236:8080/test/UserFind?user_name=moweiqi";
                            System.out.println(path);
                            URL url = new URL(path);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                            int code = conn.getResponseCode();
                            if (code == 200) {
                                InputStream is = conn.getInputStream();
                                String result = StreamTools.readInputStream(is);
                                JSONObject demoJson = new JSONObject(result);
                                if (demoJson.getString("code").equals("1")) {
                                    ddl_id = demoJson.getInt("ddl_id");
                                } else {
                                    System.out.println("failed");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                ff.startHour = cal.get(Calendar.HOUR_OF_DAY);
                ff.startMinute = cal.get(Calendar.MINUTE);

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 停止
                chronometer.stop();
                ff.dura = ((int)SystemClock.elapsedRealtime()- (int)chronometer.getBase());
                WorkEnd = s.format(cal.getTime());
                WorkEnd.replace(" ", "%20");
                WorkBegin.replace(" ", "%20");
                WorkTime= getGapTime((long)ff.dura);
                final String d = s2.format(cal.getTime());
                System.out.println(ddl_id);
                System.out.println(WorkBegin);
                System.out.println(WorkEnd);
                System.out.println(WorkTime);
                System.out.println(d);
                new Thread(){
                    public void run() {
                        try {
                            String path = "http://49.232.5.236:8080/test/WorkAdd?work_begin="+WorkBegin+"&work_end="+WorkEnd+"&interruption=1&work_time="+WorkTime+"user_name=moweiqi&ddl_id="+ddl_id+"&date="+d;
                            System.out.println(path);
                            URL url = new URL(path);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                            int code = conn.getResponseCode();
                            if (code == 200) {
                                InputStream is=conn.getInputStream();
                                String result=StreamTools.readInputStream(is);
                                JSONObject demoJson = new JSONObject(result);
                                if(demoJson.getString("code").equals("0")){
                                    System.out.println("sorry, failed");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                flist.add(ff);
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

                            new Thread(){
                                public void run() {
                                    try {
                                        String path="http://49.232.5.236:8080/test/CoinsAction?user="+userName+"&sub=-100";
                                        System.out.println(path);
                                        URL url = new URL(path);
                                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                        conn.setRequestMethod("GET");
                                        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                                        int code = conn.getResponseCode();
                                        if (code == 200) {
                                            InputStream is=conn.getInputStream();
                                            String result=StreamTools.readInputStream(is);
                                            System.out.println(result);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();

                            ff.dura = startTime;
                            flist.add(ff);
                            // 给用户提示
                            showDialog();
                        }
                    }
                });

    }

    protected void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("叮铃铃").setMessage("恭喜你完成一个番茄周期，获得金币100")
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

        //WriteCoins(-100);
    }

    public String getGapTime(long time){
        long hours = time / (1000 * 60 * 60);
        long minutes = (time-hours*(1000 * 60 * 60))/(1000* 60);
        long seconds = (time - hours * 60 * 60 * 1000 - minutes * 60 * 1000) / 1000;
        String diffTime="";
        if(minutes<10){
            if(seconds < 10)
                diffTime="0"+hours+":0"+minutes+ ":0"+seconds;
            else
                diffTime="0"+hours+":0"+minutes+":"+seconds;
        }else{
            if(seconds < 10)
                diffTime="0"+hours+":"+minutes + ":0"+seconds;
            else
                diffTime="0"+hours+":"+minutes + ":"+seconds;
        }
            return diffTime;
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
            Intent intent = new Intent(this, MainActivity.class);
            String userName = this.getIntent().getStringExtra("用户名");
            intent.putExtra("userName", userName);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, TasksActivity.class);
            String userName = this.getIntent().getStringExtra("用户名");
            intent.putExtra("userName", userName);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this, ShowReport.class);
            String userName = this.getIntent().getStringExtra("用户名");
            intent.putExtra("userName", userName);
            startActivity(intent);
        }
        else if (id == R.id.nav_slideshow2)
        {
            Intent intent = new Intent(this, RankListActivity.class);
            String userName = this.getIntent().getStringExtra("用户名");
            intent.putExtra("userName", userName);
            startActivity(intent);
        }
        else if (id == R.id.nav_tools) {
            Intent intent=new Intent(MainActivity.this,Coin.class);
            String userName = this.getIntent().getStringExtra("用户名");
            intent.putExtra("userName", userName);
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

    // 进入主界面即要读取当前应用的背景图片ID，背景铃声ID，金币数
    private void ReadCoins() {
        userName = this.getIntent().getStringExtra("用户名");

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
                        System.out.println(userName);
                        System.out.println(result);
                        JSONObject demoJson = new JSONObject(result);
                        int coins = demoJson.getInt("coins");
                        int t_background = demoJson.getInt("t_background");
                        int t_alarm = demoJson.getInt("t_alarm");

                        MainActivity.coins = coins;
                        MainActivity.Current_BackImg = t_background;
                        MainActivity.Current_Alarm = t_alarm;
                        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

    // 金币更新函数，默认为将金币减去index
    private void WriteCoins(int index) {
        userName = this.getIntent().getStringExtra("用户名");
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


    public boolean hasStudyRoom(final String userName){
        final int[] resultCode = {0};
        Thread thread = new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/getRoom?user=" + userName;
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamTools.readInputStream(is);
                        System.out.println(result);
                        JSONObject jsonObject = new JSONObject(result);
                        resultCode[0] = jsonObject.getInt("code");
                    }
                } catch (Exception e) {
                    resultCode[0] = 0;
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resultCode[0] == 1;
    }

    public void enterStudyRoom(View view){
        String userName = this.getIntent().getStringExtra("用户名");

        if(hasStudyRoom(userName)) {
            //已经加入了自习室
            Intent intent = new Intent(this, StudyRoomActivity.class);
            intent.putExtra("userName", userName);
            startActivity(intent);
        }
        else{
            //没有加入自习室
            Intent intent = new Intent(this, JoinStudyRoomActivity.class);
            intent.putExtra("userName", userName);
            startActivity(intent);
        }
    }

}


