package com.example.dms.db;

import org.litepal.crud.LitePalSupport;

public class Universitydb extends LitePalSupport
{
    private String universityID;
    private String universityName;

    public String getUniversityID() {
        return universityID;
    }

    public void setUniversityID(String universityID) {
        this.universityID = universityID;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }
}
