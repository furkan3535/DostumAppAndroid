package com.example.a20mart;

import java.util.ArrayList;
import java.util.List;

public class CallingInfo {

    private int totalDuration;
    private int totalCallCount;
    private int totalCalledPerson;
    private int averageCallTime;
    private int maximumCallTime;
    private List<Caller> callers;

    CallingInfo(){
        totalDuration = 0;
        totalCallCount = 0;
        totalCalledPerson = 0;
        averageCallTime = 0;
        maximumCallTime = 0;
        callers  = new ArrayList<Caller>();
    }


    public void addCall(String callerNumber, int duration, int callType){
        boolean isAdded = false;
        totalDuration += duration;
        totalCallCount++;
        if (maximumCallTime < duration) maximumCallTime = duration;
        averageCallTime = totalDuration / totalCallCount;
        for (int i = 0; i < callers.size() && !isAdded; i++) {
            if(callers.get(i).getCallerNumber().equals(callerNumber)){
                callers.get(i).addDuration(duration,callType);
                isAdded = true;
                break;
            }
        }
        if (!isAdded){
            Caller caller = new Caller(callerNumber);
            caller.addDuration(duration,callType);
            callers.add(caller);
            totalCalledPerson++;
        }
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public int getTotalCallCount() {
        return totalCallCount;
    }

    public int getTotalCalledPerson() {
        return totalCalledPerson;
    }

    public int getAverageCallTime() {
        return averageCallTime;
    }

    public int getMaximumCallTime() {
        return maximumCallTime;
    }

    public List<Caller> getCallers() {
        return callers;
    }
}
