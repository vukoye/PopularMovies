package com.vukoye.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;

import com.vukoye.popularmovies.MoviesPreferences;
import com.vukoye.popularmovies.data.MovieContract;

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
    static final String ID = "id";
    static final String KEY = "key";
    static final String NAME = "name";
    static final String SITE = "site";
    static final String SIZE = "size";
    public static ContentValues[] getContentValuesFromJson(Context context, String jsonStr, final String movieId) throws JSONException {

        JSONObject moviesJson = new JSONObject(jsonStr);
        JSONArray trailerArray = moviesJson.getJSONArray(RESULTS);
        ContentValues[] contentValuesArray = new ContentValues[trailerArray.length()];
        JSONObject jsonTrailer;
        if (trailerArray.length() > 0) {
            Calendar calendar = Calendar.getInstance();
            long currentTime = calendar.getTimeInMillis();
            MoviesPreferences.setLastTrailersUpdate(context, currentTime);
        }
        ContentValues cv;
        for (int i = 0; i < trailerArray.length(); i++) {
            jsonTrailer = trailerArray.getJSONObject(i);
            cv = new ContentValues();
            cv.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
            cv.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID,jsonTrailer.getString(ID));
            cv.put(MovieContract.TrailerEntry.COLUMN_KEY,jsonTrailer.getString(KEY));
            cv.put(MovieContract.TrailerEntry.COLUMN_NAME,jsonTrailer.getString(NAME));
            cv.put(MovieContract.TrailerEntry.COLUMN_SITE,jsonTrailer.getString(SITE));
            cv.put(MovieContract.TrailerEntry.COLUMN_SIZE,jsonTrailer.getString(SIZE));
            contentValuesArray[i] = cv;
        }

        return contentValuesArray;
    }

}
