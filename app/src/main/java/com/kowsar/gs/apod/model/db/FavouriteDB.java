package com.kowsar.gs.apod.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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

    // Table Names for last Item
    private static final String DB_TABLE = "table_last_updated";
    // column names for last Items
    public static final String KEY_TITLE = "apod_title";
    public static final String KEY_IMAGE = "image_data";
    public static final String KEY_DATE = "apod_date";
    public static final String KEY_URL = "thumb_url";
    public static final String KEY_DESCRIPTION = "apod_description";

    //table for Favorite items
    private static String CREATE_TABLE = "CREATE TABLE "+APOD_TABLE_NAME+"("+ITEM_ID+" TEXT,"+THUMB_URL+" TEXT,"+
                                               TITLE+" TEXT)";





    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+
            KEY_TITLE + " TEXT," +
            KEY_DATE + " TEXT," +
            KEY_URL + " TEXT," +
            KEY_DESCRIPTION + " TEXT," +
            KEY_IMAGE + " BLOB);";

    public FavouriteDB(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_IMAGE); // for last item to show when N/W not available

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + APOD_TABLE_NAME);
        onCreate(db);

    }

    public void insertFABToDB(String id,String title, String thumbUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ITEM_ID, id);
        cv.put(TITLE, title);
        cv.put(THUMB_URL, thumbUrl);
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


    public void insertItem(String title, String date, String thumbURL, String description, byte[] image) throws SQLiteException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, title);
        cv.put(KEY_DATE, date);
        cv.put(KEY_URL, thumbURL);
        cv.put(KEY_DESCRIPTION, description);
        cv.put(KEY_IMAGE, image);
        long rowId = database.replace(DB_TABLE, null, cv);
        Log.d(TAG, "insertItem: rowId=" + rowId);
    }

    public Cursor fetchLastData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery="select * from "+DB_TABLE;
        return db.rawQuery(sqlQuery, null);
    }
}
