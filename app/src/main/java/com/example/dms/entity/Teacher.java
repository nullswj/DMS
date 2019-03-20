package com.example.dms.entity;

import android.content.Context;

public class Teacher extends User
{
    private  String T_ID;

    private  String T_No;

    private  String T_name;

    public Teacher(Context context) {
        super(context);
    }

    public  String getT_name() {
        return T_name;
    }

    public  void setT_name(String t_name) {
        T_name = t_name;
    }

    public  String getT_No() {
        return T_No;
    }

    public  void setT_No(String t_No) {
        T_No = t_No;
    }

    public  String gettId() {
        return T_ID;
    }

    public  void settId(String tId) {
        T_ID = tId;
    }

    @Override
    public void clear() {
        super.clear();
        meditor.putString("T_ID",null);
        meditor.putString("T_No",null);
        meditor.putString("T_name",null);
        meditor.apply();
    }

    @Override
    public void commit() {
        super.commit();
        meditor.putString("T_ID",T_ID);
        meditor.putString("T_No",T_No);
        meditor.putString("T_name",T_name);
        meditor.apply();
    }

    @Override
    public void applyUser() {
        super.applyUser();
        T_ID = msharedPreferences.getString("T_ID",null);
        T_No = msharedPreferences.getString("T_No",null);
        T_name =msharedPreferences.getString("T_name",null);
    }
}
