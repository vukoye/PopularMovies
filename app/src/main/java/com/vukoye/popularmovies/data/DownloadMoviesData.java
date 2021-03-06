package com.vukoye.popularmovies.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.vukoye.popularmovies.MoviesPreferences;
import com.vukoye.popularmovies.utils.MoviesJsonUtil;
import com.vukoye.popularmovies.utils.NetworkUtils;
import com.vukoye.popularmovies.utils.ReviewsJsonUtil;
import com.vukoye.popularmovies.utils.TrailerJsonUtil;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class DownloadMoviesData extends IntentService {



    public static final String MOVIE_ID = "movie_id";

    public static final String MOVIE_URL_ID = "movie_url";

    public static final String MOVIE_ORDER_TOP_RATED = "movie_order_top_rated";

    public static final String ACTION_TYPE = "action_type";

    public static final String ACTION_MOVIES = "action_movies";
    public static final String ACTION_REVIEWS = "action_reviews";
    public static final String ACTION_TRAILERS = "action_trailers";
    public static final String RECEIVER = "receiver";
    private static final String TAG = DownloadMoviesData.class.getSimpleName();
    ResultReceiver receiver;

    public DownloadMoviesData() {
        super(DownloadMoviesData.class.getSimpleName());
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        String urlString = intent.getStringExtra(MOVIE_URL_ID);
        receiver = intent.getParcelableExtra(RECEIVER);
        switch (intent.getStringExtra(ACTION_TYPE)) {
            case ACTION_MOVIES:
                boolean isTopRated = intent.getBooleanExtra(MOVIE_ORDER_TOP_RATED, false);
                downloadMovies(urlString, isTopRated);
                break;
            case ACTION_REVIEWS:
                String idString = intent.getStringExtra(MOVIE_ID);
                downloadReviews(idString, urlString);
                break;
            case ACTION_TRAILERS:
                String idString1 = intent.getStringExtra(MOVIE_ID);
                downloadTrailers(idString1, urlString);
                break;
        }
    }

    private void downloadTrailers(String movieId, String urlString) {
        ContentValues[] contentValuesList = null;
        if (TextUtils.isEmpty(movieId)) {
            return;
        }
        String response;
        try {
            URL url = new URL(urlString);
            response = NetworkUtils.getResponseFromHttpUrl(url);
            Log.d(TAG, response);
            contentValuesList = TrailerJsonUtil.getContentValuesFromJson(getApplicationContext(), response, movieId);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        if (null != contentValuesList && contentValuesList.length != 0) {
            getContentResolver().bulkInsert(MovieContract.TrailerEntry.getContentUri(movieId), contentValuesList);
        }
    }

    private void downloadReviews(String movieId, String urlString) {
        ContentValues[] contentValuesList = null;
        if (TextUtils.isEmpty(movieId)) {
            return;
        }
        String response;
        try {
            URL url = new URL(urlString);
            response = NetworkUtils.getResponseFromHttpUrl(url);
            Log.d(TAG, response);
            contentValuesList = ReviewsJsonUtil.getContentValuesFromJson(getApplicationContext(), response, movieId);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        if (null != contentValuesList && contentValuesList.length != 0) {
            getContentResolver().bulkInsert(MovieContract.ReviewEntry.getContentUri(movieId), contentValuesList);
        }
    }

    private void downloadMovies(final String urlString, final boolean isTopRated) {
        ContentValues[] contentValuesList = null;
        if (TextUtils.isEmpty(urlString)) {
            return;
        }

        String response;
        try {
            URL url = new URL(urlString);
            receiver.send(MoviesPreferences.NETWORK_STATE_QUERYNG, new Bundle());
            response = NetworkUtils.getResponseFromHttpUrl(url);
            contentValuesList = MoviesJsonUtil.getContentValuesFromJson(getApplicationContext(), response, isTopRated);
        } catch (IOException | JSONException e) {
            receiver.send(MoviesPreferences.NETWORK_STATE_ERROR, new Bundle());
            e.printStackTrace();
        }
        if (null != contentValuesList && contentValuesList.length != 0) {
            receiver.send(MoviesPreferences.NETWORK_STATE_IDLE, new Bundle());
            getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValuesList);
        } else {
            receiver.send(MoviesPreferences.NETWORK_STATE_ERROR, new Bundle());
        }
    }
}
