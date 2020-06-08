package com.example.a20mart;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

enum CallType{
    AverageCallTime,MaximumCallTime,TotalCallCount,TotalCalledPerson,TotalCallTime
}
public class CallingActivity extends Activity {
    public static final String TAG = "CallingActivity";
    private FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    public static FirebaseFirestore db;
    LineChart chart;
    Drawable drawable;
    Spinner spinner;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        setInitialElements();
        getFBData(CallType.AverageCallTime);
    }


    private void setInitialElements(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        chart =  findViewById(R.id.chart);
        spinner = findViewById(R.id.spinner);
        drawable = ContextCompat.getDrawable(this,R.drawable.line_gradient);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){

                    case 1:
                        getFBData(CallType.MaximumCallTime);
                        break;
                    case 2:
                        getFBData(CallType.TotalCallCount);
                        break;
                    case 3:
                        getFBData(CallType.TotalCalledPerson);
                        break;
                    case 4:
                        getFBData(CallType.TotalCallTime);
                        break;
                    case 0:
                    default:
                        getFBData(CallType.AverageCallTime);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getFBData(CallType.AverageCallTime);

            }


        });

    }




    public  void getFBData(final CallType callType){

        db.collection("CallData")
                .whereEqualTo("UserId", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Entry> entries = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                FBCallData dataToAdd = document.toObject(FBCallData.class);
                                switch (callType){

                                    case AverageCallTime:
                                        entries.add(new Entry(dataToAdd.getDate().getTime(),dataToAdd.getCallingInfo().getAverageCallTime()));
                                        break;
                                    case MaximumCallTime:
                                        entries.add(new Entry(dataToAdd.getDate().getTime(),dataToAdd.getCallingInfo().getMaximumCallTime()));
                                        break;
                                    case TotalCallCount:
                                        entries.add(new Entry(dataToAdd.getDate().getTime(),dataToAdd.getCallingInfo().getTotalCallCount()));
                                        break;
                                    case TotalCalledPerson:
                                        entries.add(new Entry(dataToAdd.getDate().getTime(),dataToAdd.getCallingInfo().getTotalCalledPerson()));
                                        break;
                                    case TotalCallTime:
                                        entries.add(new Entry(dataToAdd.getDate().getTime(),dataToAdd.getCallingInfo().getTotalDuration()));
                                        break;
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            chart.setScaleEnabled(true);
                            chart.setDragEnabled(true);
                            chart.setExtraOffsets(10,10,10,10);
                            Collections.sort(entries, new EntryXComparator());
                            LineDataSet dataSet = new LineDataSet(entries,"Average Call Time");
                            dataSet.setDrawValues(false);
                            LineData LineData = new LineData(dataSet);
                            chart.setData(LineData);
                            chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                                private SimpleDateFormat mFormat = new SimpleDateFormat("HH");

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
                            dataSet.setDrawFilled(true);
                            dataSet.setFillDrawable(drawable);
                            dataSet.setDrawCircles(false);

                            chart.getAxisRight().setDrawGridLines(false);
                            chart.getAxisLeft().setDrawGridLines(false);
                            chart.getXAxis().setDrawGridLines(false);
                            chart.invalidate();


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

}
