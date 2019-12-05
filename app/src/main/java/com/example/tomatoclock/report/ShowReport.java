package com.example.tomatoclock.report;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.content.Intent;
//import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;


import com.example.tomatoclock.Coin;
import com.example.tomatoclock.R;
import com.example.tomatoclock.StreamTools;
import com.example.tomatoclock.Task.TasksActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ShowReport extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener
{
    NoScrollViewPager mPager;
    public String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_report);

        //toolbar 和 drawer的实现
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.report);

        setSupportActionBar(toolbar);
        userName = this.getIntent().getStringExtra("userName");

        if(toolbar == null)
            System.out.println("null toolbar");
        // System.out.println("Local Time is " + getLocalDatetimeString("GMT+8"));
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //

        //fragment适配
        ReportByDayFragment fragmentByDay = ReportByDayFragment.newInstance();
        ReportByWeekFragment fragmentByWeek = ReportByWeekFragment.newInstance();

        Bundle bundle = new Bundle();
        bundle.putString("userName",userName);
        fragmentByDay.setArguments(bundle);
        fragmentByWeek.setArguments(bundle);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(fragmentByDay);
        fragmentList.add(fragmentByWeek);
        MainFragmentAdapter _Adapter = new MainFragmentAdapter(getSupportFragmentManager(), fragmentList);
        mPager = (NoScrollViewPager)findViewById(R.id.viewPager);
        mPager.setAdapter(_Adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(mPager);





    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, TasksActivity.class);
            startActivity(intent);
            //System.out.println("aaa");
        } else if (id == R.id.nav_slideshow) {
            finish();
            Intent intent = new Intent(this, ShowReport.class);
            startActivity(intent);

        } else if (id == R.id.nav_tools) {
            Intent intent=new Intent(this, Coin.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    public void jumpToDateChoose(View view)
//    {
//        //Date date = new Date();
//        Calendar mCalendar=Calendar.getInstance();
//        int mYear=mCalendar.get(Calendar.YEAR);
//        int mMonth=mCalendar.get(Calendar.MONTH);
//        int mDay=mCalendar.get(Calendar.DATE);
//        DatePickerDialog dialog = new DatePickerDialog(this,this,
//                mYear,
//                mMonth,
//                mDay
//        );
//        dialog.show();
//
//
//    }
//
//    @Override
//    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {//暂定，所有的更新都写在这里，肯定不是好方案，但进度优先
//
//
//
//
//
//
//    }

    public String getUserName()
    {
        return userName;
    }


}
