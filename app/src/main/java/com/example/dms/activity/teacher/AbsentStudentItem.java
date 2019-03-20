package com.example.dms.activity.teacher;

import android.graphics.Bitmap;

public class AbsentStudentItem
{
    private String m_name;
    private Bitmap m_image;
    private String m_number;
    private String m_classname;

    public AbsentStudentItem(String name, Bitmap imageId, String Number, String classname)
    {
        this.m_name = name;
        this.m_image = imageId;
        this.m_number = Number;
        this.m_classname = classname;
    }

    public String getName()
    {
        return m_name;
    }
    public Bitmap getImageId()
    {
        return m_image;
    }
    public String getNumber()
    {
        return m_number;
    }
    public String getClassname()
    {
        return m_classname;
    }
    public void setImage(Bitmap imageId)
    {
        this.m_image = imageId;
    }

}
