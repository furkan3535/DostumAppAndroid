package com.example.a20mart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Pedometer extends AppCompatActivity implements View.OnClickListener {
    PieChart pieChart;
    BarChart barChart;
    int dayFlag;
    Button sevenBtn, thirtyBtn;
    FirebaseAuth mAuth =FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    DostumCalendar dostumCalendar;
    FBSensorData Data;
    ArrayList<FBSensorData> FB_Data_List;
    ArrayList<FBSensorData> Daily_FB_Data_List;
    ArrayList<BarEntry> barEntries;
    ArrayList<PieEntry> pieEntryDataList=new ArrayList<>();
    SimpleDateFormat dayFormat =new SimpleDateFormat("d");
    SimpleDateFormat dayDefFormat =new SimpleDateFormat("EEE");
    private boolean dataFlag=false;
    long DAY_IN_MS = 1000 * 60 * 60 * 24;


    int size=0;
    int labelcount=0;
    private static final String TAG="Pedometer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        pieChart=findViewById(R.id.PieChart);
        barChart=findViewById(R.id.BarChart);
        dostumCalendar=new DostumCalendar();
        sevenBtn=findViewById(R.id.sevenBtn);
        sevenBtn.setOnClickListener(this);
        thirtyBtn=findViewById(R.id.thirtyBtn);
        thirtyBtn.setOnClickListener(this);












    }








    public void getStepRecords(){
        CollectionReference notebookRef=db.collection("Step_Record");
         barEntries=new ArrayList<>();
        FB_Data_List=new ArrayList<>();
        Daily_FB_Data_List=new ArrayList<>();


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
                            size=0;
                            int Tempsize=0;

                            // collect maximum step count data for each day.
                            Daily_FB_Data_List=new ArrayList<>();
                            for (int i = dayFlag; i >=1; i--) { //dayflag changes according to buttons..
                                dataFlag=false;
                                for (FBSensorData data: FB_Data_List) {
                                    if(dostumCalendar.getDay(data.TimeLong,i)){
                                        //ilk for da 7 gün önceki data mı diye bakiyor
                                        // ardından azala azala gün karşılaşması yapıyor
                                        // data var ise önceki günlerde buranın içerisine giriyor
                                        Daily_FB_Data_List.add(data);
                                        Tempsize++;
                                        dataFlag=true;


                                }
                            }
                                if(dayFlag==7 && size==Tempsize){
                                    Date pieDate =new Date(System.currentTimeMillis()-(i*DAY_IN_MS));
                                    String pieEntryLabel= dayDefFormat.format(pieDate.getTime()); //Long to EEE,d format
                                    pieEntryDataList.add(new PieEntry(75,pieEntryLabel));

                                }
                                if(size!=Tempsize){
                                    size=Tempsize;
                                    labelcount++;

                                    int dayNumber=Integer.parseInt(dayFormat.format(new Date(Daily_FB_Data_List.get(Daily_FB_Data_List.size()-1).TimeLong)));
                                    barEntries.add(new BarEntry(dayNumber,Daily_FB_Data_List.get(Daily_FB_Data_List.size()-1).Val));


                                    if(dayFlag==7&& dataFlag){
                                        String pieEntryLabel= dayDefFormat.format(Daily_FB_Data_List.get(Daily_FB_Data_List.size()-1).TimeLong);
                                        pieEntryDataList.add(new PieEntry(Daily_FB_Data_List.get(Daily_FB_Data_List.size()-1).Val+75,pieEntryLabel));

                                    }


                                }


                            }
                            barEntries.add(new BarEntry(2,Daily_FB_Data_List.get(Daily_FB_Data_List.size()-1).Val));
                            barEntries.add(new BarEntry(30,Daily_FB_Data_List.get(Daily_FB_Data_List.size()-1).Val));
                            drawBarChart();
                            drawPieChart();

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
















    public void drawBarChart(){

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(30);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);



        BarDataSet barDataSet=new BarDataSet(barEntries,"Days");
        barDataSet.setValueTextSize(6f);
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        BarData data=new BarData(barDataSet);
        data.setBarWidth(0.15f);
        barChart.setData(data);
        barChart.setVisibleXRangeMaximum(7);
        barChart.getXAxis().setLabelCount(barEntries.size());
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.setDrawBorders(false);

        barChart.animateY(1000, Easing.EasingOption.EaseInBounce);
       barChart.invalidate();

    }




    public void drawPieChart(){





        PieDataSet pieDataSet=new PieDataSet(pieEntryDataList,"Haftalık Aktivite Göstergesi");
        pieDataSet.setSliceSpace(5f);
        pieDataSet.setSelectionShift(1f);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setHighlightEnabled(true);
        pieDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(value==75){
                    return "0";
                }
                return ""+(int)(value-75);
            }
        });
        PieData data= new PieData(pieDataSet);

        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);
        pieChart.setData(data);

        pieChart.setUsePercentValues(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(61);
        pieChart.setCenterText("7 Günlük\nAktivite Grafiği" );
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterTextColor(Color.argb(164,62,93,173));

        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.spin(500,0,-360f, Easing.EasingOption.EaseInOutQuad);
        pieChart.invalidate();



    }

    @Override
    public void onClick(View v) {
        Button btn=(Button)v;

        if(btn==sevenBtn){
            dayFlag=7;
            getStepRecords();
        }
        if(btn==thirtyBtn){
            dayFlag=30;
            getStepRecords();
        }
    }
}
