package com.example.a20mart;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppActivity extends Activity {
    PieChart chart;
    public static final String TAG = "CallingActivity";
    private FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    public static FirebaseFirestore db;
    List<PieEntry> pieEntryDataList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        setInitialElements();
        fetchData();
        drawPieChart();
    }
    private void setInitialElements() {
        chart = findViewById(R.id.chart);
        chart.setNoDataText("Loading...");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        pieEntryDataList=new ArrayList<>();
    }
    public void fetchData(){
        db.collection("ApplicationUsage")
                .whereEqualTo("UserId", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   for (DocumentSnapshot document : task.getResult()) {
                                                       FBApplicationUsage dataToAdd = document.toObject(FBApplicationUsage.class);
                                                       for (ApplicationDetail data :dataToAdd.getAppList()) {
                                                           String[] nameStr = data.getApplicationName().split("\\.");
                                                           String name = nameStr[nameStr.length-1];
                                                           name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                                           if(data.getApplicationUsageTime()/(1000*60)<=0) {
                                                               pieEntryDataList.add(new PieEntry(10,""));
                                                           }else{
                                                               pieEntryDataList.add(new PieEntry((data.getApplicationUsageTime()/(1000*60))+10, name));
                                                           }

                                                       }
                                                       break;
                                                   }
                                                   drawPieChart();
                                               }
                                           }
                                       }
                );
    }

    public void drawPieChart(){

        Collections.sort(pieEntryDataList, new EntryXComparator());
        PieDataSet pieDataSet=new PieDataSet(pieEntryDataList,"Application Usage");
        pieDataSet.setSliceSpace(5f);
        pieDataSet.setSelectionShift(1f);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setHighlightEnabled(true);
        pieDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(value<40){

                    return "";
                }
                return (int)(value-10)+"min.";
            }
        });
        PieData data= new PieData(pieDataSet);

        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        chart.setData(data);
        chart.setUsePercentValues(false);
        chart.setExtraOffsets(5,10,5,5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(61);
        chart.setCenterText("Application\nUsage" );
        chart.setCenterTextSize(15f);
        chart.setCenterTextColor(Color.argb(200,62,93,173));
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.spin(500,0,-360f, Easing.EasingOption.EaseInOutQuad);
        chart.invalidate();



    }
}