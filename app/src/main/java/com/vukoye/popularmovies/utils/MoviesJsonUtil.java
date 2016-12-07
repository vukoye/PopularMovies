package com.vukoye.popularmovies.utils;

import android.content.Context;

import com.vukoye.popularmovies.data.MovieDataObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by nemanja on 12/5/16.
 */

public final class MoviesJsonUtil {

    private static final String TAG = MoviesJsonUtil.class.getSimpleName();

    public static MovieDataObject[] getMovieObjectsFromJSON(Context context, String jsonStr) throws JSONException {

        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String ADULT = "adult";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String GENRE_IDS = "genre_ids";
        final String ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String ORIGINAL_LANGUAGE = "original_language";
        final String TITLE = "title";
        final String BACKDROP_PATH = "backdrop_path";
        final String POPULARITY = "popularity";
        final String VOTE_COUNT = "vote_count";
        final String VIDEO = "video";
        final String VOTE_AVERAGE = "vote_average";

        MovieDataObject[] movieDataObjects = null;

        //nvtd prouci kako ovo cudo menja izbacuje error
        final String OWM_MESSAGE_CODE = "cod";

        JSONObject moviesJson = new JSONObject(jsonStr);

        /* Is there an error? */
        if (moviesJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = moviesJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

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
}
