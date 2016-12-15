package com.vukoye.popularmovies.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.vukoye.popularmovies.utils.MoviesJsonUtil;
import com.vukoye.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import static com.vukoye.popularmovies.MainActivity.MOVIE_ORDER_TOP_RATED;
import static com.vukoye.popularmovies.MainActivity.MOVIE_URL_ID;

public class DownloadMoviesData extends IntentService {

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
        boolean isTopRated = intent.getBooleanExtra(MOVIE_ORDER_TOP_RATED, false);
        ContentValues[] contentValuesList = null;
        if (TextUtils.isEmpty(urlString)) {
            return;
        }
        String response;
        try {
            URL url = new URL(urlString);
            response = NetworkUtils.getResponseFromHttpUrl(url);
            contentValuesList = MoviesJsonUtil.getContentValuesFromJson(getApplicationContext(), response, isTopRated);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        if (null != contentValuesList && contentValuesList.length != 0) {
            getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValuesList);
        }
    }
}
