package com.example.tomatoclock;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.example.tomatoclock.LoginActivity;
import com.example.tomatoclock.StreamTools;

import org.json.JSONObject;

public class RegisterActivity extends Activity implements OnCheckedChangeListener{
    protected static final int ERROR = 1;
    protected static final int SUCCESS = 2;
    private EditText et_pwd;
    private EditText et_user;
    private EditText et_name;
    private EditText et_apwd;
    private RadioButton radio0;
    private RadioButton radio1;
    private Button Button1;
    private RadioGroup rg;
    String temp="";
    private Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg){
            switch(msg.what){
                case SUCCESS:
                    Toast.makeText(RegisterActivity.this,(String)msg.obj, Toast.LENGTH_LONG).show();
                    System.out.println(msg.obj);
                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                    break;
                case ERROR:
                    Toast.makeText(RegisterActivity.this,(String)msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        };
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et_user = (EditText)findViewById(R.id.ruser);
        et_pwd=(EditText)findViewById(R.id.rpassword);
        et_name=(EditText)findViewById(R.id.rname);
        et_apwd=(EditText)findViewById(R.id.rrpassword);
        rg=(RadioGroup) findViewById(R.id.radioGroup1) ;
        rg.setOnCheckedChangeListener(this);
        Button1=(Button)findViewById(R.id.button2);

    }
    public void Button1(View view){
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void onCheckedChanged(RadioGroup group,int checkedId){
        switch(checkedId){
            case R.id.radioButton:
                temp="men";
                break;
            case R.id.radioButton2:
                temp="women";
                break;
        }

    }

    public void regin(View view) throws UnsupportedEncodingException{

        final String user=et_user.getText().toString().trim();
        final String pwd=et_pwd.getText().toString().trim();
        final String name=et_name.getText().toString().trim();
        final String apwd=et_apwd.getText().toString().trim();
        final String tem =URLEncoder.encode(URLEncoder.encode(temp, "UTF-8"), "UTF-8");
        if(TextUtils.isEmpty(user)){
            Toast.makeText(this,"用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(user.length()>30){
            Toast.makeText(this,"用户名长度超过限制", Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.length()>11){
            Toast.makeText(this,"姓名长度超过限制", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(this,"密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pwd.length()>11){
            Toast.makeText(this,"密码长度超过限制", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.equals(pwd, apwd)==false){
            Toast.makeText(this,"两次输入密码不同", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pwd.length()<6){
            Toast.makeText(this,"密码位数小于6安全等级太低", Toast.LENGTH_SHORT).show();
            return;
        }
        if(temp==""){
            Toast.makeText(this,"请选择性别", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(){
            public void run(){
                try{
                    String path="http://49.232.5.236:8080/test/Register?username="+user+"&realname="+name+"&pswd="+pwd+"&sex="+temp;
                    System.out.println(path);
                    URL url=new URL(path);
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code=conn.getResponseCode();
                    if(code==200){
                        System.out.println("开始了");
                        InputStream is=conn.getInputStream();
                        String result=StreamTools.readInputStream(is);
                        JSONObject demoJson = new JSONObject(result);
                        if(demoJson.getString("code").equals("1")){
                            Message msg=Message.obtain();
                            msg.what=SUCCESS;
                            msg.obj=demoJson.getString("message");
                            handler.sendMessage(msg);
                        }else{
                            Message msg=Message.obtain();
                            msg.what=ERROR;
                            msg.obj=demoJson.getString("message");
                            handler.sendMessage(msg);
                        }
                    }else{
                        Message msg=Message.obtain();
                        msg.what=ERROR;
                        msg.obj="电波无法到达呦";
                        handler.sendMessage(msg);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    Message msg=Message.obtain();
                    msg.what=ERROR;
                    handler.sendMessage(msg);
                }
            };
        }.start();
    }
}
