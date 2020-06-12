package com.example.a20mart;

import com.google.firebase.database.PropertyName;

import java.util.List;

public class FBApplicationUsage {
    @PropertyName("UserId")
    private String UserId;

    @PropertyName("Date")
    private java.util.Date Date;

    @PropertyName("AppList")
    private List<ApplicationDetail> AppList;

    public FBApplicationUsage(){}

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public List<ApplicationDetail> getAppList() {
        return AppList;
    }

    public void setAppList(List<ApplicationDetail> appList) {
        AppList = appList;
    }

    public FBApplicationUsage(String userId, java.util.Date date, List<ApplicationDetail> appList) {
        UserId = userId;
        Date = date;
        AppList = appList;
    }
}
