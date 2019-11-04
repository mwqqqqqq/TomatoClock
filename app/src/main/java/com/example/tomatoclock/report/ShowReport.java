package com.example.tomatoclock.report;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
//import android.icu.util.Calendar;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;


import com.example.tomatoclock.Coin;
import com.example.tomatoclock.MainActivity;
import com.example.tomatoclock.R;
import com.example.tomatoclock.Task.TasksActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class ShowReport extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,NavigationView.OnNavigationItemSelectedListener
{
    MyLineChartView chartView;
    List<String> xValues;   //x轴数据集合
    List<Integer> yValues;  //y轴数据集合
    List<Focus> focusList;

    TextView reportTitle;
    TextView focusTodayText;
    TextView reportAbstract;
    TextView focusTimesText;
    TextView interruptTimesText;
    TextView continuousFocusDaysText;
    TextView focusRecordTitle;

    RecyclerView focusRecords;


    ImageButton imageButton;

    int year,month,day;

    String stringOfCurve;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_report);

        //文字，标题，等待风格统一
        //reportTitle = findViewById(R.id.reportTitle);
        //Spannable focusTodayTextStr =  new SpannableString("简报");

        //adding
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if(toolbar == null)
            System.out.println("null toolbar");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //adding over



        chartView = (MyLineChartView) findViewById(R.id.linechartview);
        xValues = new ArrayList<>();
        yValues = new ArrayList<>();
        focusList = new ArrayList<>();

        // xy轴集合自己添加数据，测试随便数据
        for(int i = 0;i < 5;i++)
            xValues.add(String.valueOf(i));
        //xValues.add("2");
        for(int i = 0;i < 5;i++)
            yValues.add(2);
        //yValues.add(33);
        chartView.setXValues(xValues);
        chartView.setYValues(yValues);
        //转化为开始时间和分钟数
        //开始时间的小时和分钟数，分钟数除以60得到小数点后位数
        String givenHour = "10";
        String givenMinute = "30";
        int dura = 40;

        int startHour = Integer.parseInt(givenHour);
        int startMinute = Integer.parseInt(givenMinute);

        Focus focus1 = new Focus();
        focus1.startHour = startHour;
        focus1.startMinute = startMinute;
        focus1.dura = dura;
        focusList.add(focus1);
        Focus focus2 = new Focus();
        focus2.startHour = 13;
        focus2.startMinute=40;
        focus2.dura = 45;
        focusList.add(focus2);
        chartView.setFocusList(focusList);
        //


        //文字框部分，尽量把相关属性都往
        focusTodayText = findViewById(R.id.focusTodayText);
        //TextPaint paint = focusTodayText.getPaint();
        //paint.setFakeBoldText(true);
        String focusTodayTextStr1 = "今日专注时长:\n";
        String focusTodayTextStr2 = "30";
        String focusTodayTextStr3 = "分钟";

        Spannable focusTodayTextStr =  new SpannableString(focusTodayTextStr1+focusTodayTextStr2+focusTodayTextStr3);
        focusTodayTextStr.setSpan(new AbsoluteSizeSpan(80),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusTodayTextStr.setSpan(new ForegroundColorSpan(Color.BLACK),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        //focusTodayText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        focusTodayText.setText(focusTodayTextStr);//此处根据后续取数据情况修改
        //focusTodayText.setBackgroundResource(R.drawable.text_view_border);


        //日历图标部分
        imageButton = findViewById(R.id.imageButton);
        imageButton.setScaleType(ImageView.ScaleType.FIT_XY);

        //文字：摘要
        reportAbstract = findViewById(R.id.reportAbstract);
        Spannable reportAbstractStr = new SpannableString("摘要");
        reportAbstractStr.setSpan(new AbsoluteSizeSpan(80),0,2,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        reportAbstractStr.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,2,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        reportAbstract.setText(reportAbstractStr);


        //卡片一：今日专注次数
        focusTimesText = findViewById(R.id.focusTimesText);
        String reportFoucsTimesStr1 = " 专注次数：\n\n\n";
        String reportFocusTimesStr2 = " 3";//从服务器端获取数据，填在这里
        String reportFocusTimesStr3 = "次";
        Spannable reportFoucsTimesStr = new SpannableString(reportFoucsTimesStr1+reportFocusTimesStr2+reportFocusTimesStr3);
        reportFoucsTimesStr.setSpan(new AbsoluteSizeSpan(80),reportFoucsTimesStr1.length(),reportFoucsTimesStr1.length()+reportFocusTimesStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusTimesText.setText(reportFoucsTimesStr);//此处根据后续取数据情况修改
        focusTimesText.setBackgroundResource(R.drawable.text_view_border);

        //卡片二：今日打断次数
        interruptTimesText = findViewById(R.id.interruptTimesText);
        String reportInterruptTimesStr1 = " 打断次数： \n\n\n";
        String reportInterruptTimesStr2 = " 5";//从服务器端获取数据，填在这里
        String reportInterruptTimesStr3 = "次";
        Spannable reportInterruptTimesStr = new SpannableString(reportInterruptTimesStr1+reportInterruptTimesStr2+reportInterruptTimesStr3);
        reportInterruptTimesStr.setSpan(new AbsoluteSizeSpan(80),reportInterruptTimesStr1.length(),reportInterruptTimesStr1.length()+reportInterruptTimesStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        interruptTimesText.setText(reportInterruptTimesStr);
        focusTimesText.setBackgroundResource(R.drawable.text_view_border);

        //卡片三：连续专注天数
        continuousFocusDaysText = findViewById(R.id.continuousFocusDaysText);
        String continuousFocusDaysStr1 = " 连续专注天数： \n\n\n";
        String continuousFocusDaysStr2 = " 3";//从服务器端获取数据，填在这里
        String continuousFocusDaysStr3 = "天";
        Spannable continuousFocusDaysStr = new SpannableString(continuousFocusDaysStr1+continuousFocusDaysStr2+continuousFocusDaysStr3);
        continuousFocusDaysStr.setSpan(new AbsoluteSizeSpan(80),continuousFocusDaysStr1.length(),continuousFocusDaysStr1.length()+continuousFocusDaysStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        continuousFocusDaysText.setText(continuousFocusDaysStr);
        continuousFocusDaysText.setBackgroundResource(R.drawable.text_view_border);

        //专注记录标题
        focusRecordTitle = findViewById(R.id.focusRecordTitle);
        String focusRecordTitleStr1 = "今日专注记录";
        Spannable focusRecordTitleStr = new SpannableString(focusRecordTitleStr1);
        focusRecordTitleStr.setSpan(new AbsoluteSizeSpan(80),0,focusRecordTitleStr1.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusRecordTitleStr.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,focusRecordTitleStr1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusRecordTitle.setText(focusRecordTitleStr);

        //专注记录实现（cardView+RecycleView)https://blog.csdn.net/u014752325/article/details/51384727
        focusRecords = findViewById(R.id.focusRecords);//外面加一层relativeLayout
        //focusRecords.setMinimumHeight();
        //测试
        List<Focus> afocus = new ArrayList<>();
        Focus f1 = new Focus();
        f1.startHour = 2;
        f1.startMinute = 30;
        Focus f2 = new Focus();
        f2.startHour = 3;
        f2.startMinute = 30;
        Focus f3 = new Focus();
        f3.startHour = 4;
        f3.startMinute = 40;
        Focus f4 = new Focus();
        f4.startHour = 5;
        f4.startMinute = 50;
        afocus.add(f1);
        afocus.add(f2);
        afocus.add(f3);
        afocus.add(f4);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        focusRecords.setLayoutManager(layoutManager);
        FocusRecordsAdapter fa = new FocusRecordsAdapter(afocus);
        focusRecords.setAdapter(fa);
        //
        //focusRecords.




    }

    public void jumpToDateChoose(View view)
    {
        //Date date = new Date();
        Calendar mCalendar=Calendar.getInstance();
        int mYear=mCalendar.get(Calendar.YEAR);
        int mMonth=mCalendar.get(Calendar.MONTH);
        int mDay=mCalendar.get(Calendar.DATE);
        DatePickerDialog dialog = new DatePickerDialog(this,this,
                mYear,
                mMonth,
                mDay
        );
        dialog.show();


    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {//暂定，所有的更新都写在这里，肯定不是好方案，但进度优先
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;

        //根据日期取数

        // *假设取到的新数*
        int newFocusTime = 78;
        List<Focus> newFocusList = new ArrayList<>();
        Focus f1 = new Focus();
        f1.startHour = 15;
        f1.startMinute = 30;
        f1.dura = 10;
        Focus f2 = new Focus();
        f2.startHour = 13;
        f2.startMinute = 30;
        f2.dura = 20;
        Focus f3 = new Focus();
        f3.startHour = 14;
        f3.startMinute = 40;
        f3.dura = 30;
        Focus f4 = new Focus();
        f4.startHour = 16;
        f4.startMinute = 50;
        f4.dura = 40;
        newFocusList.add(f1);
        newFocusList.add(f2);
        newFocusList.add(f3);
        newFocusList.add(f4);

        int newFocusTimes = 12;
        int newInterruptTimes = 20;
        int newContinuousDays = 11;


        //*



        //进行更新
        //更新最上方统计图
        chartView.setFocusList(newFocusList);
        chartView.invalidate();
        FocusRecordsAdapter fa = new FocusRecordsAdapter(newFocusList);
        focusRecords.setAdapter(fa);

        //更新专注时间
        updateFocusTime(newFocusTime);
        //更新专注次数,打断次数，连续专注天数，注意上面初始化部分可能也可以用这个重构
        updateFocusTimes(newFocusTimes);
        updateInterruptTimes(newInterruptTimes);
        updateContinuousDays(newContinuousDays);





    }

    private void updateFocusTime(int newFocusTime) {
        focusTodayText = findViewById(R.id.focusTodayText);
        //TextPaint paint = focusTodayText.getPaint();
        //paint.setFakeBoldText(true);
        String focusTodayTextStr1 = "今日专注时长:\n";
        String focusTodayTextStr2 = String.valueOf(newFocusTime);
        String focusTodayTextStr3 = "分钟";

        Spannable focusTodayTextStr =  new SpannableString(focusTodayTextStr1+focusTodayTextStr2+focusTodayTextStr3);
        focusTodayTextStr.setSpan(new AbsoluteSizeSpan(80),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusTodayTextStr.setSpan(new ForegroundColorSpan(Color.BLACK),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        //focusTodayText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        focusTodayText.setText(focusTodayTextStr);//此处根据后续取数据情况修改
        //focusTodayText.setBackgroundResource(R.drawable.text_view_border);
    }

    private void updateFocusTimes(int newFocusTimes) {
        focusTimesText = findViewById(R.id.focusTimesText);
        String reportFoucsTimesStr1 = " 专注次数：\n\n\n";
        String reportFocusTimesStr2 = " "+String.valueOf(newFocusTimes);;//从服务器端获取数据，填在这里
        String reportFocusTimesStr3 = "次";
        Spannable reportFoucsTimesStr = new SpannableString(reportFoucsTimesStr1+reportFocusTimesStr2+reportFocusTimesStr3);
        reportFoucsTimesStr.setSpan(new AbsoluteSizeSpan(80),reportFoucsTimesStr1.length(),reportFoucsTimesStr1.length()+reportFocusTimesStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusTimesText.setText(reportFoucsTimesStr);//此处根据后续取数据情况修改
        focusTimesText.setBackgroundResource(R.drawable.text_view_border);
    }

    private void updateInterruptTimes(int newInterruptTimes){
        interruptTimesText = findViewById(R.id.interruptTimesText);
        String reportInterruptTimesStr1 = " 打断次数： \n\n\n";
        String reportInterruptTimesStr2 = " "+String.valueOf(newInterruptTimes);//从服务器端获取数据，填在这里
        String reportInterruptTimesStr3 = "次";
        Spannable reportInterruptTimesStr = new SpannableString(reportInterruptTimesStr1+reportInterruptTimesStr2+reportInterruptTimesStr3);
        reportInterruptTimesStr.setSpan(new AbsoluteSizeSpan(80),reportInterruptTimesStr1.length(),reportInterruptTimesStr1.length()+reportInterruptTimesStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        interruptTimesText.setText(reportInterruptTimesStr);
        focusTimesText.setBackgroundResource(R.drawable.text_view_border);

    }

    private void updateContinuousDays(int newContinuousDays)
    {
        continuousFocusDaysText = findViewById(R.id.continuousFocusDaysText);
        String continuousFocusDaysStr1 = " 连续专注天数： \n\n\n";
        String continuousFocusDaysStr2 = " "+String.valueOf(newContinuousDays);//从服务器端获取数据，填在这里
        String continuousFocusDaysStr3 = "天";
        Spannable continuousFocusDaysStr = new SpannableString(continuousFocusDaysStr1+continuousFocusDaysStr2+continuousFocusDaysStr3);
        continuousFocusDaysStr.setSpan(new AbsoluteSizeSpan(80),continuousFocusDaysStr1.length(),continuousFocusDaysStr1.length()+continuousFocusDaysStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        continuousFocusDaysText.setText(continuousFocusDaysStr);
        continuousFocusDaysText.setBackgroundResource(R.drawable.text_view_border);

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




}
