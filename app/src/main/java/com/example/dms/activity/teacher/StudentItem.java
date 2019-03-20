package com.example.dms.activity.teacher;

public class StudentItem
{
    private String m_name;

    private String m_number;

    private String m_classname;

    private String m_college;

    private int m_image;

    public StudentItem(String name, String no, String college, String classname,int image)
    {
        this.m_classname = classname;
        this.m_college = college;
        this.m_name = name;
        this.m_number = no;
        m_image = image;
    }

    public String getM_classname() {
        return m_classname;
    }

    public void setM_classname(String m_classname) {
        this.m_classname = m_classname;
    }

    public String getM_college() {
        return m_college;
    }

    public void setM_college(String m_college) {
        this.m_college = m_college;
    }

    public String getM_name() {
        return m_name;
    }

    public void setM_name(String m_name) {
        this.m_name = m_name;
    }

    public String getM_number() {
        return m_number;
    }

    public void setM_number(String m_number) {
        this.m_number = m_number;
    }

    public int getM_image() {
        return m_image;
    }

    public void setM_image(int m_image) {
        this.m_image = m_image;
    }
}
