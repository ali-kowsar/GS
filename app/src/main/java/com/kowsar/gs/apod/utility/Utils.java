package com.kowsar.gs.apod.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    private static SharedPreferences mPref;
    private static String PREF_NAME="apod_pref";

    public static  SharedPreferences getAPODPref(Context context){
        mPref= context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return mPref;
    }
}
