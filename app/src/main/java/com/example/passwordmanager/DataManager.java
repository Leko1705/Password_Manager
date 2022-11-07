package com.example.passwordmanager;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class DataManager {

    public static String getString(String place, Context context){
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return prefs.getString(place, "");
    }

    public static void setString(String place, String value, Context context){
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        prefs.edit().putString(place, value).apply();
    }

    public static Integer getInt(String place, Context context){
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return prefs.getInt(place, 0);
    }

    public static void setInt(String place, int value, Context context){
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        prefs.edit().putInt(place, value).apply();
    }
}


