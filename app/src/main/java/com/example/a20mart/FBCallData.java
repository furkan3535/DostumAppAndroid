package com.example.a20mart;

import com.google.firebase.database.PropertyName;

import java.util.Date;


public class FBCallData {

    @PropertyName("UserId")
    private String UserId;

    @PropertyName("Date")
    private java.util.Date Date;

    @PropertyName("CallingInfo")
    private CallingInformation CallingInfo;

    public FBCallData(){}

    public FBCallData(String userId, java.sql.Date date, CallingInformation callingInfo) {
        UserId = userId;
        Date = date;
        CallingInfo = callingInfo;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date date) {
        Date = date;
    }

    public CallingInformation getCallingInfo() {
        return CallingInfo;
    }

    public void setCallingInfo(CallingInformation callingInfo) {
        CallingInfo = callingInfo;
    }
}
