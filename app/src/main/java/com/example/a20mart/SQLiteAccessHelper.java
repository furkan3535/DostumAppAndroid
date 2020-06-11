package com.example.a20mart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class SQLiteAccessHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="UserInfo.db";
    private static final String TABLE_NAME="UserRecords";
    private static final String Record_Time="Record_Time";
    private static final String Record_Def="Record_Def";
    private static final String Def_Key="Def_Key";
    private static final String Record_Value="Record_Value";
    private static final String TAG="SQL SERVER";
    private SensorData S_data;
    private ArrayList<SensorData> all_data;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: is called");
        String create_table_sql= "CREATE TABLE '" + TABLE_NAME +"' (Record_Time  Long,Record_Def String,Def_Key TEXT NOT NULL, Record_Value INT) ;";
        db.execSQL(create_table_sql);
    }

    public SQLiteAccessHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: is called");
            //db.execSQL("drop table if exists TABLE_NAME");
    }

    public void insertDataSQL(Long Rc_Time,String record_Def,String Definition_Key,int Rec_Value){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues val= new ContentValues();
        val.put(Record_Time, Rc_Time);
        val.put(Record_Def,record_Def);
        val.put(Def_Key,Definition_Key);
        val.put(Record_Value,Rec_Value);
       long isSuccess= db.insert(TABLE_NAME,null,val); //the row ID of the newly inserted row, or -1 if an error occurred
        if(isSuccess!=-1)Log.i(TAG, "SQL insertion is successfully !");
    }

    public int getCount(){
        SQLiteDatabase db= this.getWritableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        Log.i(TAG, "getCount-->: " + count);
        return (int)count;
    }

    public int getLastData(Context context,String Key){
        SQLiteDatabase db= this.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from UserRecords",null);

        if(cursor.getCount()==0){
            Toast.makeText(context,"NO DATA",Toast.LENGTH_LONG).show();
            return 0;
        }
        else{
            for (cursor.moveToLast(); !cursor.isFirst(); cursor.moveToPrevious()) {
                if(cursor.getString(2).equals(Key)){
                    Log.d(TAG, "Cursor Last Data  Type: " + cursor.getString(2)+" Value : "+cursor.getString(3));
                    return cursor.getInt(3);
                }

            }
        }


        return 0;
    }

    public ArrayList<SensorData> getDataList(Context context, String Key){
        SQLiteDatabase db= this.getWritableDatabase();
        all_data=new ArrayList<>();
        Cursor cursor=db.rawQuery("select * from UserRecords",null);
        if(cursor.getCount()==0){
            Toast.makeText(context,"NO DATA",Toast.LENGTH_LONG).show();
            return null;
        }
        else{
            for (cursor.moveToLast(); !cursor.isFirst(); cursor.moveToPrevious()) {
                if(cursor.getString(2).equals(Key)){
                    S_data=new SensorData(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3));
                    all_data.add(S_data);
                }

            }
            return all_data;
        }

    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); //delete all rows in a table

    }
}
