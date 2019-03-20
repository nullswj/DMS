package com.example.dms.activity.teacher;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;


import com.example.dms.R;
import com.example.dms.activitycontainer.BaseActivity;
import com.tencent.android.tpush.XGPushManager;

public class TeacherMsgInfoActivity extends BaseActivity
{
    private Toolbar tbl_Teacher_MsgInfo;
    private TextView tv_name;

    private TextView tv_college;

    private TextView tv_deptinfo;

    private TextView tv_time;

    private TextView tv_content;

    private Bundle xgnotification;

    private void setToolBar()
    {
        tbl_Teacher_MsgInfo = findViewById(R.id.tlb_teacher_push_info);
        setSupportActionBar(tbl_Teacher_MsgInfo);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView()
    {
        tv_name = findViewById(R.id.item_push_info_sname);
        tv_college = findViewById(R.id.item_push_info_college);
        tv_deptinfo = findViewById(R.id.item_push_info_class);
        tv_time = findViewById(R.id.item_push_info_time);
        tv_content = findViewById(R.id.item_push_info_content);
    }

    private void setText()
    {
        tv_name.setText(xgnotification.getString("sname"));
        tv_college.setText(xgnotification.getString("scollege"));
        String grade = xgnotification.getString("sgrade");
        String major = xgnotification.getString("smajor");
        String sclass = xgnotification.getString("sclass");

        tv_deptinfo.setText(grade+major+sclass);

        tv_time.setText(xgnotification.getString("update_time"));
        tv_content.setText(xgnotification.getString("content"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_msginfo);

        xgnotification = this.getIntent().getExtras();
        setToolBar();
        initView();
        setText();
//        textView = (TextView) this.findViewById(R.id.activityType);
//        TextView textViewContent = (TextView) this
//                .findViewById(R.id.activityContent);
//        if (xgnotification.getInt("notificationActionType", 0) == 1) {
//            textView.setText("特定页面：");
//        } else if (xgnotification.getInt("notificationActionType", 0) == 2) {
//            textView.setText(" URL：");
//        } else if (xgnotification.getInt("notificationActionType", 0) == 3) {
//            textView.setText("Intent:");
//        }
//        textViewContent.setText(xgnotification.getString("activity"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        XGPushManager.onActivityStarted(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        XGPushManager.onActivityStoped(this);
    }

}
