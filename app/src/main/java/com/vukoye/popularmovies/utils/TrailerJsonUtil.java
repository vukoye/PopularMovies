package com.vukoye.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;

import com.vukoye.popularmovies.MoviesPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by nemanja on 12/12/16.
 */

public class TrailerJsonUtil {
    //{"id":"58257f4dc3a36836060060c4","iso_639_1":"en","iso_3166_1":"US","key":"qvCogW-N-HE","name":"HD Trailer","site":"YouTube","size":720,
    //        "type":"Trailer"},
    static final String RESULTS = "results";
    public static ContentValues[] getContentValuesFromJson(Context context, String jsonStr, final boolean isTopRated) throws JSONException {

        JSONObject moviesJson = new JSONObject(jsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);
        ContentValues[] contentValuesArray = new ContentValues[moviesArray.length()];
        JSONObject jsonTrailer;
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        if (moviesArray.length() > 0) {
            MoviesPreferences.setLastUpdate(context, currentTime, isTopRated);
        }
        ContentValues cv;
        // for (int i = 0; i < moviesArray.length(); i++) {
        //     jsonTrailer = moviesArray.getJSONObject(i);
        //     cv = new ContentValues();
        //     cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, jsonTrailer.getInt(ID));
        //     cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, (jsonTrailer.getString(POSTER_PATH)).substring(1));
        //     cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, jsonTrailer.getString(OVERVIEW));
        //     cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, jsonTrailer.getString(RELEASE_DATE));
        //     cv.put(MovieContract.MovieEntry.COLUMN_TITLE, jsonTrailer.getString(TITLE));
        //     cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, jsonTrailer.getString(VOTE_AVERAGE));
        //     if (isTopRated) {
        //         cv.put(MovieContract.MovieEntry.COLUMN_LAST_UPDATED_TOP_RATED, currentTime);
        //         cv.put(MovieContract.MovieEntry.COLUMN_TOP_RATED_C, i);
        //     } else {
        //         cv.put(MovieContract.MovieEntry.COLUMN_LAST_UPDATED_POPULAR, currentTime);
        //         cv.put(MovieContract.MovieEntry.COLUMN_POPULAR_C, i);
        //     }
        //     //cv.put(MovieContract.MovieEntry.COLUMN_TITLE, jsonMovie.getString(TITLE));
        //     contentValuesArray[i] = cv;
        // }

        return contentValuesArray;
    }

}
