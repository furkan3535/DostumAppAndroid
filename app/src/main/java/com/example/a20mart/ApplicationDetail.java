package com.example.a20mart;

import com.google.firebase.database.PropertyName;

public class ApplicationDetail {

    @PropertyName("applicationName")
    private String applicationName;

    @PropertyName("applicationUsageTime")
    private long applicationUsageTime;

    @PropertyName("day")
    private long day;

    @PropertyName("hour")
    private long hour;

    @PropertyName("year")
    private long year;


    public void setApplicationUsageTime(long _applicationUsageTime) {
        applicationUsageTime += _applicationUsageTime;
    }

    public long getApplicationUsageTime() {
        return applicationUsageTime;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public ApplicationDetail() {
    }

    public ApplicationDetail(String applicationName, long applicationUsageTime) {
        this.applicationName = applicationName;
        this.applicationUsageTime = applicationUsageTime;
        this.day = 0;
        this.hour = 0;
        this.year = 0;
    }

    public String getApplicationName() {
        return applicationName;
    }


}