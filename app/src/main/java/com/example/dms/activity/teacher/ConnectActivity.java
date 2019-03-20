package com.example.dms.activity.teacher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.dms.R;
import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.hyphenatechat.CallManager;
import com.example.dms.hyphenatechat.VideoCallActivity;
import com.example.dms.hyphenatechat.VoiceCallActivity;
import com.example.dms.util.getMainsocket;
import com.vmloft.develop.library.tools.utils.VMSPUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.example.dms.util.Encryption.md5;

public class ConnectActivity extends BaseActivity implements View.OnClickListener
{

    private String TAG = "ConnectActivity";

    private Toolbar tbl_connect;

    private Button btn_dialog;

    private Button btn_shiping;

    private Button btn_yuyin;

    private String tel;

    private String toUsername;

    private String sid;

    private String m_send;

    private void initView()
    {
        btn_dialog = findViewById(R.id.btn_dialog);
        btn_shiping =findViewById(R.id.btn_video);
        btn_yuyin = findViewById(R.id.btn_yuyin);
    }

    private void setButtonClick()
    {
        btn_dialog.setOnClickListener(this);                                               //获取验证码按钮事件
        btn_shiping.setOnClickListener(this);
        btn_yuyin.setOnClickListener(this);//确定按钮事件
    }

    private void setToolBar()
    {
        tbl_connect = findViewById(R.id.tlb_connect);
        setSupportActionBar(tbl_connect);
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
        setContentView(R.layout.activity_connect);

        initView();
        setToolBar();
        setButtonClick();

        Intent intent = getIntent();
        tel = intent.getStringExtra("tel");
        toUsername = intent.getStringExtra("toUsername");
        sid = intent.getStringExtra("sid");

    }

    private void call()
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+tel));
            startActivity(intent);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    call();
                }
                else
                {
                    Toast.makeText(this,"你拒绝了拨号权限申请，请允许",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_dialog:
                if(ContextCompat.checkSelfPermission(ConnectActivity.this,Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(ConnectActivity.this,new String[]{Manifest.permission.CALL_PHONE},1);
                }
                else
                {
                    call();
                }
                break;
            case R.id.btn_yuyin:

                //jsonPost.put("user_name",gs_loginUser);
                try
                {
                    JSONObject jsonPost = new JSONObject();
                    jsonPost.put("request_type","16");
                    jsonPost.put("s_id",sid);
                    m_send = jsonPost.toString();
                    getMainsocket.connMainsocket(TAG,m_send,handler);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }


                Intent intent = new Intent(ConnectActivity.this, VoiceCallActivity.class);
                CallManager.getInstance().setChatId(toUsername);
                CallManager.getInstance().setInComingCall(false);
                CallManager.getInstance().setCallType(CallManager.CallType.VOICE);
                startActivity(intent);
                break;
            case R.id.btn_video:
                try
                {
                    JSONObject jsonPost = new JSONObject();
                    jsonPost.put("request_type","16");
                    jsonPost.put("s_id",sid);
                    m_send = jsonPost.toString();
                    getMainsocket.connMainsocket(TAG,m_send,handler);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                VMSPUtil.put("toUsername", toUsername);
                Intent intent1 = new Intent(ConnectActivity.this, VideoCallActivity.class);
                CallManager.getInstance().setChatId(toUsername);
                CallManager.getInstance().setInComingCall(false);
                CallManager.getInstance().setCallType(CallManager.CallType.VIDEO);
                startActivity(intent1);
                break;
        }
    }
    Handler handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg) {
            final int connect_port = msg.arg1;          //端口号
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject jsonObject_send = new JSONObject();                                   //json封装数据
                        jsonObject_send.put("sid",""+sid);
                        m_send  = jsonObject_send.toString();
                        Socket socket = new Socket("47.106.151.249",connect_port);      //创建socket连接
                        socket.setSoTimeout(10000);                                          //设置超时

                        Log.e("zqc",m_send);
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);
                        out.close();
                        socket.close();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    };
}
