package com.example.a20mart;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DostumCalendar {
    private Long TodayStartTimeLong;
    private Long TodayEndTimeLong;

    public DostumCalendar(){

        TodayEndTimeLong=getEndOfCurrentDay();

    }










    public boolean checkToday(Long TimeLong,int startInterval){
        TodayStartTimeLong=getStartOfDay(startInterval);
        // This function return false If The dates of the collected data are not on the same day as the requested date.
        // Else return true;
        //String dateString = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date(StartDate));
        // Log.d(TAG,     "\n The long magnitude value that comes from beginning of the day to now: -> "+dateString);

        if(TimeLong<=TodayEndTimeLong && TimeLong>=TodayStartTimeLong){
            return true;
        }
        else{
            Log.d("DostumCalendar","This Data is not recorded in Intervalled Time" +
                    "\n Its Recorded At : -> "+new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date(TimeLong)));
            return false;
        }


    }


    public boolean getDay(long dayTime,int dayInterval){
        //check if dayTime is between the given dayInterval.

        long pastStartDay=getStartOfDay(dayInterval);
        long pastDayEnd=getEndOfPastDay(dayInterval);
        if(dayTime>=pastStartDay && dayTime<=pastDayEnd){
            return true;
        }
        return false;

    }




    public  long getStartOfDay(int startInterval) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, (cal.get(Calendar.DAY_OF_MONTH) - startInterval));
        cal.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
        cal.set(Calendar.MINUTE, 0); // set minutes to zero
        cal.set(Calendar.SECOND, 0); //set seconds to zero
        return  cal.getTimeInMillis();
    }

    public long getEndOfPastDay(int Interval){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, (cal.get(Calendar.DAY_OF_MONTH) - Interval));
        cal.set(Calendar.HOUR_OF_DAY, 24); //set hours to zero
        cal.set(Calendar.MINUTE, 59); // set minutes to zero
        cal.set(Calendar.SECOND, 59); //set seconds to zero
        return  cal.getTimeInMillis();

    }

    public long getEndOfCurrentDay(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24); //set hours to zero
        cal.set(Calendar.MINUTE, 59); // set minutes to zero
        cal.set(Calendar.SECOND, 59); //set seconds to zero
        return  cal.getTimeInMillis();

    }

}
