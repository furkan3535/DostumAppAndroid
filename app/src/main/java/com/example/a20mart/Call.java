package com.example.a20mart;

public class Call {
    private int callTime;
    private int callType;

    public Call(){
        this.callTime = 0;
        this.callType = 0;
    }
    public Call(int callTime, int callType) {
        this.callTime = callTime;
        this.callType = callType;
    }

    public int getCallTime() {
        return callTime;
    }

    public int getCallType() {
        return callType;
    }
}
