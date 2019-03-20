package com.example.dms.activity.common;

import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.content.Intent;
import android.widget.Spinner;
import android.widget.Toast;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;


//import com.example.dms.activity.student.student_bind.BindActivity;
import com.example.dms.activity.student.student_bind.BindActivity;
import com.example.dms.activity.student.student_bind.BindingLocationActivity;
import com.example.dms.activity.student.student_bind.BindingPhotoActivity;
import com.example.dms.activity.teacher.SpinnerAdapter;
import com.example.dms.activitycontainer.AtyContainer;
import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.R;
import com.example.dms.db.Collegedb;
import com.example.dms.db.Universitydb;
import com.example.dms.util.getMainsocket;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.tencent.android.tpush.XGPushManager;
import com.vmloft.develop.library.tools.utils.VMLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import static com.example.dms.activity.common.LoginActivity.gs_loginUser;
import static com.example.dms.activity.common.LoginActivity.gt_loginUser;
import static com.example.dms.util.Encryption.md5;
import static com.example.dms.util.StringCheck.checkString;

/**
 * 文件名 InforVerifyActivity
 * 描述 信息验证界面 继承了父类 AppCompatActivity 和接口 View.OnClickListener
 * 版本 1.0
 */
public class InforVerifyActivity extends BaseActivity implements View.OnClickListener
{
    private String TAG = "InforVerifyActivity";
    private Toolbar infov_tlb;
    /**  信息验证按钮 */
    private Button btn_infor_verify_verify;

    private Spinner spi_info_verify_input_school;

    /** 学/工号输入框 */
    private TextInputLayout edt_infor_verify_input_no;

    /** 姓名输入按钮 */
    private TextInputLayout edt_infor_verify_input_name;

    /** 学/工号 */
    private String m_no;

    /** 姓名 */
    private  String m_name;

    private String m_id;

    private String user_name;

    /** 账户类型 */
    private  int m_type;

    /** Socket通信变量ip */
    private String m_ip="47.106.151.249";

    /** 发送到服务器的消息 */
    private String m_send="";

    /** 接收服务器发送过来的消息 */
    private String m_receive="";

    private String password;

    private Boolean isBindSchool;

    private Boolean isBindPhoto;

    private Boolean isBindLocation;


    private String university_ID;
    private String university_Name;

    private List<String> listUniversityId = new ArrayList<>();
    private List<String> listUniversityName = new ArrayList<>();


    private void initSpinner()
    {
        spi_info_verify_input_school = findViewById(R.id.spi_infor_verify_school);
        List<Universitydb> universities= LitePal.findAll(Universitydb.class);

        listUniversityId.add("-1");
        listUniversityName.add("学校");
        for(Universitydb universitydb : universities)
        {
            String university_name;
            String university_id;
            university_name = universitydb.getUniversityName();
            university_id = universitydb.getUniversityID();
            listUniversityName.add(university_name);
            listUniversityId.add(university_id);
        }
        SpinnerAdapter adapter_university = new SpinnerAdapter(this,listUniversityName);
        adapter_university.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_info_verify_input_school.setAdapter(adapter_university);


        LitePal.deleteAll(Universitydb.class);
    }

    private void initView()
    {
        btn_infor_verify_verify=findViewById(R.id.btn_infor_verify_verify);         //初始化信息验证按钮
        edt_infor_verify_input_no=findViewById(R.id.edt_infor_verify_input_no);     //初始化姓名输入框
        edt_infor_verify_input_name=findViewById(R.id.edt_infor_verify_input_name); //初始化学/工号输入框
    }

