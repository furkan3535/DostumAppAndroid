package com.example.a20mart;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    private static final String CHANNEL_ID="exampleServiceChannel";
   // private static final String CALLDATA_CHANNEL_ID="firebaseCalldataServiceChannel";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }



    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
         /*   NotificationChannel serviceChannel2 = new NotificationChannel(
                    CALLDATA_CHANNEL_ID,
                    "firebaseCalldataServiceChannel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager2 = getSystemService(NotificationManager.class);
            manager2.createNotificationChannel(serviceChannel2);*/
        }
    }
}
