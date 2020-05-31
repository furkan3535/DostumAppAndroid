package com.example.a20mart;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.hardware.Sensor;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DataTransmitService extends JobService {
    private FirebaseAuth mAuth;
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

    }

    @Override
    public boolean onStartJob(JobParameters params) {
        TransmitSensorDataToFireBase(params);
        return false;
    }

    private void TransmitSensorDataToFireBase(JobParameters params){
        Log.i(TAG, "TransmitSensorDataToFireBase: is started");
        Map<String, Object> SensorData;
        for(int i=0;i<Sensor_Keys.length;i++) {
            SensorData = new HashMap<>();
            SensorData.put("UserId", currentUser.getUid());
            SensorData.put("Date", Calendar.getInstance().getTime());
            SensorData.put("Record_Def", Sensor_Keys[i]);
            SensorData.put("Record_Val",my_db.getLastData(this,Sensor_Keys[i]) );

            Log.i(TAG, "SensorData Type: "+SensorData.get("Record_Def")+" Value :"+SensorData.get("Record_Val"));
            db.collection("SensorData")
                    .add(SensorData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
