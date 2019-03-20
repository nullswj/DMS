package com.example.dms.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class User
{
    protected  String SC_ID = null;
    protected String SC_name = null;

    protected  String User_name = null;
    protected  String Password = null;
    protected  String Phone = null;
    protected   int A_ID = -1;
    protected boolean isvalid = false;

    protected SharedPreferences msharedPreferences ;
    protected SharedPreferences.Editor meditor;

    public User(Context context)
    {
         msharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);;
    }

    public  int getaId() {
        return A_ID;
    }

    public  void setaId(int aId) {
        A_ID = aId;
    }

    public  String getPassword() {
        return Password;
    }

    public  void setPassword(String password) {
        Password = password;
    }

    public  String getPhone() {
        return Phone;
    }

    public  void setPhone(String phone) {
        Phone = phone;
    }

    public  String getUsername() {
        return User_name;
    }

    public  void setUsername(String user_name) {
        User_name = user_name;
    }



    public  String getScId() {
        return SC_ID;
    }

    public  void setScId(String scId) {
        SC_ID = scId;
    }

    public String getSC_name() {
        return SC_name;
    }

    public void setSC_name(String SC_name) {
        this.SC_name = SC_name;
    }

    public boolean isIsvalid() {
        return isvalid;
    }

    public void setIsvalid(boolean isvalid) {
        this.isvalid = isvalid;
    }


    public void commit(){

        meditor = msharedPreferences.edit();
        meditor.putString("SC_ID",SC_ID);
        meditor.putString("User_name",User_name);
        meditor.putString("Password",Password);
        meditor.putString("Phone",Phone);
        meditor.putInt("A_ID",A_ID);
        meditor.putString("SC_name",SC_name);
        meditor.putBoolean("isValid",isvalid);

    };

    public void applyUser()
    {
        SC_ID = msharedPreferences.getString("SC_ID",null);
        User_name = msharedPreferences.getString("User_name",null);
        Password = msharedPreferences.getString("Password",null);
        Phone = msharedPreferences.getString("Phone",null);
        A_ID = msharedPreferences.getInt("A_ID",-1);
        SC_name = msharedPreferences.getString("SC_name",null);
        isvalid = msharedPreferences.getBoolean("isValid",false);
    }

    public void clear()
    {
        meditor = msharedPreferences.edit();
        meditor.putString("SC_ID",null);
        meditor.putString("User_name",null);
        meditor.putString("Password",null);
        meditor.putString("Phone",null);
        meditor.putInt("A_ID",-1);
        meditor.putString("SC_name",null);
        meditor.putBoolean("isValid",false);
    };

}
