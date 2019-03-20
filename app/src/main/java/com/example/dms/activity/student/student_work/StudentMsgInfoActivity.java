package com.example.dms.activity.student.student_work;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;


import com.example.dms.R;
import com.example.dms.activitycontainer.BaseActivity;
import com.tencent.android.tpush.XGPushManager;

public class StudentMsgInfoActivity extends BaseActivity
{
    private Toolbar tbl_Student_MsgInfo;

    private TextView tv_name;

    private TextView tv_time;

    private TextView tv_content;

    private Bundle xgnotification;

    private void setToolBar()
    {
        tbl_Student_MsgInfo = findViewById(R.id.tlb_student_push_info);
        setSupportActionBar(tbl_Student_MsgInfo);
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
        tv_name = findViewById(R.id.item_push_info_tname);
        tv_time = findViewById(R.id.item_push_student_info_time);
        tv_content = findViewById(R.id.item_push_student_info_content);
    }

    private void setText()
    {
        tv_name.setText(xgnotification.getString("sname"));
        tv_time.setText(xgnotification.getString("update_time"));
        tv_content.setText(xgnotification.getString("content"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_msg_info);

        xgnotification = this.getIntent().getExtras();
        setToolBar();
        initView();
        setText();
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
