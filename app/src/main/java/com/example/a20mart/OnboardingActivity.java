package com.example.a20mart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class OnboardingActivity extends Activity {
    private LinearLayout stepLL,soundLL,callLL,appLL;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        stepLL = findViewById(R.id.stepLL);
        soundLL = findViewById(R.id.soundLL);
        callLL = findViewById(R.id.callLL);
        appLL = findViewById(R.id.appLL);
        StartService();

        stepLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentTClass(Pedometer.class);

            }
        });
        soundLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentTClass(SoundRecords.class);

            }
        });
        callLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentTClass(CallingActivity.class);

            }
        });
        appLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentTClass(AppActivity.class);

            }
        });



    }


    private void intentTClass(Class<?> cls){
        startActivity(new Intent(this,cls));
    }






    public void StartService() {
        Intent serviceIntent = new Intent(this, SensorDataService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

    }

    /*public void StopService(View view) {
        Intent serviceIntent=new Intent(this,SensorDataService.class);
        stopService(serviceIntent);
        finish();

    }*/
}
