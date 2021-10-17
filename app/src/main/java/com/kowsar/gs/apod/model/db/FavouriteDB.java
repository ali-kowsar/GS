package com.kowsar.gs.apod.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class FavouriteDB extends SQLiteOpenHelper {
    private final String TAG = this.getClass().getSimpleName();
    private static int DB_VERSION = 777;
    private static String DB_NAME= "APOD_FAVOURITE_DB";
    private static  String APOD_TABLE_NAME = "APOD_FAVOURITE_TABLE";
    public static  String ITEM_ID="id";
    public static String THUMB_URL ="thumbnail_url";
    public static String TITLE= "title";
//    private static String ITEM_URL="url";
    private static String CREATE_TABLE = "CREATE TABLE "+APOD_TABLE_NAME+"("+ITEM_ID+" TEXT,"+THUMB_URL+" TEXT,"+
                                               TITLE+" TEXT)";

    public FavouriteDB(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertEmpty(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i=0;i<10;i++){
            cv.put(ITEM_ID,i);
            cv.put(TITLE, "Kowsar");
            cv.put(THUMB_URL, "https://apod.nasa.gov/apod/image/2110/Helix_Oxygen_crop2_1024.jpg");
//            cv.put(ITEM_URL,"https://api.nasa.gov/planetary/apod?api_key=Kd5K7JqQLWIraJ462ddK6jfm8vgCb6ach91Z6VQN&date=2021-10-14&thumbs=True");
            db.insert(APOD_TABLE_NAME, null,cv);
        }
    }
    public void insertFABToDB(String id,String title, String thumbUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ITEM_ID, id);
        cv.put(TITLE, title);
        cv.put(THUMB_URL, thumbUrl);
//        cv.put(ITEM_URL, itemURL);
        long rowid=db.insert(APOD_TABLE_NAME, null, cv);
        Log.d(TAG, "rowid="+rowid);
    }

    public Cursor fetchAllFabData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery="select * from "+APOD_TABLE_NAME;
        return db.rawQuery(sqlQuery, null);
    }
    public void removeFromFab(String id){
        Log.d(TAG, "removeFromFab(): id="+id);
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlQuery=" DELETE FROM  "+APOD_TABLE_NAME+" WHERE "+ITEM_ID+"=\""+id+"\";";
        Log.d(TAG, "Query string="+sqlQuery);
        db.execSQL(sqlQuery );
    }
}
