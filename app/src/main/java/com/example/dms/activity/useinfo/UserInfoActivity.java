package com.example.dms.activity.useinfo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dms.activity.common.LoginActivity;
import com.example.dms.activity.teacher.ItemView;
import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.R;
import com.example.dms.entity.Student;
import com.example.dms.entity.Teacher;

import static com.example.dms.activity.common.LoginActivity.gs_loginUser;


public class UserInfoActivity extends BaseActivity {

    private String TAG = "UserInfoActivity";
    private ItemView iv_nickname;
    private ItemView iv_realname;
    private ItemView iv_school;
    private ItemView iv_college;
    private ItemView iv_major;
    private ItemView iv_year;
    private  ItemView iv_class;
    private TextView textView;
    private Button tb_button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_setting);
        initView();

        iv_nickname.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {



            }
        });
        iv_realname.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {


            }
        });
        iv_school.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {


            }
        });
        iv_college.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {


            }
        });
        iv_major.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {


            }
        });
        iv_year.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {


            }
        });
        iv_class.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {


            }
        });
        tb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView()
    {
        iv_nickname = findViewById(R.id.itemView_nickname);

        iv_realname = findViewById(R.id.itemView_realname);

        iv_school = findViewById(R.id.itemView_school);

        iv_college = findViewById(R.id.itemView_college);

        iv_major = findViewById(R.id.itemView_major);

        iv_year = findViewById(R.id.itemView_year);

        textView = findViewById(R.id.toolbar_title);
        tb_button = findViewById(R.id.toolbar_button);
        iv_class = findViewById(R.id.itemView_sclass);
        textView.setText("个人资料");

        iv_nickname.setRightDesc(gs_loginUser.getUsername());
        Log.e(TAG, "initView: "+gs_loginUser.getUsername());
        iv_realname.setRightDesc(gs_loginUser.getS_name());
        Log.e(TAG, "initView: "+gs_loginUser.getUsername());
        iv_school.setRightDesc(gs_loginUser.getSC_name());
        Log.e(TAG, "initView: "+gs_loginUser.getUsername());
        iv_college.setRightDesc(gs_loginUser.getC_name());
        Log.e(TAG, "initView: "+gs_loginUser.getUsername());
        iv_major.setRightDesc(gs_loginUser.getMajor());
        Log.e(TAG, "initView: "+gs_loginUser.getUsername());
        iv_year.setRightDesc(gs_loginUser.getGrade());
        Log.e(TAG, "initView: "+gs_loginUser.getUsername());
        iv_class.setRightDesc(gs_loginUser.getClassName());
        Log.e(TAG, "initView: "+gs_loginUser.getUsername());
    }


}
