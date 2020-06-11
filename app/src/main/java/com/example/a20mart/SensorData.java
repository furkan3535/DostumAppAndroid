package com.example.a20mart;

public class SensorData {
    public String SensorType;
    public int SensorVal;
    public Long Date;
    public String DateDef;

    public SensorData(Long date,String dateDef,String sensorType, int sensorVal) {
        SensorType = sensorType;
        SensorVal = sensorVal;
        Date=date;
        DateDef = dateDef;
    }
}
