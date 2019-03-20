package com.example.dms;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Process;

import com.example.dms.entity.User;

import com.example.dms.hyphenatechat.CallManager;
import com.example.dms.hyphenatechat.CallReceiver;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.mob.wrappers.UMSSDKWrapper;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.vmloft.develop.library.tools.VMTools;

import org.litepal.LitePal;

import java.util.List;

/**
 * <pre>
 *     author : marin
 *     time   : 2018/11/27
 *     desc   : 初始化sdk，保存用户信息，注册广播监听器
 *     version: 1.0
 * </pre>
 */
public class DmsApplication extends Application {

    private boolean      mIsInit = false;
    private CallReceiver callReceiver;
    private Context      context;

    @Override
    public void onCreate ()
    {
        super.onCreate();
        LitePal.initialize(this);

        //XGPushConfig.enableOtherPush(getApplicationContext(), true);
        //XGPushConfig.setHuaweiDebug(true);
        XGPushConfig.enableDebug(this,false);
        context = this;
        init();
    }



    private void init ()
    {
        VMTools.init(context);
        int pid = Process.myPid();
        String processAppName = getAppName(pid);
        /*
          如果app启用了远程的service，此application:onCreate会被调用2次
          为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
          默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
         */
        if (processAppName == null || !processAppName.equalsIgnoreCase(getApplicationContext()
                .getPackageName())) {
            return;
        }

        if (mIsInit) {
            return;
        }

        /*
          SDK初始化的一些配置
         */
        EMOptions options = new EMOptions();
        // 设置自动登录
        options.setAutoLogin(true);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执，TODO 这个暂时有bug，上层收不到发送回执
        options.setRequireDeliveryAck(true);
        // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
        options.setAcceptInvitationAlways(true);
        // 调用初始化方法初始化sdk
        EMClient.getInstance().init(getApplicationContext(), options);
        // 设置开启debug模式
        EMClient.getInstance().setDebugMode(true);
        // 设置通话广播监听器
        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager()
                .getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }
        //注册通话广播接收者
        context.registerReceiver(callReceiver, callFilter);

        CallManager.getInstance().setExternalInputData(false);
        // 通话管理类的初始化
        CallManager.getInstance().init(context);


        EMClient.getInstance().callManager().getCallOptions().setIsSendPushIfOffline(false);
        // 设置初始化已经完成
        mIsInit = true;
    }

    /*
      根据Pid获取当前进程名字，并返回。一般就是当前app包名
     */
    private String getAppName (int pid) {
        String processName;
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        for (Object aList : list) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)
                    (aList);
            try {
                if (info.pid == pid) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

