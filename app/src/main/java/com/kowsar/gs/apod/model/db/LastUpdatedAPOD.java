package com.kowsar.gs.apod.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LastUpdatedAPOD extends SQLiteOpenHelper {
    private static final String TAG = LastUpdatedAPOD.class.getSimpleName();
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "last_updated";

    // Table Names
    private static final String DB_TABLE = "table_last_updated";

    // column names
    public static final String KEY_TITLE = "apod_title";
    public static final String KEY_IMAGE = "image_data";
    public static final String KEY_DATE = "apod_date";
    public static final String KEY_DESCRIPTION = "apod_description";

    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+
            KEY_TITLE + " TEXT," +
            KEY_DATE + " TEXT," +
            KEY_DESCRIPTION + " TEXT," +
            KEY_IMAGE + " BLOB);";


    public LastUpdatedAPOD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating table
        db.execSQL(CREATE_TABLE_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

        // create new table
        onCreate(db);
    }

    public void insertItem( String title, String date, String description, byte[] image) throws SQLiteException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_TITLE,   title);
        cv.put(KEY_DATE, date  );
        cv.put(KEY_DESCRIPTION,   description);
        cv.put(KEY_IMAGE,   image);
        long rowId=database.replace( DB_TABLE, null, cv );
        Log.d(TAG, "insertItem: rowId="+rowId);
    }

    public Cursor fetchLastData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery="select * from "+DB_TABLE;
        return db.rawQuery(sqlQuery, null);
    }
}
