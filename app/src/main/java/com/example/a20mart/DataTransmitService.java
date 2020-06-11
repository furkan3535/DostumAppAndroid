package com.example.a20mart;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.hardware.Sensor;
import android.se.omapi.Session;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DataTransmitService extends JobService {
    private FirebaseAuth mAuth;
    private ArrayList<SensorData>tempData;
    public static FirebaseUser currentUser;
    public static FirebaseFirestore db;
    private SQLiteAccessHelper my_db;
    private static final String TAG="DataTransmitService";
    private String Sensor_Keys[]={SensorDataService.Step_Key,SensorDataService.Sound_Key}; //HARD CODING
    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        my_db=new SQLiteAccessHelper(this);
        tempData=new ArrayList<>();

    }

    @Override
    public boolean onStartJob(JobParameters params) {
        TransmitSensorDataToFireBase(params);
        return false;
    }

    private void TransmitSensorDataToFireBase(JobParameters params){
        Log.i(TAG, "TransmitSensorDataToFireBase: is started");

        Map<String,Object> Data;
        ArrayList<SensorData>tempDb=new ArrayList<>();
        for (int i = 0; i <Sensor_Keys.length; i++) {
            tempData.add(my_db.getDataList(this, Sensor_Keys[i]).get(0));

        }


        for(int i=0;i<Sensor_Keys.length;i++) {
            tempDb=my_db.getDataList(this, Sensor_Keys[i]);
            for (int j = 1; j <tempDb.size() ; j++) {
                Data = new HashMap<>();
                Data.put("UserId",currentUser.getUid());
                Data.put("Val",tempDb.get(j).SensorVal);
                Data.put("TimeLong",tempDb.get(j).Date);
                Data.put("Time",tempDb.get(j).DateDef);
                db.collection(Sensor_Keys[i]).add(Data);

            }

        }
        //SQLite CLEAR
        my_db.clear();
        my_db.insertDataSQL(tempData.get(0).Date,tempData.get(1).DateDef,tempData.get(1).SensorType,tempData.get(1).SensorVal);
        my_db.insertDataSQL(tempData.get(0).Date,tempData.get(0).DateDef,tempData.get(0).SensorType,tempData.get(0).SensorVal);
        tempData.clear();

        }



    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
