package com.vukoye.popularmovies;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vukoye.popularmovies.data.MovieContract;
import com.vukoye.popularmovies.data.MovieDataObject;
import com.vukoye.popularmovies.utils.NetworkUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = DetailsActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 55;

    @BindView(R.id.movie_details_title)
    TextView mTitle;

    @BindView(R.id.movie_details_release)
    TextView mRelease;

    @BindView(R.id.movie_details_rating)
    TextView mRating;

    @BindView(R.id.movie_details_overview)
    TextView mOverview;

    @BindView(R.id.movie_details_poster)
    ImageView mPoster;

    MovieDataObject mMovieDataObject;

    int mMovieID;

    MovieContentObserver mMovieContentObserver;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        if (getIntent().getExtras().containsKey(MainActivity.MOVIE_ID)) {
            mMovieID = getIntent().getExtras().getInt(MainActivity.MOVIE_ID);
            //mMovieDataObject = (MovieDataObject) getIntent().getExtras().get(MainActivity.MOVIE);
            if (getSupportLoaderManager().getLoader(MOVIE_LOADER_ID) == null) {
                getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
            } else {
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            }
        } else {
            showError();
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState, final PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        if (MOVIE_LOADER_ID == id) {
            return new AsyncTaskLoader<Cursor>(this) {
                Cursor mMovieData = null;

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (mMovieData != null) {
                        deliverResult(mMovieData);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {
                    try {
                        Log.d(TAG, "Start Loading in Background");
                        String where = MovieContract.MovieEntry._ID + " = ?";
                        String[] whereArgs = new String[] {String.valueOf(mMovieID)};
                        return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, where, whereArgs, null);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to asynchronously load data.");
                        e.printStackTrace();
                        return null;
                    }
                }

                public void deliverResult(Cursor data) {
                    mMovieData = data;
                    super.deliverResult(data);
                }
            };
        }
        return null;
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        if (data == null) {
            Log.d(TAG, "Load finished no data");
            showError();
        } else {
            Log.d(TAG, "Load finished showing data");
            fillDataObject(data);
            updateScreen();
        }
    }

    private void fillDataObject(Cursor cursor) {
        Log.d(TAG, "fillDataObject");
        if (cursor.moveToPosition(0)) {
        int id = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
        int movie_id = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int posterPathIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        int titleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int overviewIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        int ratingIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int raleaseIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);

        mMovieDataObject = MovieDataObject.newBuilder()
                                          .withId(cursor.getInt(movie_id))
                                          .withOverView(cursor.getString(overviewIndex))
                                          .withPosterPath(cursor.getString(posterPathIndex))
                                          .withVoteAverage(cursor.getDouble(ratingIndex))
                                          .withReleaseDate(cursor.getString(raleaseIndex))
                                          .withTitle(cursor.getString(titleIndex))
                                          .build();


        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {

    }

    public class MovieContentObserver extends ContentObserver {

        public MovieContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            updateScreen();
        }

    }

    private void showError() {
        mTitle.setText(R.string.notReceived);
    }

    private void updateScreen() {
        if (mMovieDataObject != null) {
            mTitle.setText(mMovieDataObject.getTitle());
            mOverview.setText(mMovieDataObject.getOverview());
            mRating.setText(getResources().getString(R.string.ratingString, String.format("%.2f", mMovieDataObject.getVoteAverage())));
            mRelease.setText(mMovieDataObject.getReleaseDate().substring(0, 4));
            URL url = NetworkUtils.buildImageUrl(mMovieDataObject.getPosterPath());
            Picasso.with(this).load(url.toString()).into(mPoster);
        }
    }
}
