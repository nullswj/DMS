package com.example.dms.activity.student.student_work;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import static com.example.dms.activity.common.LoginActivity.gs_loginUser;

public class StudentLeavewordActivity extends BaseActivity
{
    private String TAG = "StudentLeavewordActivity";

    private Toolbar tbl_leaveword;

    private TextInputLayout text_stud_leaveword;

    private Button btn_send_leave_word;

    private String text_leaveword;

    private String m_send;

    private String m_ip = "47.106.151.249";

    private void setToolBar()
    {
        tbl_leaveword = findViewById(R.id.tlb_stud_leave_word);
        setSupportActionBar(tbl_leaveword);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                showBackDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initView()
    {
        text_stud_leaveword = findViewById(R.id.text_stud_leaveword);
        btn_send_leave_word = findViewById(R.id.text_leaveword_send);
    }

    private void showBackDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(StudentLeavewordActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("留言");
        bindDialog.setMessage("放弃未保存的输入，确认返回？");

        bindDialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });

        bindDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        bindDialog.setCancelable(false);
        AlertDialog dialog = bindDialog.create();
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            showBackDialog();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_leaveword);

        setToolBar();
        initView();

        btn_send_leave_word.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                text_leaveword = text_stud_leaveword.getEditText().getText().toString().trim();
                try //封装信息
                {
                    Log.e(TAG, "onClick: "+"点击" );
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("request_type","13");//
                    m_send=jsonObject.toString();
                    getMainsocket.connMainsocket(TAG,m_send,handler);
                    Log.e(TAG, "onClick: "+"主端口成功" );
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            final int release_port = msg.arg1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        JSONObject jsonObjectsend = new JSONObject();
                        jsonObjectsend.put("sid",gs_loginUser.getsId());
                        jsonObjectsend.put("message",text_leaveword);

                        m_send = jsonObjectsend.toString();

                        Log.e(TAG, "run: "+m_send );

                        Socket socket = new Socket();
                        SocketAddress address = new InetSocketAddress(m_ip,release_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        Log.e(TAG, "run: "+"握手成功" );
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);

                        Log.e(TAG, "run: "+"发送成功" );
                        out.close();
                        socket.close();

                        Message message = new Message();
                        message.what = 0;
                        handlerToast.sendMessage(message);


                        finish();


                    }
                    catch (JSONException e)
                    {
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

    Handler handlerToast = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Toast.makeText(StudentLeavewordActivity.this,"留言成功",Toast.LENGTH_LONG).show();
        }
    };

}
