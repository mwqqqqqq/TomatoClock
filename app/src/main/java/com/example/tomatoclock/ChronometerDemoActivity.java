package com.example.tomatoclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;


public class ChronometerDemoActivity extends Activity {
    private int startTime = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Chronometer chronometer = findViewById(R.id.chronometer);

        Button btnStart = findViewById(R.id.btnStart);

        Button btnStop = findViewById(R.id.btnStop);

        Button btnRest = findViewById(R.id.btnReset);

        final EditText edtSetTime = findViewById(R.id.edt_settime);

        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                System.out.println("--开始记时---");
                String ss = edtSetTime.getText().toString();
                if (!(ss.equals(""))) {
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
        //builder.setIcon(R.drawable.eb28d25);
        builder.setTitle("警告").setMessage("时间到")
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
