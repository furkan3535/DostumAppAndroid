package com.example.a20mart;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.provider.CallLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CallDataFBService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        getCallDetails();
        return false;
    }



    private void getCallDetails() {
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


    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
