package com.example.a20mart;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.Settings;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MediaRecorder mRecorder = null;
    static final int REQUEST_CODE = 123;
    Button usageBtn, getSoundBtn;
    static boolean granted, sound_granted;
    ListView appDataList;
    TextView soundLevelText;
    UsageStatsManager usageStatsManager;
    ArrayList<ApplicationDetail> applicationDetailList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usageBtn = findViewById(R.id.usageBtn);
        getSoundBtn = findViewById(R.id.soundCheckBtn);
        usageBtn.setOnClickListener(this);
        getSoundBtn.setOnClickListener(this);
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        appDataList = findViewById(R.id.appsList);
        soundLevelText = findViewById(R.id.sndTextView);
        sound_granted = false;

        //App Usage Permission Check
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }
        //App Usage Permission Check


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) +
                +ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            //when permissions not granted.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_CALL_LOG,

                    },
                    REQUEST_CODE
            );


        } else {

            //When permissions are already granted.
            sound_granted = true;
            try {
                startMicSetup();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "All Permissions Already Granted", Toast.LENGTH_SHORT).show();


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // all permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //
                } else {


                    //Create Alert Dialog.
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Grant this permission");
                    builder.setMessage("Need all permissions");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{
                                            Manifest.permission.RECORD_AUDIO,
                                            Manifest.permission.READ_CALL_LOG,

                                    },
                                    REQUEST_CODE
                            );


                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    Toast.makeText(getApplicationContext(), "All Permissions NOT Granted", Toast.LENGTH_SHORT).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


        }
    }


    @Override
    public void onClick(View v) {

        Button btn = (Button) v;

        if (btn == usageBtn) {
            getCallDetails();//test yapmak amaçlı konmuştur yeri değişecek ve bir buton a atanacak.
            if (granted) {
                boolean contains = false; // en tepeye aliriz sonra
                ApplicationDetail d1;
                Toast.makeText(getApplicationContext(), "Usage Permission Already Granted", Toast.LENGTH_SHORT).show();
                //show statistics
                final long currentTime = System.currentTimeMillis(); // Get current time in milliseconds

                final Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -1);//Set year to beginning of desired period.
                final long beginTime = cal.getTimeInMillis();//Get begin time in milliseconds

                final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, beginTime, currentTime);
                String data = "";
                ArrayList<String> appDataArray = new ArrayList<>();

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
                for (int i = 0; i < applicationDetailList.size(); i++) {
                    data = getAppNameFromPackage(applicationDetailList.get(i).getApplicationName(), this) + "\t" + "ForegroundTime: "
                            + applicationDetailList.get(i).getHour() + "hours";
                    appDataArray.add(data);
                }

                ArrayAdapter<String> appData = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, appDataArray);
                appDataList.setAdapter(appData);


            } else {
                Toast.makeText(getApplicationContext(),
                        "Please allow data usage to see related data.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

            }


        }


    }


    @Override

    protected void onResume() {
        super.onResume();
        soundMeterHandler.postDelayed(soundMeter, 5000);
    }


    public void startMicSetup() throws IOException {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            mRecorder.prepare();
            mRecorder.start();
        }
    }

   /* public void stopMic() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }*/

    Handler soundMeterHandler = new Handler();

    Runnable soundMeter = new Runnable() {
        @Override
        public void run() {
            soundLevelText.setText("Sound Level is : " + getAmplitude());
            soundMeterHandler.postDelayed(soundMeter, 500);
        }
    };


    public double getAmplitude() {
        if (mRecorder != null)
            return mRecorder.getMaxAmplitude();
        else
            return 0;

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


    private String getCallDetails() {
        StringBuffer stringBuffer = new StringBuffer();
        Calendar daily= Calendar.getInstance();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        daily.add(Calendar.DATE,-1); //from Yesterday,
        String fromDate=String.valueOf(daily.getTimeInMillis());
        daily.setTime(new Date());
        String toDate=String.valueOf(daily.getTimeInMillis()); //to Now.
        String[] whereValue = {fromDate,toDate};
        // whereValue return 24hours with millis. Query that below collect calling data according to whereValue time period.
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue,strOrder);



        return "";
    }
}