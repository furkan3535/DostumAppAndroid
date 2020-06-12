package com.example.a20mart;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTransmitService extends JobService {
    private FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    private ArrayList<SensorData> tempData;
    public static FirebaseFirestore db;
    private SQLiteAccessHelper my_db;
    private static final String TAG="DataTransmitService";
    private String Sensor_Keys[]={SensorDataService.Step_Key,SensorDataService.Sound_Key}; //HARD CODING
    private UsageStatsManager usageStatsManager;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        my_db=new SQLiteAccessHelper(this);
        tempData=new ArrayList<>();
       usageStatsManager=(UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

    }





    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob: is started");
        TransmitSensorDataToFireBase(params);
        sendAppToFB();
        getCallDetails();
        return false;
    }

    public List<ApplicationDetail> getApplicationUsage(){
        ArrayList<ApplicationDetail> applicationDetailList = new ArrayList<>();
            boolean contains = false; // en tepeye aliriz sonra
            ApplicationDetail d1;
            Toast.makeText(getApplicationContext(), "Usage Permission Already Granted", Toast.LENGTH_SHORT).show();
            //show statistics
            final long currentTime = System.currentTimeMillis(); // Get current time in milliseconds

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);//Set year to beginning of desired period.
            final long beginTime = cal.getTimeInMillis();//Get begin time in milliseconds

            final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, currentTime);

            for (UsageStats u : queryUsageStats) {
                contains = false;
                if (getAppNameFromPackage(u.getPackageName(), this) != null) {
                    // if true this is an application that is downloaded from appstore etc.
                    d1 = new ApplicationDetail(u.getPackageName(), u.getTotalTimeInForeground());
                    if (applicationDetailList.size() < 1) {
                        applicationDetailList.add(d1);
                        contains = true;
                    } else {
                        for (int i = 0; i < applicationDetailList.size(); i++) {
                            if (applicationDetailList.get(i).getApplicationName().equals(d1.getApplicationName())) {
                                applicationDetailList.get(i).setApplicationUsageTime(d1.getApplicationUsageTime());
                                contains = true;
                                break;
                            }
                        }
                    }
                    if (!contains) {
                        applicationDetailList.add(d1);

                    }
                }

            }
            Collections.sort(applicationDetailList, new Comparator<ApplicationDetail>() {
                @Override
                public int compare(ApplicationDetail o1, ApplicationDetail o2) {
                    return (int) (o2.getHour() - o1.getHour());
                }
            });




        return  applicationDetailList;
    }



    private void sendAppToFB(){

        List<ApplicationDetail> appList = getApplicationUsage();
        Map<String, Object> user = new HashMap<>();
        user.put("UserId", currentUser.getUid());
        user.put("Date", Calendar.getInstance().getTime());
        user.put("AppList", appList);


        db.collection("ApplicationUsage")
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






    private String getAppNameFromPackage(String packageName, Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> pkgAppsList = context.getPackageManager()
                .queryIntentActivities(mainIntent, 0);


        for (ResolveInfo app : pkgAppsList) {
            if (app.activityInfo.packageName.equals(packageName)) {
                return app.activityInfo.loadLabel(context.getPackageManager()).toString();
            }
        }
        return null;
    }


    private void TransmitSensorDataToFireBase(JobParameters params){
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
        CallingInformation _callingInfo = new CallingInformation();
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

    public void sentToFirebase(CallingInformation info){
        Map<String, Object> user = new HashMap<>();
        user.put("UserId", currentUser.getUid());
        user.put("Date", Calendar.getInstance().getTime());
        user.put("Data",info);
        /*user.put("AverageCallTime", info.getAverageCallTime());
        user.put("MaximumCallTime", info.getMaximumCallTime());
        user.put("TotalCallCount", info.getTotalCallCount());
        user.put("TotalCalledPerson", info.getTotalCalledPerson());
        user.put("TotalDuration", info.getTotalDuration());
        user.put("Callers", info.getCallers());*/
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
