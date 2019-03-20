package com.example.dms.activity.common;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.R;
import com.example.dms.util.getMainsocket;
import com.mob.MobSDK;

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

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class Forgetpw_verify_Activity extends BaseActivity implements View.OnClickListener
{

    private String TAG = "Forgetpw_verify_Activity";

    private Toolbar tlb_forget_pw_verify;

    private TextInputLayout text_forget_pw_verify_tel;

    private TextInputLayout text_forget_pw_verify_idcod;

    private Button btn_forget_pw_verify_getIdcod;

    private Button btn_forget_pw_verify_sure;

    private String m_tel;

    private String m_idcode;

    private String m_send;

    private String m_receive;

    /** 验证码是否一致是否成功 */
    private String m_sameCode="";

    /**  倒计时 */
    private TimeCount m_TimeCount;

    /**  事件接收器 */
    public EventHandler eh;

    /**  短信验证API */
    private final String appKey="28b903adc5e76";
    private final String appSecret="c3ca823e3e86f059fd8d96a22a04ce6a";


    private void initView()
    {
        text_forget_pw_verify_tel = findViewById(R.id.forget_pw_verify_tel);
        text_forget_pw_verify_idcod = findViewById(R.id.forget_pw_verify_idcode);
        btn_forget_pw_verify_getIdcod = findViewById(R.id.forget_pw_verify_getIdcod);
        btn_forget_pw_verify_sure = findViewById(R.id.forget_pw_verify_sure);
    }

    private void initEvent()
    {
        eh = new EventHandler()          //创建事件接收器
        {
            @Override
            public void afterEvent(int event, int result, Object data)
            {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };

        //注册回调监听接口
        SMSSDK.registerEventHandler(eh);
    }
    private void setButtonClick()
    {
        btn_forget_pw_verify_getIdcod.setOnClickListener(this);                                               //获取验证码按钮事件
        btn_forget_pw_verify_sure.setOnClickListener(this);                                                //确定按钮事件
    }
    private void setToolBar()
    {
        tlb_forget_pw_verify = findViewById(R.id.forget_pw_verify_tlb);
        setSupportActionBar(tlb_forget_pw_verify);
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpw_verify);

        initView();
        setToolBar();
        setButtonClick();

        MobSDK.init(this, appKey, appSecret);//=SMSSDK.initSDK(this, "你的appkey", "你的appsecret");    //短信验证初始化
        m_TimeCount = new TimeCount(60000, 1000);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.forget_pw_verify_getIdcod:
                initEvent();
                m_tel = text_forget_pw_verify_tel.getEditText().getText().toString().trim();
                if(!m_tel.equals("") && checkTel(m_tel))
                {
                    Log.i(TAG,"手机号正确");
                    SMSSDK.getVerificationCode("+86",m_tel);            //获取验证码
                    Log.i(TAG,"验证码发送完毕");
                    m_TimeCount.start();
                }
                else
                {
                    Toast.makeText(this, "手机号有误", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.forget_pw_verify_sure:
                m_tel = text_forget_pw_verify_tel.getEditText().getText().toString().trim();
                m_idcode = text_forget_pw_verify_idcod.getEditText().getText().toString().trim();
                if(!m_tel.equals("") && checkTel(m_tel))
                {
                    if(!m_idcode.equals(""))
                    {
                        m_sameCode = "false";
                        SMSSDK.submitVerificationCode("+86", m_tel,m_idcode); //提交验证
                    }
                    //SMSSDK.getVerificationCode("+86",m_tel);            //获取验证码
                    //m_TimeCount.start();
                }
                else
                {
                    Toast.makeText(this, "手机号有误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    Handler rhander = new Handler()
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
                        try
                        {
                            JSONObject jsonObjectsend = new JSONObject();           //封装发送给服务器的json数据
                            jsonObjectsend.put("phone" ,m_tel);
                            m_send = jsonObjectsend.toString();
                            Log.e(TAG, m_send);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

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

                        Log.e(TAG, "run: 握手成功2" );
                        DataInputStream in = new DataInputStream(socket.getInputStream());   //创建输入流
                        m_receive= in.readUTF();                                            //读取服务器发送的信息
                        Log.e(TAG,m_receive);

                        if (m_receive.equals("true"))
                        {
                            Intent intent = new Intent(Forgetpw_verify_Activity.this,Forgetpw_reset_Activity.class);
                            intent.putExtra("tel",m_tel);
                            startActivity(intent);
                            m_TimeCount.cancel();
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
                    }
                }
            }).start();
        }
    };

    Handler Toasthandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Toast.makeText(Forgetpw_verify_Activity.this,"验证成功",Toast.LENGTH_LONG).show();
        }
    };


    Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            if(result == SMSSDK.RESULT_COMPLETE)
            {
                if(event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE)
                {
                    Toast.makeText(getApplicationContext(), "验证码提交成功",
                            Toast.LENGTH_SHORT).show();
                        try
                        {
                            JSONObject jsonObject = new JSONObject();           //封装发送给服务器的json数据
                            jsonObject.put("request_type" ,""+ 15);
                            m_send=jsonObject.toString();                       //将数据转为字符串
                            Log.e(TAG, m_send);
                            getMainsocket.connMainsocket(TAG,m_send,rhander);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                }
                else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE)   //正在获取验证码
                {
                    Toast.makeText(getApplicationContext(), "正在获取验证码", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };



    public boolean checkTel(String tel)
    {
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher matcher = p.matcher(tel);
        return matcher.matches();
    }

    /**     * 计时器     */
    class TimeCount extends CountDownTimer
    {
        public TimeCount(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long l)
        {
            btn_forget_pw_verify_getIdcod.setClickable(false);
            btn_forget_pw_verify_getIdcod.setText(l/1000 + "秒后重新获取");
        }
        @Override
        public void onFinish()
        {
            btn_forget_pw_verify_getIdcod.setClickable(true);
            btn_forget_pw_verify_getIdcod.setText("获取验证码");
        }
    }

    /**
     * 销毁事件接收器
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();

        SMSSDK.unregisterEventHandler(eh);
    }
}
