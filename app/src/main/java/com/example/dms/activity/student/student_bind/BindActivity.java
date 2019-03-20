package com.example.dms.activity.student.student_bind;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.example.dms.activity.common.LoginActivity;
import com.example.dms.activity.teacher.SpinnerAdapter;

import android.os.Handler;
import android.os.Message;

import com.example.dms.activity.useinfo.SettingActivity;
import com.example.dms.activitycontainer.AtyContainer;
import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.R;
import com.example.dms.db.Collegedb;
import com.example.dms.util.getMainsocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import static com.example.dms.activity.common.LoginActivity.gs_loginUser;
import static com.example.dms.activity.common.LoginActivity.gt_loginUser;


/**
 * 文件名 BindActivity
 * 功能 绑定个人信息界面
 * 描述 继承AppCompatActivity ，实现了 View.OnClickListener接口
 * 版本 1.0
 */
public class BindActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "BindActivity";
    Toolbar bind_tlb;
    /**绑定信息按钮*/
    private Button btn_bind_bind;

    /**输入学院信息*/
    private Spinner spi_bind_input_college;

    /**输入年级信息*/
    private Spinner spi_bind_input_grade;

    /**输入专业信息*/
    private Spinner spi_bind_input_major;

    /**输入班级信息*/
    private Spinner spi_bind_input_class;

    private String m_user_name;

    /** type用于保存用户类型*/
    private  int m_type;//账户类型

    /** ip保存服务器的ip地址*/
    private String m_ip="47.106.151.249";

    private String m_s_id;

    /** send用于存储发送到服务器的消息*/
    private String m_send="";

    /** receive接收服务器发送过来的消息*/
    private String m_receive=null;

    private String college_ID;

    private String college_Name;

    private String grade_ID;

    private String grade_Name;

    private String major_ID;

    private String major_Name;

    private String class_ID;

    private String class_Name;

    private String m_sc_name;

    private String m_s_no;

    private boolean isBindPhoto;

    private boolean isBindLocation;

    private List<String> listCollegeId = new ArrayList<>();

    private List<String> listCollegeName = new ArrayList<>();

    private List<String> listGradeId = new ArrayList<>();

    private List<String> listGradename = new ArrayList<>();

    private List<String> listMajorname = new ArrayList<>();

    private List<String> listMajorId = new ArrayList<>();

    private List<String> listClassname = new ArrayList<>();

    private List<String> listClassId = new ArrayList<>();




    private void initView()
    {
        //初始化变量
        btn_bind_bind=findViewById(R.id.btn_bind_bind);


        spi_bind_input_college = findViewById(R.id.spi_bind_input_college);

        List<Collegedb> collegedbs = LitePal.select("C_ID","C_name").find(Collegedb.class);

        listCollegeId.add("-1");
        listCollegeName.add("学院");
        for(Collegedb collegedb : collegedbs)
        {
            String college_name;
            String college_id;
            college_name = collegedb.getC_name();
            college_id = collegedb.getC_ID();
            Log.e(TAG, "initView: college_id"+ college_id);
            Log.e(TAG, "initView: college_name"+ college_name);
            listCollegeId.add(college_id);
            listCollegeName.add(college_name);
        }
        for(int i = 0; i < listCollegeName.size()-1; i++)
        {
            for(int j = listCollegeName.size()-1; j > i; j--)
            {
                if(listCollegeName.get(j).equals(listCollegeName.get(i)))
                {
                    listCollegeName.remove(j);
                    listCollegeId.remove(j);
                }
            }
        }

        SpinnerAdapter adapter_college = new SpinnerAdapter(this,listCollegeName);
        adapter_college.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_bind_input_college.setAdapter(adapter_college);

        spi_bind_input_grade = findViewById(R.id.spi_bind_input_grade);

        listGradeId.add("-1");
        listGradename.add("年级");
        SpinnerAdapter adapter_grade = new SpinnerAdapter(this,listGradename);
        adapter_grade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_bind_input_grade.setAdapter(adapter_grade);

        spi_bind_input_major = findViewById(R.id.spi_bind_input_major);

        listMajorId.add("-1");
        listMajorname.add("专业");
        SpinnerAdapter adapter_major = new SpinnerAdapter(this,listMajorname);
        adapter_major.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_bind_input_major.setAdapter(adapter_major);

        spi_bind_input_class =findViewById(R.id.spi_bind_input_class);

        listClassId.add("-1");
        listClassname.add("班级");
        SpinnerAdapter adapter_class = new SpinnerAdapter(this,listClassname);
        adapter_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_bind_input_class.setAdapter(adapter_class);
    }

    private void setButtonClick()
    {
        btn_bind_bind.setOnClickListener(this);

        spi_bind_input_college.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String item = (String)spi_bind_input_college.getItemAtPosition(position);
                        int len = listCollegeName.size();
                        for(int i = 0; i < len; i++)
                        {
                            if (item.equals(listCollegeName.get(i)))
                            {
                                college_ID = listCollegeId.get(i);
                                Log.e(TAG, "onItemSelected: college_ID"+college_ID );
                                college_Name = listCollegeName.get(i);
                                Log.e(TAG, "onItemSelected: college_Name"+college_Name );
                            }
                        }

                        listGradename.clear();
                        listGradeId.clear();
                        listGradeId.add("-1");
                        listGradename.add("年级");

                        if (!college_ID.equals("-1"))
                        {
                            List<Collegedb> grades = LitePal.select("G_ID","Grade").where("C_ID = ?" ,college_ID).find(Collegedb.class);

                            for(Collegedb collegedb : grades)
                            {
                                String grade_name;
                                String grade_id;
                                grade_name = collegedb.getGrade();
                                grade_id = collegedb.getG_ID();
                                listGradeId.add(grade_id);
                                listGradename.add(grade_name);
                            }

                            for(int i = 0; i < listGradename.size()-1; i++)
                            {
                                for(int j = listGradename.size()-1; j > i; j--)
                                {
                                    if(listGradename.get(j).equals(listGradename.get(i)))
                                    {
                                        listGradename.remove(j);
                                        listGradeId.remove(j);
                                    }
                                }
                            }
                        }

                        SpinnerAdapter adapter_grade = new SpinnerAdapter(getApplicationContext(),listGradename);
                        adapter_grade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spi_bind_input_grade.setAdapter(adapter_grade);


                        listMajorId.clear();
                        listMajorname.clear();
                        listMajorId.add("-1");
                        listMajorname.add("专业");
                        SpinnerAdapter adapter_major = new SpinnerAdapter(getApplicationContext(),listMajorname);
                        adapter_major.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spi_bind_input_major.setAdapter(adapter_major);

                        listClassId.clear();
                        listClassname.clear();
                        listClassId.add("-1");
                        listClassname.add("班级");
                        SpinnerAdapter adapter_class = new SpinnerAdapter(getApplicationContext(),listClassname);
                        adapter_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spi_bind_input_class.setAdapter(adapter_class);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        college_ID = "-1";
                    }
                });

        spi_bind_input_grade.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)spi_bind_input_grade.getItemAtPosition(position);
                int len = listGradename.size();
                for(int i = 0; i < len; i++)
                {
                    if (item.equals(listGradename.get(i)))
                    {
                        grade_ID = listGradeId.get(i);
                        Log.e(TAG, "onItemSelected: grade_ID"+grade_ID );
                        grade_Name = listGradename.get(i);
                        Log.e(TAG, "onItemSelected: grade_Name"+grade_Name );
                    }

                }

                listMajorId.clear();
                listMajorname.clear();
                listMajorId.add("-1");
                listMajorname.add("专业");

                if (!grade_ID.equals("-1"))
                {
                    List<Collegedb> majors = LitePal.select("M_ID","Major").where("C_ID = ? and G_ID = ?" ,college_ID,grade_ID).find(Collegedb.class);

                    for(Collegedb collegedb : majors)
                    {
                        String major_name;
                        String major_id;
                        major_name = collegedb.getMajor();
                        major_id = collegedb.getM_ID();
                        listMajorId.add(major_id);
                        listMajorname.add(major_name);
                    }

                    for(int i = 0; i < listMajorname.size()-1; i++)
                    {
                        for(int j = listMajorname.size()-1; j > i; j--)
                        {
                            if(listMajorname.get(j).equals(listMajorname.get(i)))
                            {
                                listMajorname.remove(j);
                                listMajorId.remove(j);
                            }
                        }
                    }
                }

                SpinnerAdapter adapter_major = new SpinnerAdapter(getApplicationContext(),listMajorname);
                adapter_major.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spi_bind_input_major.setAdapter(adapter_major);

                listClassId.clear();
                listClassname.clear();
                listClassId.add("-1");
                listClassname.add("班级");
                SpinnerAdapter adapter_class = new SpinnerAdapter(getApplicationContext(),listClassname);
                adapter_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spi_bind_input_class.setAdapter(adapter_class);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                grade_ID = "-1";
            }
        });

        spi_bind_input_major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)spi_bind_input_major.getItemAtPosition(position);
                int len = listMajorname.size();
                for(int i = 0; i < len; i++)
                {
                    if (item.equals(listMajorname.get(i)))
                    {
                        major_ID = listMajorId.get(i);
                        Log.e(TAG, "onItemSelected: major_ID"+major_ID );
                        major_Name = listMajorname.get(i);
                        Log.e(TAG, "onItemSelected: major_Name"+major_Name );
                    }

                }

                listClassId.clear();
                listClassname.clear();
                listClassId.add("-1");
                listClassname.add("班级");

                if (!major_ID.equals("-1"))
                {
                    List<Collegedb> classes = LitePal.select("CL_ID","Classname").where("C_ID = ? and G_ID = ? and M_ID = ?" ,college_ID,grade_ID,major_ID).find(Collegedb.class);

                    for(Collegedb collegedb : classes)
                    {
                        String class_name;
                        String class_id;
                        class_name = collegedb.getClassname();
                        class_id = collegedb.getCL_ID();
                        listClassId.add(class_id);
                        listClassname.add(class_name);
                    }

                    for(int i = 0; i < listClassname.size()-1; i++)
                    {
                        for(int j = listClassname.size()-1; j > i; j--)
                        {
                            if(listClassname.get(j).equals(listClassname.get(i)))
                            {
                                listClassname.remove(j);
                                listClassId.remove(j);
                            }
                        }
                    }
                }

                SpinnerAdapter adapter_class = new SpinnerAdapter(getApplicationContext(),listClassname);
                adapter_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spi_bind_input_class.setAdapter(adapter_class);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                major_ID = "-1";
            }
        });

        spi_bind_input_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)spi_bind_input_class.getItemAtPosition(position);
                int len = listClassname.size();
                for(int i = 0; i < len; i++)
                {
                    if (item.equals(listClassname.get(i)))
                    {
                        class_ID = listClassId.get(i);
                        Log.e(TAG, "onItemSelected: class_ID"+class_ID );
                        class_Name = listClassname.get(i);
                        Log.e(TAG, "onItemSelected: class_Name"+class_Name );
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                    class_ID = "-1";
            }
        });
    }

    private void showSubmitStudentDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(BindActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("信息绑定");
        bindDialog.setMessage("班级信息绑定成功!\n是否立即绑定照片?\n绑定照片时，请拍摄本人的正脸照，并保证照片的清晰度");

        bindDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(BindActivity.this,BindingPhotoActivity.class);
                intent.putExtra("user_name",m_user_name);
                intent.putExtra("s_id",m_s_id);
                intent.putExtra("loc",isBindLocation);
                startActivity(intent);
                finish();
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

    private void showSubmitStudentLocationDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(BindActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("信息绑定");
        bindDialog.setMessage("班级信息绑定成功!\n是否立即上传位置?\n上传位置时，请确保设备位于寝室");

        bindDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(BindActivity.this,BindingLocationActivity.class);
                intent.putExtra("user_name",m_user_name);
                intent.putExtra("s_id",m_s_id);
                startActivity(intent);
                finish();
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
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(BindActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("信息绑定");
        bindDialog.setMessage("放弃未提交的输入，确认返回？");

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
        bind_tlb = (Toolbar)findViewById(R.id.tlb_bind_info);
        setSupportActionBar(bind_tlb);
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
     * 描述 重写父类的onCreate()方法
     * 功能 加载界面布局
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);

        initView();
        setToolBar();
        setButtonClick();

        Intent intent=getIntent();
        m_type=1; //接受上一个界面传过来的参数
        m_user_name = intent.getStringExtra("user_name");
        m_s_id = intent.getStringExtra("s_id");
        m_s_no = intent.getStringExtra("s_no");
        m_sc_name = intent.getStringExtra("sc_name");
        isBindPhoto = intent.getBooleanExtra("photo",false);
        isBindLocation = intent.getBooleanExtra("loc",false);

    }





    /** handler用于处理二次通信的线程*/
    private Handler handler = new Handler()
    {
        public void handleMessage(final Message msg)
        {
            final int vet_port = msg.arg1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        JSONObject jsonObjectsend = new JSONObject();
                        jsonObjectsend.put("s_no",m_s_no);//此处封装的是学号
                        jsonObjectsend.put("S_ID",m_s_id);//此处封装的是学号
                        jsonObjectsend.put("college",college_Name);//学院
                        jsonObjectsend.put("grade",grade_Name);//年级
                        jsonObjectsend.put("major",major_Name);//专业
                        jsonObjectsend.put("input_class",class_Name);//class
                        jsonObjectsend.put("sc_name",m_sc_name);//class
                        m_send=jsonObjectsend.toString();

                        //新建立一个连接
                        Socket socket = new Socket();
                        SocketAddress address = new InetSocketAddress(m_ip,vet_port);
                        //连接时间设置
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);
                        // Log.d("zqc","握手成功");

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);
                        out.close();
                        socket.close();

                        Thread.sleep(1000);
                        //输出流
                        socket = new Socket("47.106.151.249",vet_port);      //创建socket连接
                        socket.setSoTimeout(10000);                                          //设置超时
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        m_receive = in.readUTF();
                        //Log.d("zqc",receive);
                        JSONObject jsonObject = new JSONObject(m_receive);
                        int vet_result = Integer.parseInt(jsonObject.getString("CGMTstate"));
                        //Log.d("zqc","开始mes");

                        //向其他线程发送消息
                        Message message = new Message();
                        message.what = 0;
                        message.arg1 = vet_result;
                        mHandler.sendMessage(message);
                        //Log.d("zqc","结束mes");

                        //关闭流
                        in.close();
                        socket.close();

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

    //接收线程发送过来信息，并用Toast显示
    /** mhandler用于接受子线程发过来的消息，并阻塞主线程*/
    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            int massege=msg.arg1;
            if(massege == 1)
            {
                LitePal.deleteAll(Collegedb.class);
                if (isBindPhoto)
                {
                    showSubmitStudentDialog();
                }
                else if (isBindLocation)
                {
                    showSubmitStudentLocationDialog();
                }
                else
                {
                    Toast.makeText(BindActivity.this,"信息完善成功",Toast.LENGTH_LONG).show();
                    AtyContainer.finishAllActivity();
                    Intent intent = new Intent(BindActivity.this,LoginActivity.class);
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
            else
            {
                Toast.makeText(getApplicationContext(), "绑定失败,请检查电话号码和密码",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 重写了 View.OnClickListener接口内的方法
     * 功能 实现按钮的监听
     * @param view 界面的监听
     */
    @Override
    public void onClick(View view)
    {
        switch (view.getId()) //通过按钮的Id判断监听
        {
            case R.id.btn_bind_bind: //确认绑定按钮
                Log.e(TAG, "onClick: college_ID"+ college_ID);
                Log.e(TAG, "onClick: grade_ID"+ grade_ID);
                Log.e(TAG, "onClick: major_ID"+ major_ID );
                Log.e(TAG, "onClick: class_ID"+class_ID );
                if(!college_ID.equals("-1") && !grade_ID.equals("-1") && !major_ID.equals("-1") && !class_ID.equals("-1"))
                {
                    try //封装信息
                    {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("request_type","7");//
                        m_send=jsonObject.toString();
                        getMainsocket.connMainsocket(TAG,m_send,handler);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    Log.v("zqc",""+m_type);
                }
                else
                {
                    Toast.makeText(BindActivity.this,"请选择班级信息",Toast.LENGTH_LONG);
                }
                break;
        }
    }
}

