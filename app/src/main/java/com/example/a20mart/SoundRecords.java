package com.example.a20mart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


import java.util.Date;


public class SoundRecords extends AppCompatActivity implements View.OnClickListener{
    LineChart lineChart;
    Button dailyBtn,weeklyBtn;
    FBSensorData Data;
    FirebaseAuth mAuth =FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    DostumCalendar dostumCalendar;
    private int flag=0;
    private static final String TAG="DataRepresentation";
    ArrayList<FBSensorData> FB_Data_List;
    ArrayList<FBSensorData> Daily_FB_Data_List;
    ArrayList<Entry>EntrydataSet;
    private Button zoomInBtn;
    private Button zoomOutBtn;
    private int mFillColor = Color.argb(150,44,180,229);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        //monitorBtn=findViewById(R.id.monitorData);
       // monitorBtn.setOnClickListener(this);
        lineChart=findViewById(R.id.LineChart);
        dostumCalendar=new DostumCalendar();
        dailyBtn=findViewById(R.id.DailyBtn);
        zoomInBtn=findViewById(R.id.zoomInBtn);
        zoomOutBtn=findViewById(R.id.zoomOutBtn);
        dailyBtn.setOnClickListener(this);
        weeklyBtn=findViewById(R.id.WeeklyBtn222);
        weeklyBtn.setOnClickListener(this);
        zoomInBtn.setOnClickListener(this);
        zoomOutBtn.setOnClickListener(this);














    }






    public void getSoundRecordData(){
        //Flag 1 = daily Flag 7 = weekly Flag=30 Hayırlısı
        //buraya flag atmak lazım sanırım. Daily olarak çekmek vs
         CollectionReference notebookRef=db.collection("Sound_Record");
        //Select * from table where equal to order by
        FB_Data_List=new ArrayList<>();


        notebookRef.whereEqualTo("UserId",user.getUid())
                .orderBy("TimeLong")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()){
                    task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots){


                                Data=documentSnapshot.toObject(FBSensorData.class);
                                FB_Data_List.add(Data);
                                Log.d(TAG,"Fetched Data From Firestore : "+"\nReturnedLog Magnitude  "+Data.TimeLong+"Date :" +Data.Time);

                            }
                            // collect daily data
                                Daily_FB_Data_List=new ArrayList<>();
                                for (FBSensorData data : FB_Data_List) {
                                    if(dostumCalendar.checkToday(data.TimeLong,flag)){ //flag represent day interval . if weekly return 7
                                        //Daily Data
                                        Daily_FB_Data_List.add(data);

                                    }
                                }
                                EntrydataSet=new ArrayList<>();
                                for (FBSensorData sensorEntry : Daily_FB_Data_List) {
                                    EntrydataSet.add(new Entry(sensorEntry.TimeLong.floatValue(),sensorEntry.Val));

                                }
                                drawLineChart();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: "+e.toString());
                        }
                    });

                }
                if(task.isCanceled()){
                    Log.d(TAG,"Task Cancelled.");
                }

            }
        });



    }






    public void drawLineChart(){



        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.fade_blue);
        LineDataSet lineDataSet=new LineDataSet(EntrydataSet,"Data Set 1");
        ArrayList<ILineDataSet>iLineDataSets=new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        LineData lineData=new LineData(iLineDataSets);







        LimitLine dangerLimit=new LimitLine(80,"DangerZone");
        dangerLimit.setLineWidth(2);
        dangerLimit.enableDashedLine(4,4,0);
        dangerLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        dangerLimit.setTextSize(7);

        LimitLine normalLimit=new LimitLine(55,"NormalLevel");
        normalLimit.setLineColor(Color.GREEN);
        normalLimit.setLineWidth(2);
        normalLimit.enableDashedLine(4,4,0);
        normalLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        normalLimit.setTextSize(7);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(dangerLimit);
        leftAxis.addLimitLine(normalLimit);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(35f);
        leftAxis.setDrawLimitLinesBehindData(true);

        //sağ taraftaki verileri siler.
        lineChart.getAxisRight().setEnabled(false);

        //Tepedeki değerleri firestoredan cektiğim saatlere çevirmeliyim.















        //Verilerin özelleşme fonksiyonları LineDataSet içerisindedir.
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCircleHoleRadius(3f);
        lineDataSet.setFillAlpha(65);
        lineDataSet.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
        lineDataSet.setLineWidth(3f);
        lineDataSet.enableDashedLine(30 ,10,0);
        lineDataSet.setValueTextSize(3f);
        lineDataSet.setDrawFilled(true);

        lineDataSet.setFillDrawable(drawable);



        lineChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return ""+(int)value+" db";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });


        if(flag==7){
            lineChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                private SimpleDateFormat mFormat = new SimpleDateFormat("EE,hh");


                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    long millis = (long) value;
                        return mFormat.format(new Date(millis));

                }

                @Override
                public int getDecimalDigits() {
                    return 0;
                }
            });

            lineChart.getXAxis().setTextSize(2f);
        }
        else {

            lineChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm a");

                @Override
                public String getFormattedValue(float value, AxisBase axis) {

                    long millis = (long) value;
                    return mFormat.format(new Date(millis));
                }

                @Override
                public int getDecimalDigits() {
                    return 0;
                }
            });
            lineChart.getXAxis().setTextSize(3f);
        }
        lineChart.setBackgroundColor(12632256);



        Legend l=lineChart.getLegend();
        l.setEnabled(false);




        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.setMaxHighlightDistance(200);
        lineChart.setScaleEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setData(lineData);
        lineChart.invalidate();


        lineChart.animateX(3000, Easing.EasingOption.Linear);







    }










    public void getDataFromFireBase(){
        DocumentReference documentReference= db.document("SensorData2/Ekrem");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Toast.makeText(SoundRecords.this, "Doc is exist", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SoundRecords.this, "Doc is not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SoundRecords.this, "ERRRRRROOOOOOOOORRRRRRRRRRRRRRR!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        Button btn=(Button)v;

        if(btn==dailyBtn){
            flag=1;
            getSoundRecordData();
        }
        if(btn==weeklyBtn){
            flag=7;
            getSoundRecordData();
        }
        if(btn==zoomInBtn){
            lineChart.zoomIn();
        }
        if(btn==zoomOutBtn){
            lineChart.zoomOut();
        }
    }

}
