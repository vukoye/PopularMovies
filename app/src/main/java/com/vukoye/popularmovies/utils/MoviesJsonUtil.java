package com.vukoye.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;

import com.vukoye.popularmovies.MoviesPreferences;
import com.vukoye.popularmovies.data.MovieContract;
import com.vukoye.popularmovies.data.MovieDataObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public final class MoviesJsonUtil {

    private static final String TAG = MoviesJsonUtil.class.getSimpleName();

    static final String RESULTS = "results";
    static final String POSTER_PATH = "poster_path";
    static final String ADULT = "adult";
    static final String OVERVIEW = "overview";
    static final String RELEASE_DATE = "release_date";
    static final String GENRE_IDS = "genre_ids";
    static final String ID = "id";
    static final String ORIGINAL_TITLE = "original_title";
    static final String ORIGINAL_LANGUAGE = "original_language";
    static final String TITLE = "title";
    static final String BACKDROP_PATH = "backdrop_path";
    static final String POPULARITY = "popularity";
    static final String VOTE_COUNT = "vote_count";
    static final String VIDEO = "video";
    static final String VOTE_AVERAGE = "vote_average";

    public static MovieDataObject[] getMovieObjectsFromJSON(String jsonStr) throws JSONException {



        MovieDataObject[] movieDataObjects = null;

        JSONObject moviesJson = new JSONObject(jsonStr);

        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);
        //Log.d(TAG, moviesArray.toString());

        movieDataObjects = new MovieDataObject[moviesArray.length()];

        for (int i = 0; i < movieDataObjects.length; i++) {
            JSONObject jsonMovie = moviesArray.getJSONObject(i);
            movieDataObjects[i] = MovieDataObject.newBuilder()
                                                 .withId(jsonMovie.getInt(ID))
                                                 .withOverView(jsonMovie.getString(OVERVIEW))
                                                 .withPosterPath((jsonMovie.getString(POSTER_PATH)).substring(1))
                                                 .withReleaseDate(jsonMovie.getString(RELEASE_DATE))
                                                 .withTitle(jsonMovie.getString(TITLE))
                                                 .withVoteAverage(jsonMovie.getDouble(VOTE_AVERAGE))
                                                 .build();
        }

        return movieDataObjects;
    }

    public static ContentValues[] getContentValuesFromJson(Context context, String jsonStr, final boolean isTopRated) throws JSONException {

        JSONObject moviesJson = new JSONObject(jsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);
        ContentValues[] contentValuesArray = new ContentValues[moviesArray.length()];
        JSONObject jsonMovie;
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        if (moviesArray.length() > 0) {
            MoviesPreferences.setLastUpdate(context, currentTime, isTopRated);
        }
        ContentValues cv;
        for (int i = 0; i < moviesArray.length(); i++) {
            jsonMovie = moviesArray.getJSONObject(i);
            cv = new ContentValues();
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, jsonMovie.getInt(ID));
            cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, (jsonMovie.getString(POSTER_PATH)).substring(1));
            cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, jsonMovie.getString(OVERVIEW));
            cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, jsonMovie.getString(RELEASE_DATE));
            cv.put(MovieContract.MovieEntry.COLUMN_TITLE, jsonMovie.getString(TITLE));
            cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, jsonMovie.getString(VOTE_AVERAGE));
            if (isTopRated) {
                cv.put(MovieContract.MovieEntry.COLUMN_LAST_UPDATED_TOP_RATED, currentTime);
                cv.put(MovieContract.MovieEntry.COLUMN_TOP_RATED_C, i);
            } else {
                cv.put(MovieContract.MovieEntry.COLUMN_LAST_UPDATED_POPULAR, currentTime);
                cv.put(MovieContract.MovieEntry.COLUMN_POPULAR_C, i);
            }
            //cv.put(MovieContract.MovieEntry.COLUMN_TITLE, jsonMovie.getString(TITLE));
            contentValuesArray[i] = cv;
        }

        return contentValuesArray;
    }
}
