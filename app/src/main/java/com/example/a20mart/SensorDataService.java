package com.example.a20mart;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class SensorDataService extends Service implements SensorEventListener,StepListener {
    private static final String TAG="Background Service";
    private static final String CHANNEL_ID = "exampleServiceChannel";
    public static final String Step_Key="Step_Record";
    public static final String Sound_Key="Sound_Record";
    private int RecordVal;
    private long numSteps;
    private Timer task;
    private SQLiteAccessHelper my_db;
    private MediaRecorder mRecorderSound;
    private SimpleStepDetector simpleStepDetector;
    private Notification notification;
    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private Intent notificationIntent;
    private PendingIntent pendingIntent;
    private NotificationManagerCompat notificationManagerCompat;
    private JobScheduler jobScheduler;
    private boolean jobFlag=false;


    @Override
    public void onCreate() { //it calls first time startService is run on main.
        super.onCreate();
        my_db=new SQLiteAccessHelper(this);
        task = new Timer();
        jobFlag=false;
        mRecorderSound = new MediaRecorder();
        simpleStepDetector=new SimpleStepDetector();
        simpleStepDetector.registerListener(this);
        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        notificationManagerCompat=NotificationManagerCompat.from(this);
        numSteps=my_db.getLastData(this,Step_Key); //this function returns 0 if db empty.





        notificationIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(""+(int)numSteps)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent).build();

        startForeground(1, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        numSteps=my_db.getLastData(this,Step_Key);
        startForeground(1, notification);

        task.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() { //bunu durdurmam gerekiyor.

                    try {
                        startMicSetup();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    RecordVal = 0;
                    for (int i = 0; i < 6; i++) {
                        if (i != 0) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            RecordVal += (int) getNoiseDb();

                        }
                        if (i == 0) {
                            getNoiseDb();
                        }
                    }
                    RecordVal = RecordVal / 5;

                    mRecorderSound.stop();
                    mRecorderSound.reset();

                    //Data insertion to SQLite DB
                    my_db.insertDataSQL( Calendar.getInstance().getTime().toString(),Sound_Key,RecordVal);
                    my_db.insertDataSQL(Calendar.getInstance().getTime().toString(),Step_Key,(int)numSteps);
                    if(my_db.getCount()>=2 && !jobFlag){
                        SendData();
                        jobFlag=true;
                    }

                    Log.d(TAG, "All Data is saved into SQL : " + RecordVal + "Step  "+(int)numSteps);
            }

        },0, 6*60*1000); // 10min








        return START_NOT_STICKY;
    }

    public void startMicSetup() throws IOException {
        Log.i(TAG, "startMicSetup is called.");
        mRecorderSound.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorderSound.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorderSound.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorderSound.setOutputFile("/dev/null");
        mRecorderSound.prepare();
        mRecorderSound.start();



    }

    public double getNoiseDb() {
        //Returns the Db level of maximum absolute amplitude that was sampled since the last call to this method.
        int _currentNoiseDB = (int) ((Math.log10(mRecorderSound.getMaxAmplitude())) * 20);// amplitude to db formula.
        return _currentNoiseDB;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() { //stopService call onDestroy.
        //If your service is destroyed and then run again, the onCreate will be called again.
        super.onDestroy();
        mSensorManager.unregisterListener(this,mSensorAccelerometer);
        task.cancel();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                simpleStepDetector.updateAccel(event.timestamp,event.values[0],event.values[1],event.values[2]);
                break;
            default:
                // do nothing
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void SendData(){

        jobScheduler=(JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName=new ComponentName(this,DataTransmitService.class);
        JobInfo jobInfo=new JobInfo.Builder(321,componentName)
                .setPersisted(true) //job will be written to disk and loaded at boot.
                .setPeriodic(15*60*1000) //Periodicity
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) //Requires Any Network To Run.
                .build();

        int resultCode=jobScheduler.schedule(jobInfo);
        if(resultCode == JobScheduler.RESULT_SUCCESS){
            Log.i(TAG, "Job Scheduled Successfully");
        }
        else{
            Log.i(TAG, "Job Scheduled not Successfully");
        }
    }



    @Override
    public void step(long timeNs) {
        Log.d(TAG, "step-->:  " +numSteps +"Time :" + Calendar.getInstance().getTime());
        numSteps++;
        notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Step"+numSteps)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent).build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManagerCompat.notify(1,notification);


    }
}
