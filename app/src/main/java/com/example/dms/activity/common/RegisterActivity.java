package com.example.dms.activity.common;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.R;
import com.example.dms.db.Universitydb;
import com.example.dms.util.getMainsocket;
import com.mob.MobSDK;

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

import android.support.v7.widget.Toolbar;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.example.dms.util.Encryption.md5;
import static com.example.dms.util.StringCheck.checkPasswordStrength;
import static com.example.dms.util.StringCheck.checkString;
import static com.example.dms.util.StringCheck.checkTel;


/**
 * 文件名 RegisterActivity
 * 描述 注册界面 继承了父类 AppCompatActivity 和接口 View.OnClickListener
 * 版本 1.0
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener
{
    private String TAG = "RegisterActivity";
    /**  教师注册复选框 */
    private RadioButton rbtn_reg_teacher;

    /**  学生注册复选框 */
    private  RadioButton rbtn_reg_student;

    /**  用户名输入框 */
    private TextInputLayout edt_reg_input_user_name;

    /**  密码输入框 */
    private  TextInputLayout edt_reg_input_pw;

    /**  密码验证输入框 */
    private  TextInputLayout edt_reg_input_sure_pw;

    /**  电话号码输入框 */
    private  TextInputLayout edt_reg_input_tel;

    /**  验证码输入框 */
    private  TextInputLayout edt_reg_input_identify_code;

    /**  验证码获取按钮 */
    private  Button  btn_reg_get_identify_code;

    /**  注册按钮 */
    private  Button btn_reg_reg;

    private Toolbar tlb_reg;

    private TextView text_reg_service_agreement;

    private TextView text_reg_privacy_policy;

    /**  用户名 */
    private String m_name;

    /**  用户密码 */
    private String m_passWord;

    /**  重复密码 */
    private String m_repassWord;

    /**  电话 */
    private String m_Tel;

    /**  注册类型 */
    private int m_type;

    /**  客户端输入的验证码 */
    private String m_inputCode;

    private String m_ip = "47.106.151.249";

    /**  后台传来的手机号是否注册消息 */
    String m_receive="";

    /**  发送到后台的消息 */
    String m_send="";

    /**  倒计时 */
    private  TimeCount m_TimeCount;

    /** 注册时所带的参数:电话、密码、类型 */
    /** 验证码是否一致是否成功 */
    private String m_sameCode="";

    /**  事件接收器 */
    public EventHandler eh;

    /**  短信验证API */
    private final String appKey="28b903adc5e76";
    private final String appSecret="c3ca823e3e86f059fd8d96a22a04ce6a";

    private void showBackDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(RegisterActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("注册");
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

    private void showSubmitDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(RegisterActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("注册");
        bindDialog.setMessage("注册成功\n是否立即进行身份验证？");

        bindDialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
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
        });

        bindDialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });

        bindDialog.setCancelable(false);
        AlertDialog dialog = bindDialog.create();
        dialog.show();
    }

    private void setToolBar()
    {
        tlb_reg = findViewById(R.id.tlb_reg);
        setSupportActionBar(tlb_reg);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            showBackDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            showBackDialog();
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
        super.onCreate(savedInstanceState);                 //调用父类方法创建活动
        setContentView(R.layout.activity_register);           //设置活动布局

        initView();                                            //初始化控件
        setToolBar();
        setButtonClick();
    }

    private void setButtonClick()
    {
        btn_reg_get_identify_code.setOnClickListener(this);                         //设置获取验证码按钮事件
        btn_reg_reg.setOnClickListener(this);                                         //注册按钮事件
        text_reg_privacy_policy.setOnClickListener(this);
        text_reg_service_agreement.setOnClickListener(this);
    }

    /**
     * 方法名 initView
     * 功能 初始化控件
     * 参数 无
     * 返回值 无
     */
    public void initView()
    {
        rbtn_reg_teacher=findViewById(R.id.rbtn_reg_teacher);                               //初始化教师注册复选框
        rbtn_reg_student=findViewById(R.id.rbtn_reg_student);                               //初始化学生注册复选框
        edt_reg_input_user_name=findViewById(R.id.edt_reg_input_user_name);               //初始化用户名输入框
        edt_reg_input_pw=findViewById(R.id. edt_reg_input_pw);                              //初始化密码输入框
        edt_reg_input_sure_pw=findViewById(R.id.edt_reg_input_sure_pw);                    //初始化密码验证输入框
        edt_reg_input_tel=findViewById(R.id.edt_reg_input_tel);                             //初始化电话号码输入框
        edt_reg_input_identify_code=findViewById(R.id.edt_reg_input_identify_code);     //初始化验证码输入框
        btn_reg_get_identify_code=findViewById(R.id.btn_reg_get_identify_code);          //初始化验证码获取按钮
        btn_reg_reg=findViewById(R.id.btn_reg_reg);                                         //初始化注册按钮
        text_reg_service_agreement = findViewById(R.id.txt_reg_item);
        text_reg_privacy_policy = findViewById(R.id.txt_reg_secret);

        MobSDK.init(this, appKey, appSecret);//=SMSSDK.initSDK(this, "你的appkey", "你的appsecret");    //短信验证初始化
        m_TimeCount = new TimeCount(60000, 1000);
    }

    /**
     * 方法名 initEvent
     * 功能 初始化事件接收器
     * 参数 无
     * 返回值 无
     */
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

    /**
     * 方法名 rhandler
     * 功能 异步处理网络操作
     * 参数 无
     * 返回值 无
     */
    private Handler rhandler = new Handler()
    {
        @Override
        public void handleMessage(final Message msg)
        {

            final int reg_port = msg.arg1;                              //端口号
            Log.e("zqc",""+reg_port);
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject jsonObjectsend = new JSONObject();           //封装发送给服务器的json数据
                        jsonObjectsend.put("user_type" ,""+m_type);
                        Log.e("swj",""+m_type);
                        jsonObjectsend.put("user_name",m_name);
                        String md5pass = md5(m_passWord);
                        jsonObjectsend.put("user_pw",md5pass);
                        jsonObjectsend.put("user_tel",m_Tel);
                        m_send=jsonObjectsend.toString();                       //将数据转为字符串

                        Socket socket = new Socket();                   //创建socket连接
                        SocketAddress address = new InetSocketAddress("47.106.151.249",reg_port);
                        socket.connect(address,10000);          //设置超时
                        socket.setSoTimeout(30000);
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);

                        out.close();
                        socket.close();

                        Thread.sleep(1000);

                        socket = new Socket("47.106.151.249",reg_port);      //创建socket连接
                        socket.setSoTimeout(10000);                                          //设置超时
                        DataInputStream in = new DataInputStream(socket.getInputStream());      //创建输入流
                        m_receive= in.readUTF();                            //读取服务器发送的信息

                        Log.e("zqc",m_receive);
                        JSONObject jsonObject = new JSONObject(m_receive);      //解析服务器数据
                        String result = jsonObject.getString("reg_result");
                        Log.e("zqc",result);

                        Message message = new Message();                    //发送异步消息
                        message.what = 0;
                        message.obj = result;
                        mHandler.sendMessage(message);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };

    /**
     * 方法名 rhandler
     * 功能 异步更新UI
     * 参数 无
     * 返回值 无
     */
    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            String massege=msg.obj.toString();
            if(massege.equals("true"))                      //服务器返回参数为true,注册成功
            {
                showSubmitDialog();
            }
            else                                            //服务器返回参数为false,注册失败
            {
                Toast.makeText(getApplicationContext(), "注册失败,该用户名或电话号码已注册",
                        Toast.LENGTH_SHORT).show();
                m_TimeCount.cancel();
            }
        }
    };
    /**
     * 方法名 handler
     * 功能 异步处理短信验证码
     * 参数 无
     * 返回值 无
     */
    Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("zqc", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE)
                {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE)     // 提交验证码成功
                    {
                        Toast.makeText(RegisterActivity.this, "验证码提交成功",
                                Toast.LENGTH_SHORT).show();
                        if (rbtn_reg_teacher.isChecked())                   //账户类型为老师
                        {
                            m_type=0;
                        }
                        else if (rbtn_reg_student.isChecked())              //账户类型为学生
                        {
                            m_type=1;
                        }

                        try
                        {
                            JSONObject jsonObject = new JSONObject();           //封装发送给服务器的json数据
                            jsonObject.put("request_type","2");
                            m_send=jsonObject.toString();                       //将数据转为字符串
                            getMainsocket.connMainsocket(TAG,m_send,rhandler);                                //连接服务器
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
                    else                                                        //验证码提交失败
                    {
                        Toast.makeText(RegisterActivity.this, "验证码提交失败", Toast.LENGTH_SHORT).show();
                       ((Throwable) data).printStackTrace();
                    }
                }
            }
        };

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

                        Intent intent=new Intent(RegisterActivity.this,InforVerifyActivity.class);
                        intent.putExtra("type",m_type);
                        intent.putExtra("phone",m_Tel);
                        intent.putExtra("user_name",m_name);
                        String md5pass = md5(m_passWord);
                        intent.putExtra("password",md5pass);
                        intent.putExtra("cgmt",true);
                        intent.putExtra("photo",true);
                        intent.putExtra("loc",true);
                        startActivity(intent);                      //跳转至信息验证界面
                        m_TimeCount.cancel();

                        finish();

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
        switch(view.getId())
        {
            case R.id.btn_reg_get_identify_code:                            //获取验证码按钮事件
                initEvent();//执行handle
                m_Tel=edt_reg_input_tel.getEditText().getText().toString().trim();
                if(!m_Tel.equals(""))                                       //有手机号
                {
                    if(checkTel(m_Tel))                                      //手机号合法
                    {
                        SMSSDK.getVerificationCode("+86",m_Tel);            //获取验证码
                        Log.v("mjk1","getcode");
                        m_TimeCount.start();
                        Log.v("mjk1","timestart");
                    }
                    else
                    {
                        Toast.makeText(this, "手机号有误", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_reg_reg:                                  //注册按钮事件
                m_name = edt_reg_input_user_name.getEditText().getText().toString().trim();   //获取用户名
                m_passWord = edt_reg_input_pw.getEditText().getText().toString().trim();       //获取密码
                m_repassWord=edt_reg_input_sure_pw.getEditText().getText().toString().trim(); //获取重复密码
                m_Tel=edt_reg_input_tel.getEditText().getText().toString().trim();                //获取输入框密码
                m_inputCode=edt_reg_input_identify_code.getEditText().getText().toString().trim();    //获取验证码
                if(rbtn_reg_teacher.isChecked()||rbtn_reg_student.isChecked())      //有用户类型
                {
                   if(!m_name.equals("") && checkString(m_name) && !checkTel(m_name))                    //用户名合法
                   {
                       if(!m_passWord.equals("") && checkString(m_passWord) && checkPasswordStrength(m_passWord))//密码合法
                       {
                               if(!m_repassWord.equals(("")) && m_passWord.equals(m_repassWord))//有确认密码
                               {
                                       if(!m_Tel.equals("") || checkTel(m_Tel))                //有手机号
                                       {
                                               if(!m_inputCode.equals(""))          //有验证码
                                               {
                                                   m_sameCode="false";
                                                   SMSSDK.submitVerificationCode("+86", m_Tel,m_inputCode); //提交验证
//                                                   if (rbtn_reg_teacher.isChecked())                   //账户类型为老师
//                                                   {
//                                                       m_type=0;
//                                                   }
//                                                   else if (rbtn_reg_student.isChecked())              //账户类型为学生
//                                                   {
//                                                       m_type=1;
//                                                   }
//                                                   Log.v("mjk1", "code");
//
//                                                   try
//                                                   {
//                                                       JSONObject jsonObject = new JSONObject();           //封装发送给服务器的json数据
//                                                       jsonObject.put("request_type","2");
//                                                       m_send=jsonObject.toString();                       //将数据转为字符串
//                                                       getMainsocket.connMainsocket(TAG,m_send,rhandler);                              //连接服务器
//                                                   }
//                                                   catch (JSONException e)
//                                                   {
//                                                       e.printStackTrace();
//                                                   }
                                               }
                                               else
                                               {
                                                   Toast.makeText(this, "验证码不能为空!", Toast.LENGTH_SHORT).show();
                                               }
                                       }
                                       else
                                       {
                                           Toast.makeText(this, "手机号有误!", Toast.LENGTH_SHORT).show();
                                       }
                               }
                               else
                               {
                                   Toast.makeText(this, "两次密码输入不一致！", Toast.LENGTH_SHORT).show();
                               }
                       }
                       else
                       {
                           Toast.makeText(this, "密码只能而且至少包含大小写字母、数字、下划线的三个，长度至少为8", Toast.LENGTH_SHORT).show();
                       }
                   }
                   else
                   {
                       Toast.makeText(this, "用户名非法，只能包含大小写字母、数字、下划线，且不能是手机号码", Toast.LENGTH_SHORT).show();
                   }
                }
                else
                {
                    Toast.makeText(this, "请选择用户类型!", Toast.LENGTH_SHORT).show();
                }
           break;
            case R.id.txt_reg_item:
                Intent intent = new Intent(RegisterActivity.this,ServiceAgreementActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_reg_secret:
                Intent intent1 = new Intent(RegisterActivity.this,PrivacyPolicyActivity.class);
                startActivity(intent1);
        }
    }

    /**     * 计时器     */
    class TimeCount extends CountDownTimer
    {
        public TimeCount(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }         @Override
        public void onTick(long l)
       {
            btn_reg_get_identify_code.setClickable(false);
            btn_reg_get_identify_code.setText(l/1000 + "秒后重新获取");
       }         @Override
       public void onFinish()
       {
           btn_reg_get_identify_code.setClickable(true);
           btn_reg_get_identify_code.setText("获取验证码");
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
