package com.example.dms.activity.homepage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dms.R;
import com.example.dms.activitycontainer.BaseActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMSPUtil;

import java.util.ArrayList;
import java.util.List;

import static com.example.dms.activity.common.LoginActivity.gs_loginUser;
import static com.example.dms.activity.common.LoginActivity.gt_loginUser;

public class HomepageActivity extends BaseActivity {

    String TAG = "HomepageActivity";
    private TextView tv_title;
    private Fragment homePageFragment;
    private PersonStudentInfoFragment fragment;
    private Fragment[] fragments;
    private int lastfragment;
    private Button tb_button;
    public static boolean isBindPhoto = false;
    public static boolean isBindNo = false;
    public static boolean isBindLocation = false;
    public static boolean isBindSchool = false;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    tv_title.setText("主页");
                    tv_title.setGravity(Gravity.CENTER);
                    if(lastfragment!=0)
                    {
                        switchFragment(lastfragment,0);
                        lastfragment=0;
                    }
                    return true;
//                case R.id.navigation_dashboard:
//
//                    tv_title.setText("没卵用");
//                    tv_title.setGravity(Gravity.CENTER);
//                    //onetransaction.addToBackStack(null);
//                    if(lastfragment!=1)
//                    {
//                        switchFragment(lastfragment,1);
//                        lastfragment=1;
//
//                    }
//
//                    return true;
                case R.id.navigation_notifications:

                    tv_title.setText("个人中心");
                    tv_title.setGravity(Gravity.CENTER);
                    //transaction.addToBackStack(null);
                    if(lastfragment!=1)
                    {
                        switchFragment(lastfragment,1);
                        lastfragment=1;
                    }
                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Intent getIntent = this.getIntent();
        Bundle bundle = getIntent.getExtras();
        int type = -1;
        if (bundle!=null)
        {
            type = bundle.getInt("userType",-1);
            isBindNo = bundle.getBoolean("isBindNo");
            isBindPhoto = bundle.getBoolean("isBindPhoto");
            isBindNo = bundle.getBoolean("isBindNo");
            isBindLocation = bundle.getBoolean("isBindLocation");
        }
        XGPushManager.registerPush(getApplicationContext());

        Log.e("zqc","测试");
        init(type);
        getPermissions();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            EMClient.getInstance().logout(EMClient.getInstance().isConnected(), new EMCallBack() {
                @Override
                public void onSuccess()
                {
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
            finish();
        }
        return true;
    }


    private void logintcxg(String ID, int type )
    {
        if (!ID.equals(""))
        {
            StringBuffer sb = null;
            int len = ID.length();
            while (len < 12) {
                sb = new StringBuffer();
                sb.append("0").append(ID);
                ID = sb.toString();
                len = ID.length();
            }
            if (type == 1)
                ID = "s" + ID;
            else if (type == 0)
                ID = "t" + ID;
            final String account = ID;
            XGPushManager.registerPush(getApplicationContext(), account);
        }
    }


    private void init(int type)
    {
        tv_title = findViewById(R.id.toolbar_title);
        tb_button = findViewById(R.id.toolbar_button);
        tb_button.setVisibility(View.GONE);
        if(type == 1)
        {
            String ID = gs_loginUser.getsId();
            logintcxg(ID,type);
            homePageFragment = new HomePageFragment();
        }
        else
        {
            String ID = gt_loginUser.gettId();
            logintcxg(ID,type);
            homePageFragment = new TeacherHomePageFragment();
        }

        fragment = new PersonStudentInfoFragment();
        fragments = new Fragment[]{homePageFragment,fragment};//, centerFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragelayout_change,homePageFragment).show(homePageFragment).commit();
        lastfragment = 0;

    }

    private void switchFragment(int lastfragment,int index)
    {
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if(fragments[index].isAdded()==false)
        {
            transaction.add(R.id.fragelayout_change,fragments[index]);

        }
        transaction.show(fragments[index]).commitAllowingStateLoss();


    }
    private void getPermissions()
    {
        List<String> permissionList = new ArrayList<>(); //用于申请的权限
        if (ContextCompat.checkSelfPermission(HomepageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(HomepageActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(HomepageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) //如果权限为空，申请权限
        {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);

            ActivityCompat.requestPermissions(HomepageActivity.this, permissions, 1);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().logout(EMClient.getInstance().isConnected(), new EMCallBack() {
            @Override
            public void onSuccess()
            {
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
        //XGPushManager.unregisterPush(getApplicationContext());
    }
}
