package com.example.dms.activitycontainer;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AtyContainer {

    private AtyContainer() {
    }

    private static AtyContainer instance = new AtyContainer();
    private static List<AppCompatActivity> activityStack = new ArrayList<AppCompatActivity>();

    public static AtyContainer getInstance() {
        return instance;
    }

    public void addActivity(AppCompatActivity aty) {
        activityStack.add(aty);
    }

    public void removeActivity(AppCompatActivity aty) {
        activityStack.remove(aty);
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public static Boolean isActivityEmpty()
    {
        if (activityStack == null || activityStack.isEmpty())
            return false;
        else
            return true;
    }

    public static boolean getActivityCompat()
    {
        for (AppCompatActivity activityCompat: activityStack)
        {
            if (activityCompat.getClass().equals("HomepageActivity"))
            {
                return true;
            }
        }
        return false;
    }

    public static int getactivityStackSize()
    {
        return activityStack.size();
    }
//    public static void exitLogout()
//    {
//        for(AppCompatActivity appCompatActivity : activityStack)
//        {
//            if(!LoginActivity.class.equals(appCompatActivity))
//            {
//                appCompatActivity.finish();
//            }
//        }
//    }
}
