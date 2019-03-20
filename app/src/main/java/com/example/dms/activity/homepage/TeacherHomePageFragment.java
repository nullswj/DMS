package com.example.dms.activity.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.dms.R;
import com.example.dms.activity.common.LoginActivity;
import com.example.dms.activity.teacher.CheckStudentInfoActivity;
import com.example.dms.activity.teacher.ReleaseCheckActivity;
import com.example.dms.activity.teacher.ResultActivity;
import com.example.dms.activity.teacher.TeacherMessageActivity;
import com.example.dms.activity.teacher.TeacherSendNoticeActivity;
import com.example.dms.db.Collegedb;
import com.example.dms.util.getMainsocket;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMSPUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import static com.example.dms.activity.common.LoginActivity.gt_loginUser;
import static com.example.dms.util.Encryption.md5;


public class TeacherHomePageFragment extends Fragment {

    private String TAG = "TeacherHomePageFragment";
    private Button btn_send;
    private Button btn_send_notice;
    private Button btn_check_result;
    private Button btn_check_leave_word;
    private Button btn_check_stud_list;
    private String m_send;
    private String m_receive;
    private String m_ip = "47.106.151.249";

    private void loginhyphenatechat()
    {
        //username = g_loginUser.getS_id();
        String T_ID  = gt_loginUser.gettId();
        final String password = md5("shabi_wangluyao");
        StringBuffer sb = null;
        int len = T_ID.length();
        while (len < 12)
        {
            sb = new StringBuffer();
            sb.append("0").append(T_ID);
            T_ID = sb.toString();
            len = T_ID.length();
        }
        T_ID = "t"+T_ID;
        final String account = T_ID;
        EMClient.getInstance().login(account, password, new EMCallBack() {
            @Override
            public void onSuccess()
            {
                VMLog.i("login success");
                VMSPUtil.put("username", account);
                VMSPUtil.put("password", password);
            }
            @Override
            public void onError(final int i, final String s) {
                String errorMsg = "login error: " + i + "; " + s;
                VMLog.i(errorMsg);
            }
            @Override
            public void onProgress(int i, String s) {
            }
        });


        Log.e(TAG, "loginhyphenatechat: 登陆成功" );
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        loginhyphenatechat();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        btn_send = getActivity().findViewById(R.id.btn_send);
        btn_send_notice = getActivity().findViewById(R.id.btn_send_notice);
        btn_check_result = getActivity().findViewById(R.id.btn_checkresult);
        btn_check_leave_word = getActivity().findViewById(R.id.btn_check_leaveword);
        btn_check_stud_list = getActivity().findViewById(R.id.btn_check_studlist);

        btn_check_result.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent((HomepageActivity)getActivity(),ResultActivity.class);
                startActivity(intent);
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                //连接服务器，发送查寝推送
                //Toast.makeText(getActivity(),"已经发送查寝通知",Toast.LENGTH_SHORT).show();
                Connector.getDatabase();

                try
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("request_type","12");
                    m_send = jsonObject.toString();
                    getMainsocket.connMainsocket(TAG,m_send,db_release_handler);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        btn_send_notice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //连接服务器，发送自定义推送
                try
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("request_type","12");
                    m_send = jsonObject.toString();
                    getMainsocket.connMainsocket(TAG,m_send,db_release_notice_handler);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_check_leave_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转查看消息界面
                Intent intent = new Intent((HomepageActivity)getActivity(),TeacherMessageActivity.class);
                startActivity(intent);
            }
        });
        btn_check_stud_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((HomepageActivity)getActivity(),CheckStudentInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    Handler db_release_handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            final int release_port = msg.arg1;                              //端口号
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Socket socket = new Socket();
                        SocketAddress address = new InetSocketAddress(m_ip,release_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        JSONObject jsonObjectsend = new JSONObject();
                        jsonObjectsend.put("type","3");
                        jsonObjectsend.put("tid",gt_loginUser.gettId());
                        m_send = jsonObjectsend.toString();

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);

                        out.close();
                        socket.close();

                        Thread.sleep(1000);

                        socket = new Socket();
                        address = new InetSocketAddress(m_ip,release_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        Log.e(TAG, "dbrun: 握手成功2");

                        DataInputStream in = new DataInputStream(socket.getInputStream());    //创建输入流
                        m_receive = in.readUTF();                     //读取服务器发送的信息

                        Log.e(TAG, "run: "+m_receive);
                        if (m_receive.equals("empty"))
                        {
                            Message message  = new Message();
                            message.what = 0;
                            Toasthandele.sendMessage(message);
                            return;
                        }

                        in.close();
                        socket.close();

                        JSONArray jsonArray = new JSONArray(m_receive);



                        int len = jsonArray.length();

                        LitePal.deleteAll(Collegedb.class);

                        for(int i = 0; i < len; i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Collegedb collegedb = new Collegedb();
                            collegedb.setCGMT_ID(jsonObject.getString("CGMT_ID"));
                            collegedb.setC_ID(jsonObject.getString("C_ID"));
                            collegedb.setC_name(jsonObject.getString("C_name"));
                            collegedb.setG_ID(jsonObject.getString("G_ID"));
                            collegedb.setGrade(jsonObject.getString("Grade"));
                            collegedb.setM_ID(jsonObject.getString("M_ID"));
                            collegedb.setMajor(jsonObject.getString("Major"));
                            collegedb.setCL_ID(jsonObject.getString("CL_ID"));
                            collegedb.setClassname(jsonObject.getString("Class"));
                            collegedb.save();
                        }

                        Intent intent = new Intent((HomepageActivity)getActivity(),ReleaseCheckActivity.class);
                        startActivity(intent);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, "dbrun: 握手成功");
                }
            }).start();
        }
    };

    Handler Toasthandele = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(),"学生为空",Toast.LENGTH_LONG).show();
        }
    };
    Handler db_release_notice_handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            final int release_port = msg.arg1;                              //端口号
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Socket socket = new Socket();
                        SocketAddress address = new InetSocketAddress(m_ip,release_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        JSONObject jsonObjectsend = new JSONObject();
                        jsonObjectsend.put("type","3");
                        jsonObjectsend.put("tid",gt_loginUser.gettId());
                        m_send = jsonObjectsend.toString();

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);

                        out.close();
                        socket.close();

                        Thread.sleep(1000);

                        socket = new Socket();
                        address = new InetSocketAddress(m_ip,release_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        Log.e(TAG, "dbrun: 握手成功2");

                        DataInputStream in = new DataInputStream(socket.getInputStream());    //创建输入流
                        m_receive = in.readUTF();                     //读取服务器发送的信息
                        Log.e(TAG, "run: "+m_receive);
                        if (m_receive .equals("empty"))
                        {
                            Message message = new Message();
                            message.what = 0;
                            Toasthandele.sendMessage(message);
                            return;
                        }

                        in.close();
                        socket.close();

                        JSONArray jsonArray = new JSONArray(m_receive);

                        int len = jsonArray.length();



                        LitePal.deleteAll(Collegedb.class);
                        for(int i = 0; i < len; i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Collegedb collegedb = new Collegedb();
                            collegedb.setCGMT_ID(jsonObject.getString("CGMT_ID"));
                            collegedb.setC_ID(jsonObject.getString("C_ID"));
                            collegedb.setC_name(jsonObject.getString("C_name"));
                            collegedb.setG_ID(jsonObject.getString("G_ID"));
                            collegedb.setGrade(jsonObject.getString("Grade"));
                            collegedb.setM_ID(jsonObject.getString("M_ID"));
                            collegedb.setMajor(jsonObject.getString("Major"));
                            collegedb.setCL_ID(jsonObject.getString("CL_ID"));
                            collegedb.setClassname(jsonObject.getString("Class"));
                            collegedb.save();
                        }

                        Intent intent = new Intent((HomepageActivity)getActivity(),TeacherSendNoticeActivity.class);
                        startActivity(intent);
                        //Intent intent = new Intent(())
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, "dbrun: 握手成功");
                }
            }).start();
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_home_page, container, false);
    }
}
