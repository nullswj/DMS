package com.example.dms.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.dms.R;

public class NetWorkUtil {

    public  static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static void isOpenNet(final Context context)
    {
        boolean iswork = NetWorkUtil.isNetworkConnected(context);
        if(!iswork)
        {
            Log.e("zqc","进入");
            final AlertDialog.Builder bindDialog = new AlertDialog.Builder(context);
            bindDialog.setIcon(R.drawable.title);
            bindDialog.setTitle("提示");
            bindDialog.setMessage("网络连接出现问题，请请检查网络连接");
            bindDialog.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    context.startActivity(intent);
                }
            });
            bindDialog.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            bindDialog.setCancelable(false);
            AlertDialog dialog = bindDialog.create();
            dialog.show();
        }
    }
}
