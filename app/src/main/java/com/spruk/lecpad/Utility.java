package com.spruk.lecpad;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by taray on 6/8/2015.
 */
public class Utility {
    public static String FetchInfo(Context context, String action,String params)
    {
        String url = "";
        return null;
    }

    public static void savePreferences(Context context,String key, String value) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();

    }
    public static String loadSavedPreferences(Context context,String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String name = sharedPreferences.getString(key, "studus");

        return name;
    }

}
