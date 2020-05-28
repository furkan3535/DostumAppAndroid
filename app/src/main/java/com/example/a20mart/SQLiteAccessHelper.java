package com.example.a20mart;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteAccessHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="UserInfo.db";
    private static final String TABLE_NAME="SoundRecords";
    private static final String USER_ID="USER_ID";
    private static final String Record_Time="Record_Time";
    private static final String Noise_Level="Noise_Level";
    private static final String TAG="SQL SERVER";

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: is called");
        String create_table_sql= "CREATE TABLE '" + TABLE_NAME +"' (USER_ID INT PRIMARY KEY,Record_Time TEXT, Noise_Level INT) ;";
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

    public void insertSoundRecords(int User_Id,String Rc_Time,int N_Level){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues val= new ContentValues();
        val.put(USER_ID,User_Id);
        val.put(Record_Time,Rc_Time);
        val.put(Noise_Level,N_Level);
       long isSuccess= db.insert(TABLE_NAME,null,val); //the row ID of the newly inserted row, or -1 if an error occurred
        if(isSuccess!=-1)Log.i(TAG, "InsertSoundRecord is successfully !");
    }
}
