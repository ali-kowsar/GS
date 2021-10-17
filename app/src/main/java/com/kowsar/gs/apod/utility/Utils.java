package com.kowsar.gs.apod.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utils {
    private static SharedPreferences mPref;
    private static String PREF_NAME="apod_pref";

    public static  SharedPreferences getAPODPref(Context context){
        mPref= context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return mPref;
    }

    public static boolean isNetworkConnected(Context context){
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
}
