package com.example.dms.activity.teacher;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dms.R;
import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.db.Collegedb;
import com.example.dms.util.getMainsocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static com.example.dms.activity.common.LoginActivity.gt_loginUser;

public class TeacherSendNoticeActivity extends BaseActivity {

    private String TAG = "TeacherSendNoticeActivity";

    private Toolbar tbl_send_notice;

    private TextInputLayout text_notice_input;

    private TextInputLayout getText_notice_tital_input;

    /**输入学院信息*/
    private Spinner spi_notice_input_college;

    /**输入年级信息*/
    private Spinner spi_notice_input_grade;

    /**输入专业信息*/
    private Spinner spi_notice_input_major;

    /**输入班级信息*/
    private Spinner spi_notice_input_class;

    private Button btn_teacher_send_notice;

    /** ip保存服务器的ip地址*/
    private String m_ip="47.106.151.249";

    /** send用于存储发送到服务器的消息*/
    private String m_send="";

    /** receive接收服务器发送过来的消息*/
    private String m_receive=null;

    private String notice_input;

    private String notice_title_input;

    private String college_ID;

    private String college_Name;

    private String grade_ID;

    private String grade_Name;

    private String major_ID;

    private String major_Name;

    private String class_ID;

    private String class_Name;

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
        text_notice_input = findViewById(R.id.text_teacher_notice_input);

        getText_notice_tital_input = findViewById(R.id.text_teacher_notice_tital_input);

        spi_notice_input_college = findViewById(R.id.spi_teacher_notice_college);

        List<Collegedb> collegedbs = LitePal.select("C_ID","C_name").find(Collegedb.class);

        listCollegeId.add("");
        listCollegeName.add("学院");
        for(Collegedb collegedb : collegedbs)
        {
            String college_name;
            String college_id;
            college_name = collegedb.getC_name();
            college_id = collegedb.getC_ID();
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
        spi_notice_input_college.setAdapter(adapter_college);

        spi_notice_input_grade = findViewById(R.id.spi_teacher_notice_grade);

        listGradeId.add("");
        listGradename.add("年级");
        SpinnerAdapter adapter_grade = new SpinnerAdapter(this,listGradename);
        adapter_grade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_notice_input_grade.setAdapter(adapter_grade);

        spi_notice_input_major = findViewById(R.id.spi_teacher_notice_major);

        listMajorId.add("");
        listMajorname.add("专业");
        SpinnerAdapter adapter_major = new SpinnerAdapter(this,listMajorname);
        adapter_major.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_notice_input_major.setAdapter(adapter_major);

        spi_notice_input_class =findViewById(R.id.spi_teacher_notice_class);

        listClassId.add("");
        listClassname.add("班级");
        SpinnerAdapter adapter_class = new SpinnerAdapter(this,listClassname);
        adapter_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_notice_input_class.setAdapter(adapter_class);
    }

    private void setButtonClick()
    {
        spi_notice_input_college.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String item = (String)spi_notice_input_college.getItemAtPosition(position);
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
                        listGradeId.add("");
                        listGradename.add("年级");

                        if (!college_ID.equals(""))
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
                        spi_notice_input_grade.setAdapter(adapter_grade);


                        listMajorId.clear();
                        listMajorname.clear();
                        listMajorId.add("");
                        listMajorname.add("专业");
                        SpinnerAdapter adapter_major = new SpinnerAdapter(getApplicationContext(),listMajorname);
                        adapter_major.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spi_notice_input_major.setAdapter(adapter_major);

                        listClassId.clear();
                        listClassname.clear();
                        listClassId.add("");
                        listClassname.add("班级");
                        SpinnerAdapter adapter_class = new SpinnerAdapter(getApplicationContext(),listClassname);
                        adapter_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spi_notice_input_class.setAdapter(adapter_class);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        college_ID = "";
                    }
                });

        spi_notice_input_grade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)spi_notice_input_grade.getItemAtPosition(position);
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
                listMajorId.add("");
                listMajorname.add("专业");

                if (!grade_ID.equals(""))
                {
                    List<Collegedb> majors = LitePal.select("M_ID","Major").where("C_ID = ?" ,college_ID).where("G_ID = ?" , grade_ID).find(Collegedb.class);
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
                spi_notice_input_major.setAdapter(adapter_major);

                listClassId.clear();
                listClassname.clear();
                listClassId.add("");
                listClassname.add("班级");
                SpinnerAdapter adapter_class = new SpinnerAdapter(getApplicationContext(),listClassname);
                adapter_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spi_notice_input_class.setAdapter(adapter_class);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                grade_ID = "";
            }
        });

        spi_notice_input_major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)spi_notice_input_major.getItemAtPosition(position);
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
                listClassId.add("");
                listClassname.add("班级");

                if (!major_ID.equals(""))
                {
                    List<Collegedb> classes = LitePal.select("CL_ID","Classname").where("C_ID = ?" ,college_ID).where("G_ID = ?" , grade_ID).where("M_ID = ?", major_ID).find(Collegedb.class);

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
                spi_notice_input_class.setAdapter(adapter_class);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                major_ID = "";
            }
        });

        spi_notice_input_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String item = (String)spi_notice_input_class.getItemAtPosition(position);
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
                class_ID = "";
            }
        });

        btn_teacher_send_notice = findViewById(R.id.btn_teacher_notice_send);
        btn_teacher_send_notice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                notice_input = text_notice_input.getEditText().getText().toString().trim();
                Log.e(TAG, "onClick: " + notice_input );
                notice_title_input = getText_notice_tital_input.getEditText().getText().toString().trim();

                Log.e(TAG, "onClick: " + notice_title_input );

                Log.e(TAG, "onClick: college_ID"+ college_ID);
                Log.e(TAG, "onClick: grade_ID"+ grade_ID);
                Log.e(TAG, "onClick: major_ID"+ major_ID );
                Log.e(TAG, "onClick: class_ID"+class_ID );

                try //封装信息
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("request_type","11");//
                    m_send=jsonObject.toString();
                    getMainsocket.connMainsocket(TAG,m_send,handler);
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
                        jsonObjectsend.put("scid","");
                        jsonObjectsend.put("cid",college_ID);//此处封装的是学号
                        jsonObjectsend.put("gid",grade_ID);
                        jsonObjectsend.put("mid",major_ID);
                        jsonObjectsend.put("clid",class_ID);
                        jsonObjectsend.put("title",notice_title_input);
                        jsonObjectsend.put("message",notice_input);
                        jsonObjectsend.put("tid",gt_loginUser.gettId());
                        //jsonObjectsend.put("scid",);
                        //jsonObjectsend.put("tid",);

                        m_send = jsonObjectsend.toString();

                        Log.e(TAG, "run: "+ m_send);

                        Socket socket = new Socket();
                        SocketAddress address = new InetSocketAddress(m_ip,release_port);
                        socket.connect(address,10000);
                        socket.setSoTimeout(10000);

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);
                        out.close();
                        socket.close();

                        Message message = new Message();
                        message.what = 0;
                        Toasthandler.sendMessage(message);

                        finish();

                        //Toast.makeText(TeacherSendNoticeActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
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

    Handler Toasthandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(TeacherSendNoticeActivity.this,"发布成功",Toast.LENGTH_LONG).show();
        }
    };

    private void showBcakDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(TeacherSendNoticeActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("发送通知");
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
        tbl_send_notice = findViewById(R.id.tlb_teacher_send_notice);
        setSupportActionBar(tbl_send_notice);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_send_notice);

        initView();
        setToolBar();
        setButtonClick();

        Intent intent=getIntent();

    }
}
