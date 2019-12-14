package com.example.tomatoclock.rankList;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.tomatoclock.Coin;
import com.example.tomatoclock.MainActivity;
import com.example.tomatoclock.R;
import com.example.tomatoclock.Task.TasksActivity;
import com.example.tomatoclock.report.ShowReport;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class RankListActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{


    RecyclerView rankListFocusTime;
    TextView rankUserFocusTime;
    RecyclerView  rankListFocusDegree;
    TextView rankUserFocusLevel;//总专注时间除以（专注次数+打断次数）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_list);

        //toolbar 和 drawer的实现
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("排行榜");

        //setSupportActionBar(toolbar);
        //String userName = this.getIntent().getStringExtra("userName");

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

        rankListFocusTime = findViewById(R.id.rankListFocusTime);//外面加一层relativeLayout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rankListFocusTime.setLayoutManager(layoutManager);
        List<FocusTimeRank> rankList = new ArrayList<>();
        FocusTimeRank fr = new FocusTimeRank();
        fr.focusTime = 7200;
        fr.rank = 1;
        fr.userName = "Eren";
        rankList.add(fr);
        FocusTimeRank fr2 = new FocusTimeRank();
        fr2.focusTime = 3600;
        fr2.rank = 2;
        fr2.userName = "Jean";
        rankList.add(fr2);
        FocusTimeRankListAdapter fa = new FocusTimeRankListAdapter(rankList);
        rankListFocusTime.setAdapter(fa);


        rankUserFocusTime = findViewById(R.id.textView9);
        String focusTodayTextStr1 = "专注时长排名:\n";
        String focusTodayTextStr2 = "30/100";
        //String focusTodayTextStr3 = "分钟";

        Spannable focusTodayTextStr =  new SpannableString(focusTodayTextStr1+focusTodayTextStr2);
        focusTodayTextStr.setSpan(new AbsoluteSizeSpan(80),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusTodayTextStr.setSpan(new ForegroundColorSpan(Color.BLACK),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        //focusTodayText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        rankUserFocusTime.setText(focusTodayTextStr);//此处根据后续取数据情况修改

        rankListFocusDegree = findViewById(R.id.rankListFocusDegree);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        rankListFocusDegree.setLayoutManager(layoutManager2);
        List<FocusTimeRank> rankList2 = new ArrayList<>();
        FocusTimeRank frrr = new FocusTimeRank();
        frrr.userName = "Eren";
        frrr.focusTime = 27.0;
        frrr.rank = 1;
        rankList2.add(frrr);
        FocusTimeRank frrr2 = new FocusTimeRank();
        frrr2.userName = "Jean";
        frrr2.focusTime = 19.0;
        frrr2.rank = 2;
        rankList2.add(frrr2);
        FocusTimeRankListAdapter fa2 = new FocusTimeRankListAdapter(rankList2);
        rankListFocusDegree.setAdapter(fa2);
        System.out.println("rankList2.size()="+rankList2.size());

        rankUserFocusLevel = findViewById(R.id.textView11);
        String focusDegreeStr1 = "专注程度排名:\n";
        String focusDegreeStr2 = "30/100";
        Spannable focusDegreeStr = new SpannableString(focusDegreeStr1+focusDegreeStr2);
        focusDegreeStr.setSpan(new AbsoluteSizeSpan(80),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusDegreeStr.setSpan(new ForegroundColorSpan(Color.BLACK),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        rankUserFocusLevel.setText(focusDegreeStr);

    }

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
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, TasksActivity.class);
            String userName = this.getIntent().getStringExtra("userName");
            intent.putExtra("userName", userName);
            intent.putExtra("用户名", userName);
            startActivity(intent);
            //System.out.println("aaa");
        } else if (id == R.id.nav_slideshow) {
            finish();
            Intent intent = new Intent(this, ShowReport.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow2) {
            Intent intent = new Intent(this, RankListActivity.class);
            String userName = this.getIntent().getStringExtra("userName");
            intent.putExtra("userName", userName);
            intent.putExtra("用户名", userName);
            startActivity(intent);

        }else if (id == R.id.nav_tools) {
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
