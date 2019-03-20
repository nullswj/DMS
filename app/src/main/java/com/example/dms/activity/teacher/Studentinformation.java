package com.example.dms.activity.teacher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.dms.R;
import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.hyphenatechat.CallManager;
import com.example.dms.hyphenatechat.VideoCallActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMSPUtil;

import java.io.FileNotFoundException;
import java.io.IOException;



public class Studentinformation extends BaseActivity implements View.OnClickListener
{

    private Toolbar tlb_tsinfo;
    private ItemView iv_m_sname;
    private ItemView iv_m_sno;
    private ItemView iv_m_school;
    private ItemView iv_m_tel;
    private ItemView iv_m_college;
    private ItemView iv_m_major;
    private ItemView iv_m_year;
    private ItemView iv_m_class;
    private Button btn_reset_student;
    private Button btn_connect;
    private ImageView iv_image;

    private Bitmap image;
    private String name;
    private String no;
    private String school;
    private String College;
    private String grade;
    private String major;
    private String classno;
    private String tel;
    private String sid;



    private void initView()
    {
        iv_m_sname = findViewById(R.id.itemView_m_name);
        iv_m_sno = findViewById(R.id.itemView_m_sno);
        iv_m_school = findViewById(R.id.itemView_m_school);
        iv_m_college = findViewById(R.id.itemView_m_collage);
        iv_m_year = findViewById(R.id.itemView_m_grade);
        iv_m_major = findViewById(R.id.itemView_m_major);
        iv_m_class = findViewById(R.id.itemView_m_class);
        iv_m_tel = findViewById(R.id.itemView_m_tel);
        iv_image = findViewById(R.id.img_stud_info);
        //btn_studinfo_video = findViewById(R.id.studinfo_video);
        btn_reset_student = findViewById(R.id.reset_stud_info);
        btn_connect = findViewById(R.id.btn_connect);
    }

    private void setToolBar()
    {
        tlb_tsinfo = findViewById(R.id.tlb_tsinfo);
        setSupportActionBar(tlb_tsinfo);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

//    private Bitmap getBitmapFromUri(Uri uri)
//    {
//        Bitmap bitmap = null;
//        try
//        {
//            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }
    private void getParameter()
    {
        Intent intent= getIntent();
        name = intent.getStringExtra("name");
        no = intent.getStringExtra("no");
        school = intent.getStringExtra("scname");
        College = intent.getStringExtra("collage");
        grade = intent.getStringExtra("grade");
        major = intent.getStringExtra("major");
        classno = intent.getStringExtra("clname");
        tel = intent.getStringExtra("tel");
        sid = intent.getStringExtra("sid");
        //Uri uri = intent.getData();
        //image = getBitmapFromUri(uri);
        byte[] bytes = intent.getByteArrayExtra("image");
        image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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

    private void setLeftText()
    {
        iv_m_sname.setLeftTitle("姓名");
        iv_m_sno.setLeftTitle("学号");
        iv_m_school.setLeftTitle("学校");
        iv_m_college.setLeftTitle("学院");
        iv_m_year.setLeftTitle("年级");
        iv_m_major.setLeftTitle("专业");
        iv_m_class.setLeftTitle("班级");
        iv_m_tel.setLeftTitle("电话");
    }
    private void setLeftIcon()
    {
        iv_m_sname.setLeftIcon(R.drawable.ic_personal);
        iv_m_sno.setLeftIcon(R.drawable.ic_personal);
        iv_m_school.setLeftIcon(R.drawable.ic_personal);
        iv_m_college.setLeftIcon(R.drawable.ic_personal);
        iv_m_year.setLeftIcon(R.drawable.ic_personal);
        iv_m_major.setLeftIcon(R.drawable.ic_personal);
        iv_m_class.setLeftIcon(R.drawable.ic_personal);
        iv_m_tel.setLeftIcon(R.drawable.ic_personal);
    }

    private void setRightView()
    {
        iv_m_sname.setRightDesc(name);
        iv_m_sno.setRightDesc(no);
        iv_m_school.setRightDesc(school);
        iv_m_tel.setRightDesc(tel);
        iv_m_college.setRightDesc(College);
        iv_m_year.setRightDesc(grade);
        iv_m_major.setRightDesc(major);
        iv_m_class.setRightDesc(classno);
    }
    private void setButtonOnclickListener()
    {

        btn_reset_student.setOnClickListener(this);
        btn_connect.setOnClickListener(this);
        iv_m_sname.setItemClickListener(new ItemView.itemClickListener()
        {
            @Override
            public void itemClick(String text)
            {

            }
        });
        iv_m_sno.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {

            }
        });
        iv_m_school.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {

            }
        });
        iv_m_tel.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {


            }
        });
        iv_m_college.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                //Toast.makeText(Studentinformation.this,"提交成功",Toast.LENGTH_SHORT).show();
            }
        });
        iv_m_major.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                //Toast.makeText(Studentinformation.this,"提交成功",Toast.LENGTH_SHORT).show();
            }
        });
        iv_m_year.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                //Toast.makeText(Studentinformation.this,"提交成功",Toast.LENGTH_SHORT).show();
            }
        });
        iv_m_class.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                //Toast.makeText(Studentinformation.this,"提交成功",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String gethyphenateaccount()
    {
        String S_ID  = sid;
        StringBuffer sb = null;
        int len = S_ID.length();
        while (len < 12)
        {
            sb = new StringBuffer();
            sb.append("0").append(S_ID);
            S_ID = sb.toString();
            len = S_ID.length();
        }
        S_ID = "s"+S_ID;
        final String account = S_ID;
        return account;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentinformation);

        initView();
        setToolBar();
        getParameter();
        setLeftIcon();
        setLeftText();
        setRightView();
        setButtonOnclickListener();
        iv_image.setImageBitmap(image);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_connect:
                Intent intent = new Intent(Studentinformation.this,ConnectActivity.class);
                intent.putExtra("toUsername",gethyphenateaccount());
                intent.putExtra("tel",tel);
                intent.putExtra("sid",sid);
                startActivity(intent);

                break;
            case R.id.reset_stud_info:
                Toast.makeText(Studentinformation.this,"重置",Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(Studentinformation.this,ResetStudentActivity.class);
                intent1.putExtra("sid",sid);
                startActivity(intent1);
                break;
        }
    }
}
