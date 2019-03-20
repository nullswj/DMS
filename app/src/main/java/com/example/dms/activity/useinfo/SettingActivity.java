package com.example.dms.activity.useinfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dms.activity.common.Forgetpw_reset_Activity;
import com.example.dms.activity.common.LoginActivity;
import com.example.dms.activity.teacher.ItemView;
import com.example.dms.activitycontainer.AtyContainer;
import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.tencent.android.tpush.XGPushManager;
import com.vmloft.develop.library.tools.utils.VMLog;

import static com.example.dms.activity.common.LoginActivity.gs_loginUser;
import static com.example.dms.activity.common.LoginActivity.gt_loginUser;
import static com.example.dms.activity.student.student_work.StudentMessageActivity.clear2;
import static com.example.dms.activity.teacher.TeacherMessageActivity.clear1;


public class SettingActivity extends BaseActivity {

    private ItemView iv_change;
    private ItemView iv_logout;
    private ItemView iv_quit;
    private ItemView iv_changeName;
    private TextView tv_title;
    private Button btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();


        iv_change.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                //Toast.makeText(SettingActivity.this,"真的修改密码吗？弟弟",Toast.LENGTH_SHORT).show();
                Intent setPhoneIntent = new Intent(SettingActivity.this,Forgetpw_reset_Activity.class);
                if (gs_loginUser.isIsvalid())
                    setPhoneIntent.putExtra("tel",gs_loginUser.getPhone());
                else if (gt_loginUser.isIsvalid())
                    setPhoneIntent.putExtra("tel",gt_loginUser.getPhone());
                startActivity(setPhoneIntent);
            }
        });
        iv_logout.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                //Toast.makeText(SettingActivity.this,"真的退出登陆吗？弟弟",Toast.LENGTH_SHORT).show();
                AtyContainer.finishAllActivity();
                Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isauto",false);
                intent.putExtras(bundle);
                int type = LoginActivity.wahtType();
                if (type == 0)
                {
                    clear1();
                    gt_loginUser.clear();
                }

                else if (type == 1)
                {
                    clear2();
                    gs_loginUser.clear();
                }




                XGPushManager.unregisterPush(getApplicationContext());
                EMClient.getInstance().logout(EMClient.getInstance().isConnected(), new EMCallBack()
                {
                    @Override
                    public void onSuccess() {
                        VMLog.i("logout success");
                    }

                    @Override
                    public void onError(int i, String s)
                    {
                        String errorMsg = "logout error: " + i + "; " + s;
                        VMLog.i(errorMsg);
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
                startActivity(intent);
            }
        });
        iv_quit.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                //Toast.makeText(SettingActivity.this,"就不让你退出！弟弟",Toast.LENGTH_SHORT).show();
                AtyContainer.finishAllActivity();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initView()
    {
        iv_change = findViewById(R.id.Item_changepwd);
        tv_title = findViewById(R.id.toolbar_title);
        tv_title.setText("设置");
        iv_logout = findViewById(R.id.Item_logout);
        btn_back = findViewById(R.id.toolbar_button);
        iv_quit = findViewById(R.id.Item_quit);
        //iv_changeName = findViewById(R.id.Item_changeName);
    }

}