    private void setButtonClick()
    {
        btn_infor_verify_verify.setOnClickListener(this);                           //验证按钮事件
        spi_info_verify_input_school.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String item = (String)spi_info_verify_input_school.getItemAtPosition(position);
                        int len = listUniversityName.size();
                        for(int i = 0; i < len; i++)
                        {
                            if (item.equals(listUniversityName.get(i)))
                            {
                                university_ID = listUniversityId.get(i);
                                university_Name = listUniversityName.get(i);
                            }

                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        university_ID = "-1";
                    }
                });
    }

    private void showSubmitStudentSchoolDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(InforVerifyActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("信息完善");
        bindDialog.setMessage("身份验证成功!\n是否立即绑定班级信息?");

        bindDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    Connector.getDatabase();
                    Log.e(TAG, "onClick: 建库成功");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("request_type","12");
                    m_send = jsonObject.toString();
                    Log.e(TAG, "onClick: 打包");
                    getMainsocket.connMainsocket(TAG,m_send,dbhandler);
                    Log.e(TAG, "onClick: 主端口连接成功");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        bindDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        bindDialog.setCancelable(false);
        AlertDialog dialog = bindDialog.create();
        dialog.show();
    }






    private void showBcakDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(InforVerifyActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("信息验证");
        bindDialog.setMessage("放弃未保存的输入，确认返回？");

        bindDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void setToolBar()
    {
        infov_tlb = (Toolbar)findViewById(R.id.infov_tlb);
        setSupportActionBar(infov_tlb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            showBcakDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            showBcakDialog();
        }
        return true;
    }
    /**
     * 方法名 onCreate
     * 功能 创建界面并初始化
     * 参数 savedInstanceState
     * 返回值 无
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);                         //调用父类方法创建活动
        setContentView(R.layout.activity_infor_verify);               //设置活动布局

        initView();
        setToolBar();
        initSpinner();
        setButtonClick();

        Intent intent=getIntent();
        m_type=intent.getIntExtra("type",-1);
        user_name = intent.getStringExtra("user_name");
        password = intent.getStringExtra("password");
        isBindSchool = intent.getBooleanExtra("cgmt",false);
        isBindPhoto = intent.getBooleanExtra("photo",false);
        isBindLocation = intent.getBooleanExtra("loc",false);
    }

    /**
     * 方法名 handler
     * 功能 异步处理网络操作
     * 参数 无
     * 返回值 无
     */
    private Handler handler = new Handler()
    {
        public void handleMessage(final Message msg)
        {
            final int vet_port = msg.arg1;        //端口号
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {


                        JSONObject jsonObjectsend = new JSONObject();
                        jsonObjectsend.put("user_name",user_name);
                        jsonObjectsend.put("name",m_name);
                        jsonObjectsend.put("type",""+m_type);
                        jsonObjectsend.put("no",m_no);
                        jsonObjectsend.put("scid",university_ID);
                        m_send  = jsonObjectsend.toString();

                        Log.d(TAG,m_receive);

                        Socket socket = new Socket();                 //创建socket连接
                        SocketAddress address = new InetSocketAddress(m_ip,vet_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);                   //设置超时

                        Log.d(TAG,"握手成功");

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);

                        Log.d(TAG,"握手成功");
                        out.close();
                        socket.close();

                        Thread.sleep(1000);
                        socket = new Socket("47.106.151.249",vet_port);      //创建socket连接
                        socket.setSoTimeout(10000);                                          //设置超时

                        Log.d(TAG,"握手成功");

                        DataInputStream in = new DataInputStream(socket.getInputStream());    //创建输入流
                        m_receive = in.readUTF();                     //读取服务器发送的信息

                        Log.d(TAG,"读取成功2");
                        in.close();                                   //关闭流
                        socket.close();

                        Log.d("zqc",m_receive);

                        JSONObject jsonObject = new JSONObject(m_receive);        //解析服务器数据
                        int vet_result = Integer.parseInt(jsonObject.getString("checkresult"));
                        Log.e(TAG,""+vet_result);
                        if(vet_result == 1 && m_type == 0)
                        {
                            m_id = jsonObject.getString("T_ID");
                        }
                        else if(vet_result == 1 && m_type == 1)
                        {
                            m_id = jsonObject.getString("S_ID");
                        }
                        Message message = new Message();                  //发送异步消息
                        message.what = 0;
                        message.arg1 = vet_result;
                        mHandler.sendMessage(message);
                    }
                    catch (Exception e)
                    {
                        Log.d("zqc","异常");
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };

    private void registSDK()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    String account = m_id;
                    StringBuffer sb = null;
                    int len = m_id.length();
                    while (len < 12)
                    {
                        sb = new StringBuffer();
                        sb.append("0").append(account);
                        account = sb.toString();
                        len = account.length();
                    }
                    if (m_type == 1)
                    {
                        account = "s"+ account;
                    }
                    else if(m_type == 0)
                    {
                        account = "t"+account;
                    }
                    String pass = md5("shabi_wangluyao");
                    EMClient.getInstance().createAccount(account, pass);
                    //XGPushManager.registerPush(getApplicationContext(), account);
                }
                catch (HyphenateException e)
                {
                    String errorMsg = "sign up error " + e.getErrorCode() + "; " + e.getMessage();
                    VMLog.d(errorMsg);
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 方法名 handler
     * 功能 异步更新UI
     * 参数 无
     * 返回值 无
     */
    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(msg.arg1 == 1)                               //服务器返回1
            {
                if (m_type == 1)                            //学生账户验证成功
                {

                    registSDK();
                    showSubmitStudentSchoolDialog();
                }
                if (m_type == 0)                            //老师账户验证成功
                {
                    Toast.makeText(getApplicationContext(), "信息验证成功", Toast.LENGTH_LONG).show();
                    registSDK();
                    AtyContainer.finishAllActivity();
                    Intent intent = new Intent(InforVerifyActivity.this,LoginActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isauto",false);
                    intent.putExtras(bundle);
                    int type = LoginActivity.wahtType();
                    if (type == 0)
                        gt_loginUser.clear();
                    else if (type == 1)
                        gs_loginUser.clear();
                    startActivity(intent);
                }
            }
            else if (msg.arg1 == 2)
            {
                Log.d("zqc","");
                Toast.makeText(getApplicationContext(), "信息验证失败,该账号已被占用",
                        Toast.LENGTH_LONG).show();
            }
            else                                            //服务器返回0，验证失败
            {
                Log.d("zqc","验证失败");
                Toast.makeText(getApplicationContext(), "信息验证失败,该账号不存在",
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    Handler dbhandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            final int ver_port = msg.arg1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        Socket socket = new Socket();
                        SocketAddress address = new InetSocketAddress(m_ip,ver_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);
                        Log.e(TAG, "dbrun: 握手成功");

                        JSONObject jsonObjectsend = new JSONObject();
                        jsonObjectsend.put("type","1");
                        jsonObjectsend.put("scid",university_ID);
                        m_send = jsonObjectsend.toString();


                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);

                        out.close();
                        socket.close();

                        Thread.sleep(1000);

                        socket = new Socket();
                        address = new InetSocketAddress(m_ip,ver_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        Log.e(TAG, "dbrun: 握手成功2");

                        DataInputStream in = new DataInputStream(socket.getInputStream());    //创建输入流
                        m_receive = in.readUTF();                     //读取服务器发送的信息

                        in.close();
                        socket.close();

                        JSONArray jsonArray = new JSONArray(m_receive);
                        Log.e(TAG, "run: "+m_receive);
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

                        Intent intent = new Intent(InforVerifyActivity.this,BindActivity.class);//显式,第二次迭代跳转到InforVerifyActivity
                        intent.putExtra("user_name",user_name);
                        intent.putExtra("s_id",m_id);
                        intent.putExtra("s_no",m_no);
                        intent.putExtra("sc_name",university_Name);
                        intent.putExtra("photo",isBindPhoto);
                        intent.putExtra("loc",isBindLocation);
                        startActivity(intent);
                        finish();

                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };
    /**
     * 方法名 onClick
     * 功能 设置按钮事件
     * 参数 view
     * 返回值 无
     */
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_infor_verify_verify:              //验证按钮事件

                boolean flag = true;
                m_no=edt_infor_verify_input_no.getEditText().getText().toString().trim();         //获取学号
                m_name=edt_infor_verify_input_name.getEditText().getText().toString().trim();         //获取姓名
                if (m_no.equals("") || m_name.equals("") || !checkString(m_no) )                 //合法性检查
                {
                    Toast.makeText(InforVerifyActivity.this,"输入非法，只能包含数字和大小写字母",Toast.LENGTH_SHORT).show();
                    flag = false;
                }
                else if (university_ID.equals("-1"))
                {
                    Toast.makeText(InforVerifyActivity.this,"请选择学校",Toast.LENGTH_SHORT).show();
                    flag = false;
                }
                if (flag == true)                       //输入合法
                {
                    JSONObject jsonObject = new JSONObject();
                    try
                    {
                        jsonObject.put("request_type","6");
                        m_send=jsonObject.toString();
                        getMainsocket.connMainsocket(TAG,m_send,handler);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
