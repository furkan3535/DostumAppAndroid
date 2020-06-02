package com.example.a20mart;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.hardware.Sensor;
import android.se.omapi.Session;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DataTransmitService extends JobService {
    private FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    public static FirebaseFirestore db;
    private SQLiteAccessHelper my_db;
    private ArrayList<SensorData> dataArrayList;
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

    }

    @Override
    public boolean onStartJob(JobParameters params) {
        TransmitSensorDataToFireBase(params);
        return false;
    }

    private void TransmitSensorDataToFireBase(JobParameters params){
        Log.i(TAG, "TransmitSensorDataToFireBase: is started");
        Map<String,ArrayList<Map<String, ArrayList<SensorData>>>> FireBaseDataList=new HashMap();
        Map<String,ArrayList<SensorData>> DataList;
        ArrayList<Map<String, ArrayList<SensorData>>> SensorsList=new ArrayList<>();
        int list_len=0;
        for(int i=0;i<Sensor_Keys.length;i++) {
            DataList = new HashMap<>();
            DataList.put(Sensor_Keys[i], my_db.getDataList(this, Sensor_Keys[i]));
            SensorsList.add(DataList);


        }
        FireBaseDataList.put(Calendar.getInstance().getTime().toString(),SensorsList);
        db.collection("SensorData2").document("Ekrem")
                .set(FireBaseDataList,SetOptions.merge());



        }



    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
