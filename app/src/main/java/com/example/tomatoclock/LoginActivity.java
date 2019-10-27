package com.example.tomatoclock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
//import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
    protected static final int ERROR = 1;
    protected static final int SUCCESS = 2;
    protected static final int SUCCESSS = 0;
    BufferedReader bufferReader;
    private EditText account;
    private EditText password;
    private Button   register;
    private Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg){
            switch(msg.what){
                case SUCCESS:
                    Toast.makeText(LoginActivity.this,(String)msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    Toast.makeText(LoginActivity.this,"发送失败", Toast.LENGTH_LONG).show();
                    break;
            }
        };
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        account = (EditText)findViewById(R.id.user);
        password=(EditText)findViewById(R.id.password);
        register = (Button)findViewById(R.id.login);
    }
    public void register(View view){
        Intent intent=new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    public void login(View view){
        final String user=account.getText().toString().trim();
        final String pwd=password.getText().toString().trim();
        if(TextUtils.isEmpty(user)){
            Toast.makeText(this,"用户名为空登录失败", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(this,"密码为空登陆失败", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(){
            Map<String, Object> listItem = new HashMap<String, Object>();
            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
            public void run(){
                try{
                    String path="http://49.232.5.236:8080/test/Login?username="+user+"&pswd="+pwd;
                    URL url=new URL(path);
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code=conn.getResponseCode();
                    if(code==200){
                        InputStream is=conn.getInputStream();
                        String result=StreamTools.readInputStream(is);
                        JSONObject demoJson = new JSONObject(result);
                        if(demoJson.getString("code").equals("1")){
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("用户名",demoJson.getString("user"));
                            System.out.println(demoJson.getString("user"));
                            intent.putExtra("密码",demoJson.getString("pwd"));
                            System.out.println(demoJson.getString("pwd"));
                            intent.putExtra("真实姓名",demoJson.getString("name"));
                            System.out.println(demoJson.getString("name"));
                            intent.putExtra("性别",demoJson.getString("sex"));
                            System.out.println(demoJson.getString("sex"));
                            startActivity(intent);
                        }else{
                            Message msg=Message.obtain();
                            msg.what=SUCCESS;
                            msg.obj=result;
                            handler.sendMessage(msg);
                        }
                    }else{
                        Message msg=Message.obtain();
                        msg.what=ERROR;
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

