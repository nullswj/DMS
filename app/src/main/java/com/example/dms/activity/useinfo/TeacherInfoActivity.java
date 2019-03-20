package com.example.dms.activity.useinfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dms.R;
import com.example.dms.activity.teacher.ItemView;
import com.example.dms.activitycontainer.BaseActivity;

import static com.example.dms.activity.common.LoginActivity.gt_loginUser;


public class TeacherInfoActivity extends BaseActivity {

    private ItemView iv_nickname;
    private ItemView iv_realname;
    private ItemView iv_school;
    private ItemView iv_college;
    private TextView textView;
    private Button tb_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);
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
        tb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initView()
    {
        iv_nickname = findViewById(R.id.itemView_teacher_nickname);

        iv_realname = findViewById(R.id.itemView_teacher_realname);

        iv_school = findViewById(R.id.itemView_teacher_school);

        iv_college = findViewById(R.id.itemView_teacher_college);

        textView = findViewById(R.id.toolbar_title);
        tb_button = findViewById(R.id.toolbar_button);

        textView.setText("个人资料");
        iv_nickname.setRightDesc(gt_loginUser.getUsername());
        iv_realname.setRightDesc(gt_loginUser.getT_name());
        iv_school.setRightDesc(gt_loginUser.getSC_name());
        //iv_college.setRightDesc(gt_loginUser.getC_name());
    }
}
