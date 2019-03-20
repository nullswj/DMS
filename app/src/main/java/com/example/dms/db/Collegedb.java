package com.example.dms.db;


import org.litepal.crud.LitePalSupport;

public class Collegedb extends LitePalSupport
{
    private String CGMT_ID;

    private String C_ID;

    private String C_name;

    private String G_ID;

    private String Grade;

    private String M_ID;

    private String Major;

    private String CL_ID;

    private String Classname;

    public String getCGMT_ID() {
        return CGMT_ID;
   }

    public String getC_ID() {
        return C_ID;
    }

    public String getC_name() {
        return C_name;
    }



    public String getG_ID() {
        return G_ID;
    }

    public String getGrade() {
        return Grade;
    }

    public String getCL_ID() {
        return CL_ID;
    }


    public String getM_ID() {
        return M_ID;
    }

    public String getClassname() {
        return Classname;
    }

    public String getMajor() {
        return Major;
    }

    public void setC_ID(String c_ID) {
        C_ID = c_ID;
    }

    public void setC_name(String c_name) {
        C_name = c_name;
    }

    public void setCGMT_ID(String CGMT_ID) {
        this.CGMT_ID = CGMT_ID;
    }

    public void setG_ID(String g_ID) {
        G_ID = g_ID;
    }

    public void setGrade(String grade) {
        Grade = grade;
    }

    public void setM_ID(String m_ID) {
        M_ID = m_ID;
    }

    public void setCL_ID(String CL_ID) {
        this.CL_ID = CL_ID;
    }



    public void setMajor(String major) {
        Major = major;
    }

    public void setClassname(String classname) {
        Classname = classname;
    }
}
