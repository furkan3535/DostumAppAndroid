package com.example.a20mart;

import java.util.ArrayList;
import java.util.List;

public class Caller {
    private String callerNumber;
    private int totalDuration;
    private int totalCallCount;
    private int averageCallTime;
    private int maximumCallTime;
    private List<Call> calls;

    public Caller(String callerNumber) {
        this.callerNumber = callerNumber;
        this.totalDuration = 0;
        this.totalCallCount = 0;
        this.averageCallTime = 0;
        this.maximumCallTime = 0;
        this.calls = new ArrayList<Call>();
    }
    public Caller(){
        this.callerNumber = "";
        this.totalDuration = 0;
        this.totalCallCount = 0;
        this.averageCallTime = 0;
        this.maximumCallTime = 0;
        this.calls = new ArrayList<Call>();
    }

    public void addDuration(int duration,int callType){
        this.totalDuration += duration;
        this.totalCallCount++;
        this.averageCallTime = (this.totalDuration/this.totalCallCount);
        if(duration >maximumCallTime ) this.maximumCallTime = duration;
        calls.add(new Call(duration,callType));
    }


    public String getCallerNumber() {
        return callerNumber;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public int getTotalCallCount() {
        return totalCallCount;
    }

    public int getAverageCallTime() {
        return averageCallTime;
    }

    public int getMaximumCallTime() {
        return maximumCallTime;
    }

    public void setCallerNumber(String _CallerNumber){this.callerNumber = _CallerNumber;}

    public List<Call> getCalls() {
        return calls;
    }
}
