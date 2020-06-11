package com.example.a20mart;

import java.sql.Date;

public class SensorData {
    public String SensorType;
    public int SensorVal;
    public Long Date;
    public String DateDef;

    public SensorData(Long date,String Date_Def,String sensorType, int sensorVal) {
        SensorType = sensorType;
        SensorVal = sensorVal;
        Date=date;
        DateDef=Date_Def;
    }
}
