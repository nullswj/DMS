package com.example.dms.activity.common;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.R;
import com.example.dms.util.getMainsocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.dms.util.Encryption.md5;
import static com.example.dms.util.StringCheck.checkPasswordStrength;

public class Forgetpw_reset_Activity extends BaseActivity implements View.OnClickListener
{

    private String TAG = "Forgetpw_reset_Activity";

    private Toolbar tlb_forgetpw_reset;

    private TextInputLayout text_forgetpw_reset_pw;

    private TextInputLayout text_forgetpw_reset_pw_r;

    private Button btn_forgetpw_reset_sure;

    private String pw;

    private String pw_r;

    private String m_send;

    private String m_receive;

    private String phone;

    private void initView()
    {
        text_forgetpw_reset_pw = findViewById(R.id.forget_pw_reset_pw);
        text_forgetpw_reset_pw_r = findViewById(R.id.forget_pw_reset_pw_r);
        btn_forgetpw_reset_sure = findViewById(R.id.forget_pw_reset_sure);
    }

    private void setToolBar()
    {
        tlb_forgetpw_reset = findViewById(R.id.forget_pw_reset_tlb);
        setSupportActionBar(tlb_forgetpw_reset);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setButtonClick()
    {
        btn_forgetpw_reset_sure.setOnClickListener(this);                                               //获取验证码按钮事件         //确定按钮事件
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpw_reset_);
        Intent intent = getIntent();
        phone = intent.getStringExtra("tel");

        initView();
        setToolBar();
        setButtonClick();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.forget_pw_reset_sure:
                pw = text_forgetpw_reset_pw.getEditText().getText().toString().trim();
                pw_r = text_forgetpw_reset_pw_r.getEditText().getText().toString().trim();

                if(checkPassword(pw))
                {
                    if(checkPasswordStrength(pw))
                    {
                        if(pw.equals(pw_r))
                        {
                            try
                            {
                                JSONObject jsonObject = new JSONObject();           //封装发送给服务器的json数据
                                jsonObject.put("request_type" ,""+10);

                                m_send=jsonObject.toString();                       //将数据转为字符串
                                Log.e(TAG, m_send);
                                getMainsocket.connMainsocket(TAG,m_send,handler);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Toast.makeText(Forgetpw_reset_Activity.this,"两次密码不一致",Toast.LENGTH_LONG).show();

                        }
                    }
                    else
                    {
                        Toast.makeText(Forgetpw_reset_Activity.this,"密码必须含有数字，大小写字母，下划线中的三种",Toast.LENGTH_LONG).show();

                    }
                }
                else
                {
                    Toast.makeText(Forgetpw_reset_Activity.this,"密码只能由数字，大小写字母，下划线构成",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    Handler Toasthandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(Forgetpw_reset_Activity.this,"重置成功",Toast.LENGTH_LONG).show();
        }
    };

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            final int port = msg.arg1;
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject jsonObjectsend = new JSONObject();           //封装发送给服务器的json数据
                        jsonObjectsend.put("phone",phone);
                        String md5pass = md5(pw);
                        jsonObjectsend.put("password" ,md5pass);
                        m_send = jsonObjectsend.toString();
                        Log.e(TAG, m_send);

                        Socket socket = new Socket();                   //创建socket连接
                        SocketAddress address = new InetSocketAddress("47.106.151.249",port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(30000);

                        Log.e(TAG, "握手成功" );


                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);

                        Log.e(TAG, "run: 发送成功" );

                        out.close();
                        socket.close();

                        Thread.sleep(1000);

                        socket = new Socket("47.106.151.249",port);      //创建socket连接
                        socket.setSoTimeout(10000);                                          //设置超时

                        DataInputStream in = new DataInputStream(socket.getInputStream());   //创建输入流
                        m_receive= in.readUTF();                                            //读取服务器发送的信息

                        Log.e(TAG,m_receive);

                        if (m_receive.equals("true"))
                        {
                            Intent intent = new Intent(Forgetpw_reset_Activity.this,LoginActivity.class);
                            startActivity(intent);
                            in.close();
                            socket.close();
                            Message message = new Message();
                            message.what = 0;
                            Toasthandler.sendMessage(message);
                            finish();
                        }
                    }
                    catch (SocketException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };
    public boolean checkPassword(String user)
    {
        Pattern p = Pattern.compile("^[0-9a-zA-Z_]{1,}$");
        Matcher matcher = p.matcher(user);
        return matcher.matches();
    }


}
