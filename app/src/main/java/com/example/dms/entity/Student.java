package com.example.dms.entity;

import android.content.Context;

import androidx.annotation.NonNull;

public class Student extends User
{
    private  String S_ID = null;

    private  String S_No = null;

    private  String S_name = null;

    private  String CGMT_ID = null;

    private  String P_Sys = null;

    private  String Lon_Sys = null;

    private  String lat_Sys = null;

    private String C_ID = null;
    private String C_name = null;

    private String M_ID = null;

    private String Major =null;

    private  String G_ID = null;

    private String Grade = null;

    private String ClassName = null;

    private String Cl_no = null;

    public Student(Context context) {
        super(context);
    }

    public  String getCgmtId() {
        return CGMT_ID;
    }

    public  void setCgmtId(String cgmtId) {
        CGMT_ID = cgmtId;
    }

    public  String getLat_Sys() {
        return lat_Sys;
    }

    public  void setLat_Sys(String lat_Sys) {
         lat_Sys = lat_Sys;
    }

    public  String getLon_Sys() {
        return Lon_Sys;
    }

    public  void setLon_Sys(String lon_Sys) {
        Lon_Sys = lon_Sys;
    }

    public  String getP_Sys() {
        return P_Sys;
    }

    public  void setP_Sys(String p_Sys) {
        P_Sys = p_Sys;
    }

    public  String getS_name() {
        return S_name;
    }

    public  void setS_name(String s_name) {
        S_name = s_name;
    }

    public  String getS_No() {
        return S_No;
    }

    public  void setS_No(String s_No) {
        S_No = s_No;
    }

    public  String getsId() {
        return S_ID;
    }

    public  void setsId(String sId) {
        S_ID = sId;
    }

    public String getMID() {
        return M_ID;
    }

    public void setMID(String m_ID) {
        M_ID = m_ID;
    }

    public String getMajor() {
        return Major;
    }

    public void setMajor(String major) {
        Major = major;
    }

    public String getGID() {
        return G_ID;
    }

    public void setGID(String g_ID) {
        G_ID = g_ID;
    }

    public String getGrade() {
        return Grade;
    }

    public void setGrade(String grade) {
        Grade = grade;
    }


    public String getClassName() {
        return ClassName;
    }

    public void setClass(String aClass) {
        ClassName = aClass;
    }

    public String getCl_no() {
        return Cl_no;
    }

    public void setCl_no(String cl_no) {
        Cl_no = cl_no;
    }

    public String getC_name() {
        return C_name;
    }

    public String getC_ID() {
        return C_ID;
    }

    public void setC_ID(String c_ID) {
        C_ID = c_ID;
    }

    public void setC_name(String c_name) {
        C_name = c_name;
    }

    @Override
    public void clear() {
        super.clear();
        meditor.putString("S_ID",null);
        meditor.putString("S_No",null);
        meditor.putString("S_name",null);
        meditor.putString("C_ID",null);
        meditor.putString("C_name",null);
        meditor.putString("CGMT_ID",null);
        meditor.putString("P_Sys",null);
        meditor.putString("Lon_Sys",null);
        meditor.putString("lat_Sys",null);
        meditor.putString("M_ID",null);
        meditor.putString("Major",null);
        meditor.putString("G_ID",null);
        meditor.putString("Grade",null);
        meditor.putString("ClassName",null);
        meditor.putString("Cl_no",null);
        meditor.apply();
    }

    @Override
    public void commit() {
        super.commit();
        meditor.putString("S_ID",S_ID);
        meditor.putString("S_No",S_No);
        meditor.putString("S_name",S_name);
        meditor.putString("CGMT_ID",CGMT_ID);
        meditor.putString("C_ID",C_ID);
        meditor.putString("C_name",C_name);
        meditor.putString("P_Sys",P_Sys);
        meditor.putString("Lon_Sys",Lon_Sys);
        meditor.putString("lat_Sys",lat_Sys);
        meditor.putString("M_ID",M_ID);
        meditor.putString("Major",Major);
        meditor.putString("G_ID",G_ID);
        meditor.putString("Grade",Grade);
        meditor.putString("ClassName",ClassName);
        meditor.putString("Cl_no",Cl_no);
        meditor.apply();
    }

    @Override
    public void applyUser() {
        super.applyUser();
        S_ID =msharedPreferences.getString("S_ID",null);
        S_No = msharedPreferences.getString("S_No",null);
        S_name = msharedPreferences.getString("S_name",null);
        CGMT_ID = msharedPreferences.getString("CGMT_ID",null);
        C_ID = msharedPreferences.getString("C_ID",null);
        C_name = msharedPreferences.getString("C_name",null);
        P_Sys =msharedPreferences.getString("P_Sys",null);
        Lon_Sys = msharedPreferences.getString("Lon_Sys",null);
        lat_Sys = msharedPreferences.getString("lat_Sys",null);
        M_ID = msharedPreferences.getString("M_ID",null);
        Major =msharedPreferences.getString("Major",null);
        G_ID = msharedPreferences.getString("G_ID",null);
        Grade= msharedPreferences.getString("Grade",null);
        ClassName = msharedPreferences.getString("ClassName",null);
        Cl_no = msharedPreferences.getString("CL_no",Cl_no);
    }
}
