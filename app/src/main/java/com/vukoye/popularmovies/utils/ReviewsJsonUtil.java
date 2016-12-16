package com.vukoye.popularmovies.utils;

/**
 * Created by nemanja on 12/12/16.
 */

public class ReviewsJsonUtil {
    // static final String RESULTS = "results";
    // public static ContentValues[] getContentValuesFromJson(Context context, String jsonStr, final boolean isTopRated) throws JSONException {
    //
    //     JSONObject moviesJson = new JSONObject(jsonStr);
    //     JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);
    //     ContentValues[] contentValuesArray = new ContentValues[moviesArray.length()];
    //     JSONObject jsonMovie;
    //     Calendar calendar = Calendar.getInstance();
    //     long currentTime = calendar.getTimeInMillis();
    //     if (moviesArray.length() > 0) {
    //         MoviesPreferences.setLastUpdate(context, currentTime, isTopRated);
    //     }
    //     ContentValues cv;
    //     for (int i = 0; i < moviesArray.length(); i++) {
    //         jsonMovie = moviesArray.getJSONObject(i);
    //         cv = new ContentValues();
    //         cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, jsonMovie.getInt(ID));
    //         cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, (jsonMovie.getString(POSTER_PATH)).substring(1));
    //         cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, jsonMovie.getString(OVERVIEW));
    //         cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, jsonMovie.getString(RELEASE_DATE));
    //         cv.put(MovieContract.MovieEntry.COLUMN_TITLE, jsonMovie.getString(TITLE));
    //         cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, jsonMovie.getString(VOTE_AVERAGE));
    //         if (isTopRated) {
    //             cv.put(MovieContract.MovieEntry.COLUMN_LAST_UPDATED_TOP_RATED, currentTime);
    //             cv.put(MovieContract.MovieEntry.COLUMN_TOP_RATED_C, i);
    //         } else {
    //             cv.put(MovieContract.MovieEntry.COLUMN_LAST_UPDATED_POPULAR, currentTime);
    //             cv.put(MovieContract.MovieEntry.COLUMN_POPULAR_C, i);
    //         }
    //         //cv.put(MovieContract.MovieEntry.COLUMN_TITLE, jsonMovie.getString(TITLE));
    //         contentValuesArray[i] = cv;
    //     }
    //
    //     return contentValuesArray;
    // }

}
