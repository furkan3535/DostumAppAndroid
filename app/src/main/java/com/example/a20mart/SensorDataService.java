package com.example.a20mart;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
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

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class SensorDataService extends Service implements SensorEventListener,StepListener {

    private static final String TAG="Background Service";
    private MediaRecorder mRecorderSound;
    private static final String CHANNEL_ID = "exampleServiceChannel";
    private String notifData;
    private int RecordVal;
    private Timer task;
    private int dataBaseFlag=0;
    private SQLiteAccessHelper my_db;
    private SimpleStepDetector simpleStepDetector;
    private Notification notification;
    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private long numSteps;
    private Intent notificationIntent;
    private PendingIntent pendingIntent;
    private NotificationManagerCompat notificationManagerCompat;


    @Override
    public void onCreate() { //it calls first time startService is run on main.
        super.onCreate();
        my_db=new SQLiteAccessHelper(this);
        task = new Timer();
        mRecorderSound = new MediaRecorder();
        simpleStepDetector=new SimpleStepDetector();
        simpleStepDetector.registerListener(this);
        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        notificationManagerCompat=NotificationManagerCompat.from(this);

        // Log.i(TAG, "onStartCommand is called.  ");
        notifData = "" + 0;
        // Log.i(TAG, "notifData: " + notifData);

        notificationIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(notifData)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent).build();

        startForeground(1, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(1, notification);

        /*task.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() { //bunu durdurmam gerekiyor.
                if (dataBaseFlag < 1) {
                    try {
                        startMicSetup();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    RecordVal = 0;
                    int RecordVal2 = 0;
                    //Calendar.getInstance().getTime()
                    for (int i = 0; i < 6; i++) {
                        if (i != 0) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            RecordVal += (int) getNoiseDb();
                            // Log.d(TAG, "Sound Level " + RecordVal);

                        }
                        if (i == 0) {
                            getNoiseDb();
                        }
                    }
                    RecordVal = RecordVal / 5;
                    Log.d(TAG, "Final Average RecordVal after For Loop : " + RecordVal);
                    mRecorderSound.stop();
                    mRecorderSound.reset();
                    dataBaseFlag++;

                }
                else{
                    //my_db.insertSoundRecords(1, Calendar.getInstance().getTime().toString(),RecordVal);
                    //task.cancel();
                }
            }

        },0, 10*60*1000); // 10min*/








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
                //Log.d(TAG, "onSensorChanged--->: TYPE_ACCELEROMETER");
                Vector temp = new Vector(3);
                temp.add(0, event.values[0]);
                temp.add(1, event.values[1]);
                temp.add(2, event.values[2]);
                simpleStepDetector.updateAccel(event.timestamp,event.values[0],event.values[1],event.values[2]);
                break;
            default:
                // do nothing
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
