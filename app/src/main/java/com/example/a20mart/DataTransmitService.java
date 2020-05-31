package com.example.a20mart;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        Log.i(TAG, "onStartJob: is started");
        TransmitSensorDataToFireBase(params);
        getCallDetails();
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



    private void getCallDetails() {
        Log.i("FirebaseLog","getCallDetails Start");

        int NumOfPerson=0;
        int Duration=0;
        StringBuffer stringBuffer = new StringBuffer();
        Calendar daily = Calendar.getInstance();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        daily.add(Calendar.DATE, -1); //from Yesterday,
        String fromDate = String.valueOf(daily.getTimeInMillis());
        daily.setTime(new Date());
        String toDate = String.valueOf(daily.getTimeInMillis()); //to Now.
        String[] whereValue = {fromDate, toDate};
        // whereValue return 24hours with millis. Query that below collect calling data according to whereValue time period.
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue, strOrder);

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        CallingInfo _callingInfo = new CallingInfo();
        stringBuffer.append("CALL LOG\n\n");
        NumOfPerson=managedCursor.getCount();
        while (managedCursor.moveToNext()) {
            String phoneNumber = managedCursor.getString(number);
            int callType = managedCursor.getInt(type);
            String callDate = managedCursor.getString(date);
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "dd-MMM-yyyy HH:mm");
            String dateString = formatter.format(new Date(Long
                    .parseLong(callDate)));
            //  Date callDayTime = new Date(Long.valueOf(callDate));
            int callDuration = managedCursor.getInt(duration);
            Duration+= managedCursor.getInt(duration);


            _callingInfo.addCall(phoneNumber,callDuration,callType);



        }
        stringBuffer.append("\nTotal Call Duration: "+_callingInfo.getTotalDuration()+
                "sn.\nTotal Call Count: "+_callingInfo.getTotalCallCount()+
                "\nTotal Called Person: "+_callingInfo.getTotalCalledPerson()+
                "\nAverage Call Time: "+_callingInfo.getAverageCallTime()+
                "sn.\nMaximum Call Time: "+_callingInfo.getMaximumCallTime()+"sn.\n");
        for (Caller caller:_callingInfo.getCallers()) {
            stringBuffer.append("-----------------\nCaller "+(_callingInfo.getCallers().indexOf(caller)+1 )+"\nTotal Call Duration: "+caller.getTotalDuration()+
                    "sn.\nTotal Call Count: "+caller.getTotalCallCount()+
                    "\nAverage Call Time: "+caller.getAverageCallTime()+
                    "sn.\nMaximum Call Time: "+caller.getMaximumCallTime()+"sn.\n\n");
            for (Call call:caller.getCalls()) {
                String dir = null;
                switch (call.getCallType()) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "Outgoing";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "Incoming";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        dir = "Missed Call";
                        break;
                    case CallLog.Calls.VOICEMAIL_TYPE:
                        dir = "Voicemail";
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        dir = "Rejected";
                        break;
                    case CallLog.Calls.BLOCKED_TYPE:
                        dir = "Blocked";
                        break;



                }

                stringBuffer.append("Call Duration: "+call.getCallTime()+"sn\n"
                        +"Call Type: "+dir+"\n");
            }

        }
        stringBuffer.append("\n\n");

        sentToFirebase(_callingInfo);


    }

    public void sentToFirebase(CallingInfo info){
        Map<String, Object> user = new HashMap<>();
        user.put("UserId", currentUser.getUid());
        user.put("Date", Calendar.getInstance().getTime());
        user.put("AverageCallTime", info.getAverageCallTime());
        user.put("MaximumCallTime", info.getMaximumCallTime());
        user.put("TotalCallCount", info.getTotalCallCount());
        user.put("TotalCalledPerson", info.getTotalCalledPerson());
        user.put("TotalDuration", info.getTotalDuration());
        user.put("Callers", info.getCallers());
        Log.i("FirebaseLog","sentToFirebase Start");

        db.collection("CallData")
                .add(user)
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

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
