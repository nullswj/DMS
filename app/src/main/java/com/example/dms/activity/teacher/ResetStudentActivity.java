package com.example.dms.activity.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dms.R;
import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.util.getMainsocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public class ResetStudentActivity extends BaseActivity implements View.OnClickListener
{

    private String TAG = "ResetStudentActivity";

    private Toolbar tlb_reset_student_info;

    private Button btn_reset_status;

    private Button btn_reset_photo;

    private Button btn_reset_location;

    private String sid;

    private String m_send;

    private String m_receiver;

    private String m_ip = "47.106.151.249";

    private Button btn_unbind_id;

    private void initView()
    {
        btn_reset_location = findViewById(R.id.reset_stud_location);
        btn_reset_photo = findViewById(R.id.reset_stud_photo);
        btn_reset_status = findViewById(R.id.reset_stud_status);
        btn_unbind_id = findViewById(R.id.btn_unbind);
    }

    private void setToolBar()
    {
        tlb_reset_student_info = findViewById(R.id.tlb_reset_stud_info);
        setSupportActionBar(tlb_reset_student_info);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    private void setButtonClick()
    {
        btn_reset_status.setOnClickListener(this);
        btn_reset_photo.setOnClickListener(this);
        btn_reset_location.setOnClickListener(this);
        btn_unbind_id.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
        }
        return true;
    }

    private void getParameter()
    {
        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_student);

        initView();
        setToolBar();
        getParameter();
        setButtonClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.reset_stud_location:
                JSONObject jsonObject = new JSONObject();
                try
                {
                    jsonObject.put("request_type","14");//
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                m_send=jsonObject.toString();
                getMainsocket.connMainsocket(TAG,m_send,handler2);
                break;
            case R.id.reset_stud_photo:
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("request_type","14");//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                m_send=jsonObject1.toString();
                getMainsocket.connMainsocket(TAG,m_send,handler1);
                break;
            case R.id.reset_stud_status:
                JSONObject jsonObject2 = new JSONObject();
                try {
                    jsonObject2.put("request_type","14");//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                m_send=jsonObject2.toString();
                getMainsocket.connMainsocket(TAG,m_send,handler0);
                break;
            case R.id.btn_unbind:
                JSONObject jsonObject4 = new JSONObject();
                try {
                    jsonObject4.put("request_type","14");//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                m_send=jsonObject4.toString();
                getMainsocket.connMainsocket(TAG,m_send,handler4);
                break;
        }
    }
    Handler handler0 = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            final int reset_port = msg.arg1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObjectsend = new JSONObject();
                    try
                    {
                        jsonObjectsend.put("type","1");
                        jsonObjectsend.put("sid",sid);
                        m_send = jsonObjectsend.toString();

                        Socket socket = new Socket();
                        SocketAddress address = new InetSocketAddress(m_ip,reset_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);
                        out.close();
                        socket.close();

                        Message message = new Message();
                        message.what = 0;
                        ToastHandle.sendMessage(message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };
    Handler handler1 = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            final int reset_port = msg.arg1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObjectsend = new JSONObject();
                    try
                    {
                        jsonObjectsend.put("type","2");
                        jsonObjectsend.put("sid",sid);
                        m_send = jsonObjectsend.toString();

                        Socket socket = new Socket();
                        SocketAddress address = new InetSocketAddress(m_ip,reset_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);
                        out.close();
                        socket.close();
                        Message message = new Message();
                        message.what = 0;
                        ToastHandle.sendMessage(message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };
    Handler handler2 = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            final int reset_port = msg.arg1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObjectsend = new JSONObject();
                    try
                    {
                        jsonObjectsend.put("type","3");
                        jsonObjectsend.put("sid",sid);
                        m_send = jsonObjectsend.toString();

                        Socket socket = new Socket();
                        SocketAddress address = new InetSocketAddress(m_ip,reset_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);
                        out.close();
                        socket.close();
                        Message message = new Message();
                        message.what = 0;
                        ToastHandle.sendMessage(message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };

    Handler handler4 = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            final int reset_port = msg.arg1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObjectsend = new JSONObject();
                    try
                    {
                        jsonObjectsend.put("type","0");
                        jsonObjectsend.put("sid",sid);
                        m_send = jsonObjectsend.toString();

                        Socket socket = new Socket();
                        SocketAddress address = new InetSocketAddress(m_ip,reset_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);
                        out.close();
                        socket.close();
                        Message message = new Message();
                        message.what = 0;
                        ToastHandle.sendMessage(message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };
    Handler ToastHandle = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(ResetStudentActivity.this,"重置成功",Toast.LENGTH_LONG).show();
        }
    };
}
