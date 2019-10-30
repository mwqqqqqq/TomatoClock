package com.example.tomatoclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.Toast;

public class Coin extends AppCompatActivity {
    private Button[] backb = new Button[MainActivity.BackImg_num];
    //private DrawerLayout layout = null;

    private TextView CoinBalance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

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
            if ( MainActivity.BackImg[i] == 1){
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
                if (v == backb[i] && MainActivity.BackImg[i] == 0 && MainActivity.coins >= 40) {
                    // 正常购买
                    MainActivity.coins -= 40;
                    CoinBalance.setText("金币数：" + String.valueOf(MainActivity.coins));
                    MainActivity.BackImg[i] = 1;
                    backb[i].setText("应用");
                    backb[i].setBackgroundColor(Color.parseColor("#6495ED"));
                    Toast.makeText(Coin.this,"购买成功", Toast.LENGTH_SHORT).show();
                }
                else if (v == backb[i] && MainActivity.BackImg[i] == 1){
                    // 正常应用, 直接更改出现问题，修改MainActivity处的全局变量Current_BackImg
                    MainActivity.Current_BackImg = i;
                    Toast.makeText(Coin.this,"背景更换成功", Toast.LENGTH_SHORT).show();
                }
                else if(v == backb[i] && MainActivity.BackImg[i] == 0 && MainActivity.coins < 40){
                    // 欲购买，金币不足
                    Toast.makeText(Coin.this,"金币不足", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
