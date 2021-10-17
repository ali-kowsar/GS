package com.kowsar.gs.apod.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class APODSharedPref {
    private static SharedPreferences mPref;
    private static String PREF_NAME="apod_pref";
    static  APODSharedPref apodPref;
    private SharedPreferences.Editor editor;

    private   Context mContext;

    public static APODSharedPref getInstance(Context context){
        if (apodPref == null){
            apodPref = new APODSharedPref(context);
        }

        return apodPref;
    }

    private APODSharedPref(Context context){
        mPref= context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, boolean val){
        editor=mPref.edit();
        editor.putBoolean(key, val);
        editor.apply();
    }

    public boolean getBoolean(String key){
        return mPref.getBoolean(key, false);
    }

    public void putInt(String key, int val){
        editor=mPref.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    public int getInt(String key){
        return mPref.getInt(key, 0);
    }

    public void putString(String key, String val){
        editor=mPref.edit();
        editor.putString(key, val);
        editor.apply();
    }

    public String getString(String key){
        return mPref.getString(key, null);
    }
}
