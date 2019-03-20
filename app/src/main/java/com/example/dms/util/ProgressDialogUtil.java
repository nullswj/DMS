package com.example.dms.util;

import android.app.ProgressDialog;
import android.content.Context;


public class ProgressDialogUtil {
    public static ProgressDialog progressDialog = null;

    public static void dailogProgress(String title, Context context)
    {
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(title);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public static void cancel()
    {
        progressDialog.dismiss();
        progressDialog = null;
    }
}
