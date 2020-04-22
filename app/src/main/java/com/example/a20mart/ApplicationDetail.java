package com.example.a20mart;

public class ApplicationDetail {
    private String ApplicationName;
    private long ApplicationUsageTime;

    public String getApplicationName() {
        return ApplicationName;
    }

    public void setApplicationUsageTime(long applicationUsageTime) {
        ApplicationUsageTime += applicationUsageTime;
    }

    public long getApplicationUsageTime() {
        return ApplicationUsageTime;
    }

    public ApplicationDetail(String appName, long timeMs){
        this.ApplicationName=appName;
        this.ApplicationUsageTime=timeMs;

    }


    public float getHour(){
        return this.ApplicationUsageTime/3600000;
    }

    public float getDay(){
        return this.ApplicationUsageTime/(3600000*24);
    }
    public float getYear(){
        return this.ApplicationUsageTime/(3600000*24*365);
    }


}
