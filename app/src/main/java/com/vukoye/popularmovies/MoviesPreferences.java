package com.vukoye.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by nemanja on 12/5/16.
 */

class MoviesPreferences {

    public static final String SORT_ORDER_PREF = "SORT_ORDER";
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
}
