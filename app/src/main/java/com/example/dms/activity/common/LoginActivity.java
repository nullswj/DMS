package com.example.dms.activity.common;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;


import com.example.dms.activity.homepage.HomepageActivity;
import com.example.dms.activity.student.student_bind.BindActivity;
import com.example.dms.activity.student.student_bind.BindingLocationActivity;
import com.example.dms.activity.student.student_bind.BindingPhotoActivity;
import com.example.dms.activitycontainer.AtyContainer;
import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.R;
import com.example.dms.db.Collegedb;
import com.example.dms.db.Universitydb;
import com.example.dms.entity.Student;
import com.example.dms.entity.Teacher;
import com.example.dms.util.NetWorkUtil;
import com.example.dms.util.getMainsocket;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import static com.example.dms.util.Encryption.md5;
import static com.example.dms.util.StringCheck.checkString;
import static com.example.dms.util.StringCheck.checkTel;

/**
 * 文件名 LoginActivity
 * 描述 登陆页 继承了父类 APPCompatActivity
 * 版本1.0
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener
{

    private String TAG = "LoginActivity";
    /** 注册按钮 */
    private TextView text_login_reg;

    /** 登录按钮 */
    private Button btn_login_login;

    /** 忘记密码 */
    private TextView text_login_forget;

    /** 电话号码输入框 */
    private TextInputLayout edt_login_input;

    /** 密码输入框 */
    private TextInputLayout edt_login_input_pw;

    /** 电话号码串 */
    private String login_input;

    /** 密码串 */
    private String login_input_pw;

    private CheckBox checkBox_remember;

    private CheckBox checkBox_autoLogin;

    private SharedPreferences mpref;
    private SharedPreferences.Editor meditor;


    /** Socket通信变量ip */
    private String m_ip="47.106.151.249";

    /** 发送到服务器的消息 */
    private String m_send="";

    /** 接收服务器发送过来的消息 */
    private String m_receive="";

    private int login_type;

    public ProgressDialog progressDialog = null;

    private String m_secondReceieve = "";

    private String m_thirdReceive = "";

    public static Student gs_loginUser;
    public static Teacher gt_loginUser;

    private boolean isBindPhoto = false;
    private boolean isBindNo = false;
    private boolean isBindLocation = false;
    private boolean isBindSchool = false;

    private volatile boolean isFinish = false;

    private volatile String SCID ;

    private volatile String SID;

    private volatile String SNO;

    private volatile String SCNAME;

    private XGPushClickedResult click;

    //申请两个权限，录音和文件读写
    //1、首先声明一个数组permissions，将需要的权限都放在里面
    private String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA};
    //2、创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
    List<String> mPermissionList = new ArrayList<>();

    private final int mRequestCode = 100;//权限请求码



    //权限判断和申请
    private void initPermission() {

        mPermissionList.clear();//清空没有通过的权限

        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }

        //申请权限
        if (mPermissionList.size() > 0)
        {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (mRequestCode == requestCode)
        {
            for (int i = 0; i < grantResults.length; i++)
            {
                if (grantResults[i] == -1)
                {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss)
            {
                finish();//跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
            }
            else
            {
                //全部权限通过，可以进行下一步操作。。。
            }
        }

    }

    /**
     * 方法名 hanler
     * 功能 Handler异步处理网络操作
     * 参数 无
     * 返回值 无
     */
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            final int log_port = msg.arg1;          //端口号
            Log.e("zqc","handler"+log_port);

            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject jsonObject_send = new JSONObject();                                   //json封装数据
                        jsonObject_send.put("login_type",""+login_type);
                        jsonObject_send.put("user_name",login_input);
                        String md5pass = md5(login_input_pw);
                        jsonObject_send.put("user_pw",md5pass);
                        Log.e("zqc",m_receive);
                        Socket socket = new Socket("47.106.151.249",log_port);      //创建socket连接
                        socket.setSoTimeout(10000);                                          //设置超时

                        m_send  = jsonObject_send.toString();
                        Log.e("zqc",m_send);
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);
                        out.close();
                        socket.close();

                        Thread.sleep(1000);
                        socket = new Socket("47.106.151.249",log_port);      //创建socket连接
                        socket.setSoTimeout(10000);                                          //设置超时
                        DataInputStream in = new DataInputStream(socket.getInputStream());   //创建输入流
                        m_receive= in.readUTF();                                            //读取服务器发送的信息
                        Log.e("zqc",m_receive);
                        JSONObject jsonObject = new JSONObject(m_receive);                  //解析服务器数据

                        int logstate = Integer.parseInt(jsonObject.getString("login_state"));
                        int usertype = Integer.parseInt(jsonObject.getString("user_type"));

                        meditor = mpref.edit();
                        meditor.putInt("usertype",usertype);
                        meditor.apply();

                        if(logstate == 1 && usertype == 0)                                                    //判断账户类型
                        {

                            gt_loginUser.setPhone(jsonObject.getString("Phone"));      //参数设置\
                            //gt_loginUser.settId(jsonObject.getString("T_ID"));
                            gt_loginUser.setUsername(jsonObject.getString("Use_name"));
                            gt_loginUser.setaId(usertype);
                            gt_loginUser.setPassword(jsonObject.getString("Password"));
                            gt_loginUser.setIsvalid(true);

                            gt_loginUser.commit();
                        }
                        else if(logstate == 1 && usertype == 1)
                        {
                            gs_loginUser.setPhone(jsonObject.getString("Phone"));
                            gs_loginUser.setaId(usertype);
                            gs_loginUser.setUsername(jsonObject.getString("Use_name"));
                            gs_loginUser.setPassword(jsonObject.getString("Password"));
                            gs_loginUser.setIsvalid(true);
                            gs_loginUser.commit();
                        }


                        Log.e("zqc","发送message");
                        Message message = new Message();                                    //发送带参数异步消息，更新UI
                        message.what = 0;
                        message.arg1 = usertype;
                        message.arg2 = logstate;
                        mHandler.sendMessage(message);
                        in.close();
                        out.close();//关闭连接
                        socket.close();

                        if (logstate == 0)
                        {
                            return;
                        }

                        Thread.sleep(1000);

                        socket = new Socket("47.106.151.249",log_port);      //创建socket连接
                        socket.setSoTimeout(10000);                                          //设置超时
                        DataInputStream instring = new DataInputStream(socket.getInputStream());   //创建输入流
                        m_secondReceieve= instring.readUTF();
                        Log.e("zqc",m_secondReceieve);

                        instring.close();
                        socket.close();



                        JSONObject jsonObject1 = new JSONObject(m_secondReceieve);
                        String false0 = jsonObject1.getString("false0");
                        Log.e(TAG, "run: false0"+false0 );
                        String false1 = jsonObject1.getString("false1");
                        Log.e(TAG, "run: "+false1 );
                        String false2 = jsonObject1.getString("false2");
                        Log.e(TAG, "run: "+false2 );
                        String false3 = jsonObject1.getString("false3");
                        Log.e(TAG, "run: "+false3 );

                        if (false0.equals("0"))
                        {
                            isBindNo = true;
                            isBindSchool = true;
                            isBindPhoto = true;
                            isBindLocation = true;
                            isFinish = true;
                        }

                        if (false1.equals("0"))
                        {
                            isBindSchool = true;
                        }

                        if (false2.equals("0"))
                        {
                            isBindPhoto = true;
                        }

                        if (false3.equals("0"))
                        {
                            isBindLocation = true;
                        }


                        if (!false0.equals("0") && !(false1.equals("1") && false3.equals("1") && false2.equals("1")))
                        {

                            Thread.sleep(1000);

                            socket = new Socket("47.106.151.249",log_port);      //创建socket连接
                            socket.setSoTimeout(10000);                                          //设置超时
                            instring = new DataInputStream(socket.getInputStream());   //创建输入流
                            m_secondReceieve= instring.readUTF();
                            Log.e("zqc",m_secondReceieve);

                            JSONObject jsonObject2 = new JSONObject(m_secondReceieve);
                            SID = jsonObject2.getString("S_ID");
                            SCID = jsonObject2.getString("SC_ID");
                            SNO = jsonObject2.getString("S_No");
                            SCNAME = jsonObject2.getString("SC_name");

                            instring.close();
                            socket.close();
                        }

                        if (false0.equals("1") && false1.equals("1") && false3.equals("1") && false2.equals("1"))
                        {
                            Thread.sleep(1000);

                            socket = new Socket("47.106.151.249",log_port);      //创建socket连接
                            socket.setSoTimeout(10000);                                          //设置超时
                            in = new DataInputStream(socket.getInputStream());   //创建输入流
                            m_thirdReceive = in.readUTF();
                            Log.e(TAG, "run: third"+m_thirdReceive );

                            if (usertype == 0 )
                                getTeacherInfo(m_thirdReceive);
                            else if (usertype ==1)
                                getStudentInfo(m_thirdReceive);

                            progressDialog.dismiss();
                        }
                        isFinish = true;
                    }
                    catch (IOException e)
                    {
                        Log.e("zqc","IO异常");
                        isFinish = true;
                        progressDialog.dismiss();
                        Message message = new Message();
                        message.what = 0;

                        e.printStackTrace();
                    }
                    catch (JSONException e)
                    {
                        Log.e("zqc","json异常");
                        progressDialog.dismiss();
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    catch (Exception e)
//                    {
//                        isFinish = true;
//                        progressDialog.dismiss();
////                        Message message = new Message();
////                        message.what = 0;
////                        Toasthandler.sendMessage(message);
//                        Log.e("zqc","异常");
//                        e.printStackTrace();
//                    }
                }
            }).start();

        }
    };

    private void getPermission()
    {
        if(ContextCompat.checkSelfPermission(LoginActivity.this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.CAMERA},1);
        }
        if(ContextCompat.checkSelfPermission(LoginActivity.this,Manifest.permission.MODIFY_AUDIO_SETTINGS)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},2);
        }
        if(ContextCompat.checkSelfPermission(LoginActivity.this,Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},3);
        }
    }


    Handler Toasthandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(LoginActivity.this,"网络连接异常，请检查你的网络",Toast.LENGTH_LONG).show();
        }
    };

    /**
     * 方法名 hanler
     * 功能 Handler异步更新UI
     * 参数 无
     * 返回值 无
     */
    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            while (!isFinish)
            {

            }
            Log.e("zqc","m登陆");
            if(msg.arg2 == 1)
            {
                progressDialog.dismiss();
                rememberPassowrd();
                if (msg.arg1 == 0)
                {
                    Log.e("zqc", "1"+isBindNo);
                    if (isBindNo)
                    {
                        Log.e("zqc","未绑定学号");
                        Toast.makeText(LoginActivity.this,"该账号未绑定工号",Toast.LENGTH_LONG).show();
                        try
                        {
                            Connector.getDatabase();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("request_type","12");
                            m_send = jsonObject.toString();
                            getMainsocket.connMainsocket(TAG,m_send,dbhandler3);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    else
                    {
                        Bundle sendBundle = new Bundle();
                        sendBundle.putInt("userType",msg.arg1);
                        sendBundle.putBoolean("isBindNo",isBindNo);
                        sendBundle.putBoolean("isBindPhoto",isBindPhoto);
                        sendBundle.putBoolean("isBindLocation",isBindLocation);
                        sendBundle.putBoolean("isBindSchool",isBindSchool);

                        Intent intent1=new Intent(LoginActivity.this,HomepageActivity.class);
                        intent1.putExtras(sendBundle);

                        String account = gt_loginUser.gettId();
                        StringBuffer sb = null;
                        int len = account.length();
                        while (len < 12)
                        {
                            sb = new StringBuffer();
                            sb.append("0").append(account);
                            account = sb.toString();
                            len = account.length();
                        }

                        account = "t"+ account;
                        XGPushManager.registerPush(getApplicationContext(), account);

                        startActivity(intent1);                                             //跳转至老师端首页
                        finish();
                    }
                }
                else if (msg.arg1 == 1)
                {
                    Log.e("zqc", "1"+isBindNo);
                    if (isBindNo)
                    {
                        Log.e("zqc","未绑定学号");
                        Toast.makeText(LoginActivity.this,"该账号未绑定学号",Toast.LENGTH_LONG).show();
                        try
                        {
                            Connector.getDatabase();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("request_type","12");
                            m_send = jsonObject.toString();
                            getMainsocket.connMainsocket(TAG,m_send,dbhandler);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    //Log.e("zqc", "1"+isBindSchool);
                    else if (isBindSchool && !isBindNo)
                    {
                        Toast.makeText(LoginActivity.this,"该账号未绑定学籍",Toast.LENGTH_LONG).show();
                        Log.e("zqc","未绑定信息");
                        try
                        {
                            Connector.getDatabase();
                            Log.e(TAG, "onClick: 建库成功");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("request_type","12");
                            m_send = jsonObject.toString();
                            Log.e(TAG, "onClick: 打包");
                            getMainsocket.connMainsocket(TAG,m_send,dbhandler2);
                            Log.e(TAG, "onClick: 主端口连接成功");
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    //Log.e("zqc", "1"+isBindPhoto);
                    else if (!isBindNo && !isBindSchool && isBindPhoto)
                    {
                        Toast.makeText(LoginActivity.this,"该账号未绑定照片",Toast.LENGTH_LONG).show();
                        Log.e("zqc","未绑定照片");
                        Intent intent=new Intent(LoginActivity.this,BindingPhotoActivity.class);
                        intent.putExtra("user_name",gs_loginUser.getUsername());
                        intent.putExtra("s_id",SID);
                        intent.putExtra("loc",isBindLocation);
                        startActivity(intent);
                    }
                    else if (!isBindNo && !isBindSchool && !isBindPhoto && isBindLocation)
                    {
                        Toast.makeText(LoginActivity.this,"该账号未绑定位置",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this,BindingLocationActivity.class);
                        intent.putExtra("s_id",SID);
                        intent.putExtra("user_name",gs_loginUser.getUsername());
                        startActivity(intent);
                    }
                    else if (!isBindNo && !isBindPhoto && !isBindSchool && !isBindLocation)
                    {

                        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                        Bundle sendBundle = new Bundle();
                        sendBundle.putInt("userType",msg.arg1);
                        sendBundle.putBoolean("isBindNo",isBindNo);
                        sendBundle.putBoolean("isBindPhoto",isBindPhoto);
                        sendBundle.putBoolean("isBindLocation",isBindLocation);
                        sendBundle.putBoolean("isBindSchool",isBindSchool);

                        String account = gs_loginUser.getsId();
                        StringBuffer sb = null;
                        int len = account.length();
                        while (len < 12)
                        {
                            sb = new StringBuffer();
                            sb.append("0").append(account);
                            account = sb.toString();
                            len = account.length();
                        }

                        account = "s"+ account;
                        XGPushManager.registerPush(getApplicationContext(), account);
                        Intent intent1=new Intent(LoginActivity.this,HomepageActivity.class);
                        intent1.putExtras(sendBundle);

                        startActivity(intent1);                                             //跳转至老师端首页
                        finish();
                    }
                }
            }

            else if (msg.arg2 == 0)                                                     //登陆失败
            {
                progressDialog.dismiss();
                Log.e("zqc","登陆失败");
                Toast.makeText(getApplicationContext(), "登录失败,请检查用户名和密码",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 方法名 initView
     * 功能 初始化控件
     * 参数 无
     * 返回值 无
     */
    private void initView()
    {
        text_login_reg = findViewById(R.id.text_login_reg);                                   //初始化注册按钮
        text_login_forget = findViewById(R.id.text_login_reset);                            //初始化忘记密码按钮
        btn_login_login=findViewById(R.id.btn_login_login);                                 //初始化登录按钮
        edt_login_input = findViewById(R.id.edt_login_input);                               //初始化用户输入框
        edt_login_input_pw = findViewById(R.id.edt_login_input_pw);                           //初始化密码框
        checkBox_remember = findViewById(R.id.checkBox_remember);
        checkBox_autoLogin = findViewById(R.id.checkBox_antoLogin);
    }

    private void setButtonClick()
    {
        btn_login_login.setOnClickListener(this);                                               //登录按钮事件
        text_login_reg.setOnClickListener(this);                                                //注册按钮事件
        text_login_forget.setOnClickListener(this);                                             //忘记密码事件
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initView();
        NetWorkUtil.isOpenNet(LoginActivity.this);
    }

    /**
     * 方法名 onCreate
     * 功能 活动创建与初始化
     * 参数 savedInstanceState
     * 返回值 无
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);                                                     //调用父类方法创建活动
        setContentView(R.layout.activity_login);                                                  //设置活动界面
        NetWorkUtil.isOpenNet(LoginActivity.this);
        mpref = PreferenceManager.getDefaultSharedPreferences(this);
        initView();
        gt_loginUser = new Teacher(LoginActivity.this);
        gs_loginUser = new Student(LoginActivity.this);
        Intent getIntent = this.getIntent();
        Bundle bundle = getIntent.getExtras();
        boolean iscancelauto= true;
        if(bundle!=null)
        {
            iscancelauto = bundle.getBoolean("isauto",true);
        }

        if(!iscancelauto)
            checkBox_autoLogin.setChecked(false);

        loginRemember(iscancelauto);

        setButtonClick();
        click = XGPushManager.onActivityStarted(this);
        if (click != null)
        {
            finish();
            return;
        }

        initPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy-2: "+AtyContainer.getInstance().getactivityStackSize());
        if (click != null)
        {
            Log.e(TAG, "onDestroy-1: "+AtyContainer.getInstance().getactivityStackSize());
            Log.e(TAG, "onDestroy0: "+AtyContainer.getInstance().getActivityCompat());
            if(AtyContainer.getInstance().getactivityStackSize() < 1)
            {
                Log.e(TAG, "onDestroy1: "+AtyContainer.getInstance().getActivityCompat());
                Intent intent = new Intent(LoginActivity.this,LoginActivity.class);
                Log.e(TAG, "onDestroy2: "+AtyContainer.getInstance().getActivityCompat());
                startActivity(intent);
                Log.e(TAG, "onDestroy3: "+AtyContainer.getInstance().getActivityCompat());
            }
            return;
        }
    }

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
                case R.id.btn_login_login:                                                      //登录按钮事件
                    boolean flag = true;
                    login_input = edt_login_input.getEditText().getText().toString().trim();          //获取电话
                    login_input_pw=edt_login_input_pw.getEditText().getText().toString();                     //获取密码

                    if(!checkString(login_input))
                    {
                        Toast.makeText(LoginActivity.this,"输入非法，用户名只能包含数字、大小写字母、下划线",Toast.LENGTH_SHORT).show();
                        flag = false;
                    }
                    if(flag == true)
                    {
                        if (checkTel(login_input))  login_type = 1;
                        else    login_type = 0;
                        try
                        {
                            isFinish = false;
                            JSONObject jsonObject = new JSONObject();                                   //json封装数据
                            jsonObject.put("request_type","1");

                            m_send  = jsonObject.toString();                                            //封装成服务器字符串
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        if(!login_input.equals(""))                                                 //输入合法性检查
                        {
                            if(!login_input_pw.equals(""))
                            {
                                progressDialog = new ProgressDialog(LoginActivity.this);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("正在登录。。。");
                                progressDialog.show();
                                progressDialog.setCanceledOnTouchOutside(false);
                                getMainsocket.connMainsocket(TAG,m_send,handler);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "请输入密码!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "请输入用户名或手机号!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
                case R.id.text_login_reg:
                    Intent intent0=new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(intent0);
                    break;
                case R.id.text_login_reset:
                    Intent intent1 = new Intent(LoginActivity.this,Forgetpw_verify_Activity.class);
                    startActivity(intent1);
                    break;
            }
        }

    /**
     * 程武正编写
     */
    private  void rememberPassowrd()
        {
            meditor = mpref.edit();
            if(checkBox_remember.isChecked())
            {
                meditor.putBoolean("remember_password",true);
                meditor.putString("account",login_input);
                meditor.putString("password",login_input_pw);
                if(checkBox_autoLogin.isChecked())
                {
                    meditor.putBoolean("auto_login",true);
                }
                else
                {
                    meditor.putBoolean("auto_login",false);
                }
            }
            else
            {
                meditor.clear();
            }
            meditor.apply();
        }

    /**
     * 程武正编写
     * @param isAutoCancel
     */
    private void loginRemember(boolean isAutoCancel)
        {
            boolean isRemember = mpref.getBoolean("remember_password",false);
            if(!isAutoCancel)
            {
                meditor = mpref.edit();
                meditor.putBoolean("auto_login",false);
                meditor.apply();
            }
            boolean isAuto = mpref.getBoolean("auto_login",false);
            if(isRemember)
            {
                checkBox_remember.setChecked(true);
                String account = mpref.getString("account","");
                String password = mpref.getString("password","");
                edt_login_input.getEditText().setText(account);
                edt_login_input_pw.getEditText().setText(password);
                if(isAuto)
                {
                    checkBox_autoLogin.setChecked(true);
                    Intent intent = new Intent(LoginActivity.this,HomepageActivity.class);
                    Log.e("zqc",""+mpref.getInt("usertype",-1));
                    if(mpref.getInt("usertype",-1)==0)
                        gt_loginUser.applyUser();
                    else if(mpref.getInt("usertype",-1)==1)
                        gs_loginUser.applyUser();

                    startActivity(intent);
                    Toast.makeText(LoginActivity.this,"自动登陆成功",Toast.LENGTH_SHORT).show();
                    finish();
                }


            }
        }

        public static int wahtType()
        {
            if (gt_loginUser.isIsvalid())
                return 0;
            else if (gs_loginUser.isIsvalid())
                return 1;
            else
                return -1;
        }
        private void getStudentInfo(String info)
        {
            try
            {


                JSONObject jsonObject= new JSONObject(info);

                gs_loginUser.setSC_name(jsonObject.getString("SC_name"));

                gs_loginUser.setS_name(jsonObject.getString("S_name"));

                gs_loginUser.setScId(jsonObject.getString("SC_ID"));

                gs_loginUser.setCgmtId(jsonObject.getString("CGMT_ID"));

                gs_loginUser.setC_ID(jsonObject.getString("C_ID"));

                gs_loginUser.setC_name(jsonObject.getString("C_name"));

                gs_loginUser.setGID(jsonObject.getString("G_ID"));

                gs_loginUser.setGrade(jsonObject.getString("Grade"));

                gs_loginUser.setClass(jsonObject.getString("Class"));

                gs_loginUser.setCl_no(jsonObject.getString("CL_ID"));

                gs_loginUser.setsId(jsonObject.getString("S_ID"));

                gs_loginUser.setS_No(jsonObject.getString("S_No"));

                gs_loginUser.setMID(jsonObject.getString("M_ID"));

                gs_loginUser.setMajor(jsonObject.getString("Major"));

                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getSC_name());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getS_name());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getScId());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getCgmtId());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getC_ID());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getC_name());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getGID());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getGrade());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getCl_no());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getClassName());


                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getMajor());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getMID());

                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getsId());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getS_No());

                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getPassword());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getPhone());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getUsername());
                Log.e(TAG, "getStudentInfo: "+gs_loginUser.getaId());

                gs_loginUser.commit();


            } catch (JSONException e) {
                Log.e("zqc","json设置异常");
                e.printStackTrace();
            }
        }
        private void getTeacherInfo(String info)
        {
            try {

                JSONObject jsonObject= new JSONObject(info);
                gt_loginUser.setT_name(jsonObject.getString("T_name"));
                gt_loginUser.setSC_name(jsonObject.getString("SC_name"));
                gt_loginUser.settId(jsonObject.getString("T_ID"));
                gt_loginUser.setT_No(jsonObject.getString("T_No"));
                Log.e(TAG, "getTeacherInfo: "+gt_loginUser.getT_name());
                Log.e(TAG, "getTeacherInfo: "+gt_loginUser.getT_No() );
                Log.e(TAG, "getTeacherInfo: "+gt_loginUser.gettId() );
                gt_loginUser.commit();

            } catch (JSONException e) {
                Log.e("zqc","json设置异常");
                e.printStackTrace();
            }
        }

    Handler dbhandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            final int reg_port = msg.arg1;                              //端口号

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        Socket socket = new Socket();
                        SocketAddress address = new InetSocketAddress(m_ip,reg_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);
                        Log.e(TAG, "dbrun: 握手成功");

                        JSONObject jsonObjectsend = new JSONObject();
                        jsonObjectsend.put("type","0");
                        jsonObjectsend.put("scid",SCID);
                        m_send = jsonObjectsend.toString();

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);

                        out.close();
                        socket.close();

                        Thread.sleep(1000);

                        socket = new Socket();
                        address = new InetSocketAddress(m_ip,reg_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        Log.e(TAG, "dbrun: 握手成功2");

                        DataInputStream in = new DataInputStream(socket.getInputStream());    //创建输入流
                        m_receive = in.readUTF();                     //读取服务器发送的信息

                        in.close();
                        socket.close();

                        LitePal.deleteAll(Universitydb.class);
                        JSONArray jsonArray = new JSONArray(m_receive);
                        Log.e(TAG, "run: "+m_receive);
                        int len = jsonArray.length();
                        for(int i = 0; i < len; i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Universitydb universitydb = new Universitydb();
                            universitydb.setUniversityID(jsonObject.getString("SC_ID"));
                            universitydb.setUniversityName(jsonObject.getString("SC_name"));
                            universitydb.save();
                        }

                        Intent intent = new Intent(LoginActivity.this,InforVerifyActivity.class);
                        intent.putExtra("type",gs_loginUser.getaId());
                        intent.putExtra("user_name",gs_loginUser.getUsername());
                        intent.putExtra("password",gs_loginUser.getPassword());
                        intent.putExtra("cgmt",isBindSchool);
                        intent.putExtra("photo",isBindPhoto);
                        intent.putExtra("loc",isBindLocation);
                        startActivity(intent);

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

    Handler dbhandler2 = new Handler()
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
                        jsonObjectsend.put("scid",SCID);
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

                        Intent intent = new Intent(LoginActivity.this,BindActivity.class);//显式,第二次迭代跳转到InforVerifyActivity
                        intent.putExtra("user_name",gs_loginUser.getUsername());
                        intent.putExtra("s_id",SID);
                        intent.putExtra("s_no",SNO);
                        intent.putExtra("sc_name",SCNAME);
                        intent.putExtra("photo",isBindPhoto);
                        intent.putExtra("loc",isBindLocation);
                        startActivity(intent);

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

    Handler dbhandler3 = new Handler()
    {
        public void handleMessage(Message msg)
        {
            final int reg_port = msg.arg1;                              //端口号

            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        Socket socket = new Socket();
                        SocketAddress address = new InetSocketAddress(m_ip,reg_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);
                        Log.e(TAG, "dbrun: 握手成功");

                        JSONObject jsonObjectsend = new JSONObject();
                        jsonObjectsend.put("type","0");
                        jsonObjectsend.put("scid",SCID);
                        m_send = jsonObjectsend.toString();

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);

                        out.close();
                        socket.close();

                        Thread.sleep(1000);
                        socket = new Socket();
                        address = new InetSocketAddress(m_ip,reg_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        Log.e(TAG, "dbrun: 握手成功2");

                        DataInputStream in = new DataInputStream(socket.getInputStream());    //创建输入流
                        m_receive = in.readUTF();                     //读取服务器发送的信息

                        in.close();
                        socket.close();

                        LitePal.deleteAll(Universitydb.class);
                        JSONArray jsonArray = new JSONArray(m_receive);
                        Log.e(TAG, "run: "+m_receive);
                        int len = jsonArray.length();
                        for(int i = 0; i < len; i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Universitydb universitydb = new Universitydb();
                            universitydb.setUniversityID(jsonObject.getString("SC_ID"));
                            universitydb.setUniversityName(jsonObject.getString("SC_name"));
                            universitydb.save();
                        }

                        Intent intent = new Intent(LoginActivity.this,InforVerifyActivity.class);
                        intent.putExtra("type",gt_loginUser.getaId());
                        intent.putExtra("user_name",gt_loginUser.getUsername());
                        intent.putExtra("password",gt_loginUser.getPassword());
                        intent.putExtra("cgmt",isBindSchool);
                        intent.putExtra("photo",isBindPhoto);
                        intent.putExtra("loc",isBindLocation);
                        startActivity(intent);

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
}
