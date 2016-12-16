package com.vukoye.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class MoviesPreferences {

    public static final String SORT_ORDER_PREF = "SORT_ORDER";
    public static String LAST_TIME_TOP_RATED_PREF = "LAST_TIME_TOP_RATED";
    public static String LAST_TIME_POPULARITY_PREF = "LAST_TIME_POPULARITY";
    private static final String TAG = MoviesPreferences.class.getSimpleName();
    public static String SORT_ORDER_TOP_RATED = "SORT_ORDER_TOP_RATED";
    public static String SORT_ORDER_POPULAR = "SORT_ORDER_POPULAR";



    public static String API_KEY = "";

    public static String getSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(SORT_ORDER_PREF, SORT_ORDER_POPULAR);
    }

    public static void setSortOrder(String sortOrder, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Log.d(TAG, "setSortOrder: " + sortOrder);
        editor.putString(SORT_ORDER_PREF, sortOrder);
        editor.apply();
    }

    public static void setLastUpdate(Context context, long timeInMs, boolean isTopRated) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        if (isTopRated) {
            Log.d(TAG, "setLastUpdate for top rated: " + timeInMs);
            editor.putLong(LAST_TIME_TOP_RATED_PREF, timeInMs);
        } else {
            Log.d(TAG, "setLastUpdate for popularity: " + timeInMs);
            editor.putLong(LAST_TIME_POPULARITY_PREF, timeInMs);
        }
        editor.apply();
    }

    public static long getLastUpdate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (getSortOrder(context).equals(SORT_ORDER_TOP_RATED)) {
            return prefs.getLong(LAST_TIME_TOP_RATED_PREF, -1);
        } else {
            return prefs.getLong(LAST_TIME_POPULARITY_PREF, -1);
        }
    }
}
