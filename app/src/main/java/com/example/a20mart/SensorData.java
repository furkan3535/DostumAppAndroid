package com.example.a20mart;

import java.sql.Date;

public class SensorData {
    public String SensorType;
    public int SensorVal;
    public String Date;

    public SensorData(String date,String sensorType, int sensorVal) {
        SensorType = sensorType;
        SensorVal = sensorVal;
        Date=date;
    }
}
