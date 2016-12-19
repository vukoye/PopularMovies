package com.vukoye.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vukoye.popularmovies.data.DownloadMoviesData;
import com.vukoye.popularmovies.data.MovieContract;
import com.vukoye.popularmovies.data.MovieDataObject;
import com.vukoye.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.vukoye.popularmovies.data.DownloadMoviesData.ACTION_REVIEWS;
import static com.vukoye.popularmovies.data.DownloadMoviesData.ACTION_TRAILERS;
import static com.vukoye.popularmovies.data.DownloadMoviesData.ACTION_TYPE;
import static com.vukoye.popularmovies.data.DownloadMoviesData.MOVIE_ID;
import static com.vukoye.popularmovies.data.DownloadMoviesData.MOVIE_URL_ID;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, TrailersAdapter.TrailersAdapterOnClickHandler {

    public static final String TAG = DetailsActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 55;
    private static final int TRAILERS_LOADER_ID = 56;
    private static final int REVIEWS_LOADER_ID = 57;
    private static final java.lang.String ACTIVE_TRAILERS_ID = "active_trailers";

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

    @BindView(R.id.movie_details_favorite)
    ImageButton mFavorite;

    @BindView(R.id.movie_details_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.movie_details_trailers_tab)
    TextView mTrailersTab;

    @BindView(R.id.movie_details_reviews_tab)
    TextView mReviewsTab;

    TrailersAdapter mTrailersAdapter;

    ReviewsAdapter mReviewsAdapter;

    // @BindView(R.id.movie_details_toolbar)
    // Toolbar mToolbar;
    //
    // @BindView(R.id.movie_detail_collapsing_toolbar)
    // CollapsingToolbarLayout mCollapsingToolbarLayout;

    MovieDataObject mMovieDataObject;

    int mMovieID = -1;

    MovieContentObserver mMovieContentObserver;
    TrailersContentObserver mTrailersContentObserver;
    ReviewsContentObserver mReviewsContentObserver;

    boolean mActiveTrailers = true;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        if (getIntent().getExtras().containsKey(DownloadMoviesData.MOVIE_ID)) {
            mMovieID = getIntent().getExtras().getInt(DownloadMoviesData.MOVIE_ID);
        }
        if (savedInstanceState != null) {
            mMovieID = savedInstanceState.getInt(MOVIE_ID);
            mActiveTrailers = savedInstanceState.getBoolean(ACTIVE_TRAILERS_ID);
        }
        if (mMovieID != -1) { //load Preexisting Data
            loadMovieFromDb();
            loadTrailersFromDb();
            loadReviewsFromDb();
        } else {
            showError();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mTrailersAdapter = new TrailersAdapter(this, this);
        mReviewsAdapter = new ReviewsAdapter(this);
        //nvtd on rotation save state

        mRecyclerView.setAdapter(mActiveTrailers ? mTrailersAdapter : mReviewsAdapter);

        mMovieContentObserver = new MovieContentObserver(new Handler());
        mReviewsContentObserver = new ReviewsContentObserver(new Handler());
        mTrailersContentObserver = new TrailersContentObserver(new Handler());

        reloadReviews();
        reloadTrailers();

    }

    boolean isReviewsNetworkUpdateNeeded() {
        Calendar calendar = Calendar.getInstance();
        long currentTimeInMs = calendar.getTimeInMillis();
        return currentTimeInMs - MoviesPreferences.getReviewsLastUpdate(getApplicationContext()) > 5000;
    }

    boolean isTrailersNetworkUpdateNeeded() {
        Calendar calendar = Calendar.getInstance();
        long currentTimeInMs = calendar.getTimeInMillis();
        return currentTimeInMs - MoviesPreferences.getTrailersLastUpdate(getApplicationContext()) > 5000;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MOVIE_ID, mMovieID);
        outState.putBoolean(ACTIVE_TRAILERS_ID, mActiveTrailers);
    }

    private void loadMovieFromDb() {
        Log.d(TAG, "loadMovieFromDb");
        if (getSupportLoaderManager().getLoader(MOVIE_LOADER_ID) == null) {
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }
    }

    private void loadReviewsFromDb() {
        Log.d(TAG, "loadReviewsFromDb: ");
        if (getSupportLoaderManager().getLoader(REVIEWS_LOADER_ID) == null) {
            getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);
        } else {
            getSupportLoaderManager().restartLoader(REVIEWS_LOADER_ID, null, this);
        }
    }

    private void loadTrailersFromDb() {
        Log.d(TAG, "loadTrailersFromDb: ");
        if (getSupportLoaderManager().getLoader(TRAILERS_LOADER_ID) == null) {
            getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, null, this);
        } else {
            getSupportLoaderManager().restartLoader(TRAILERS_LOADER_ID, null, this);
        }
    }

    private void reloadTrailers() {
        Log.d(TAG, "reloadTrailers: ");
        URL trailersUrl = NetworkUtils.buildTrailersUrl(MoviesPreferences.API_KEY, String.valueOf(mMovieID));
        Intent downloadTrailersIntent = new Intent(this, DownloadMoviesData.class);
        downloadTrailersIntent.putExtra(ACTION_TYPE, ACTION_TRAILERS);
        downloadTrailersIntent.putExtra(MOVIE_URL_ID, trailersUrl.toString());
        downloadTrailersIntent.putExtra(MOVIE_ID, String.valueOf(mMovieID));
        startService(downloadTrailersIntent);
    }
    private void reloadReviews() {
        Log.d(TAG, "reloadReviews: ");
        URL reviewsUri = NetworkUtils.buildReviewsUrl(MoviesPreferences.API_KEY, String.valueOf(mMovieID));
        Intent downloadReviewsIntent = new Intent(this, DownloadMoviesData.class);
        downloadReviewsIntent.putExtra(ACTION_TYPE, ACTION_REVIEWS);
        downloadReviewsIntent.putExtra(MOVIE_URL_ID, reviewsUri.toString());
        downloadReviewsIntent.putExtra(MOVIE_ID, String.valueOf(mMovieID));
        startService(downloadReviewsIntent);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState, final PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        Log.d(TAG, "onCreateLoader: " + id);
        if (MOVIE_LOADER_ID == id) {
            return getMovieLoader();
        } else if (TRAILERS_LOADER_ID == id) {
            return getTrailersLoader();
        } else if (REVIEWS_LOADER_ID == id) {
            return getReviewsLoader();
        }
        return null;
    }

    private AsyncTaskLoader<Cursor> getMovieLoader() {
        Log.d(TAG, "getMovieLoader: ");
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
                    Uri movieUri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovieID)).build();
                    return getContentResolver().query(movieUri, null, null, null, null);
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

    private AsyncTaskLoader<Cursor> getTrailersLoader() {
        Log.d(TAG, "getTrailersLoader: ");
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor trailersData;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (trailersData != null) {
                    deliverResult(trailersData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    Log.d(TAG, "Start Loading in Background");
                    Uri trailerUrl = MovieContract.TrailerEntry.getContentUri(String.valueOf(mMovieID));
                    return getContentResolver().query(trailerUrl, null, null, null, null);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                trailersData = data;
                super.deliverResult(data);
            }
        };
    }

    private AsyncTaskLoader<Cursor> getReviewsLoader() {
        Log.d(TAG, "getReviewsLoader: ");
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor reviewsData = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (reviewsData != null) {
                    deliverResult(reviewsData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    Log.d(TAG, "Start Loading in Background");
                    Uri reviewUri = MovieContract.ReviewEntry.getContentUri(String.valueOf(mMovieID));
                    return getContentResolver().query(reviewUri, null, null, null, null);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load reviews data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                reviewsData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        Log.d(TAG, "onLoadFinished:");
        if (data == null) {
            Log.d(TAG, "Load finished no data");
            showError();
        } else {
            if (loader.getId() == MOVIE_LOADER_ID) {
                Log.d(TAG, "Movie Load finished showing data");
                fillDataObject(data);
                updateScreen();
            } else if (loader.getId() == TRAILERS_LOADER_ID) {
                Log.d(TAG, "Trailers Load finished showing data");
                mTrailersAdapter.swapCursor(data);
            } else if (loader.getId() == REVIEWS_LOADER_ID) {
                Log.d(TAG, "Reviews Load finished showing data");
                mReviewsAdapter.swapCursor(data);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovieID)).build();
        getContentResolver().registerContentObserver(movieUri, true, mMovieContentObserver);
        getContentResolver().registerContentObserver(MovieContract.TrailerEntry.getContentUri(String.valueOf(mMovieID)), true, mTrailersContentObserver);
        getContentResolver().registerContentObserver(MovieContract.ReviewEntry.getContentUri(String.valueOf(mMovieID)), true, mReviewsContentObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mMovieContentObserver);
        getContentResolver().unregisterContentObserver(mTrailersContentObserver);
        getContentResolver().unregisterContentObserver(mReviewsContentObserver);
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
            int favoriteIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE);

            mMovieDataObject = MovieDataObject.newBuilder()
                    .withId(cursor.getInt(movie_id))
                    .withOverView(cursor.getString(overviewIndex))
                    .withPosterPath(cursor.getString(posterPathIndex))
                    .withVoteAverage(cursor.getDouble(ratingIndex))
                    .withReleaseDate(cursor.getString(raleaseIndex))
                    .withTitle(cursor.getString(titleIndex))
                    .withFavorite(cursor.getInt(favoriteIndex) == 1)
                    .build();
        }
    }

    private void updateFavorite(boolean state) {
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovieID)).build();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, state ? 1 : 0);
        getContentResolver().update(movieUri, contentValues, null, null);
    }

    @OnClick(R.id.movie_details_favorite)
    void changeFavoriteState() {
        mMovieDataObject.setFavorite(!mMovieDataObject.isFavorite());
        updateFavorite(mMovieDataObject.isFavorite());
        updateScreen();
    }

    @OnClick(R.id.movie_details_trailers_tab)
    public void setTrailersTabActive() {
        if (!mActiveTrailers) {
            mActiveTrailers = true;
            mRecyclerView.setAdapter(mTrailersAdapter);
            updateScreen();
        }
    }

    @OnClick(R.id.movie_details_reviews_tab)
    public void setReviewsTabActive() {
        if (mActiveTrailers) {
            mActiveTrailers = false;
            mRecyclerView.setAdapter(mReviewsAdapter);
            updateScreen();
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        if (loader.getId() == TRAILERS_LOADER_ID) {
            mTrailersAdapter.swapCursor(null);
        } else if (loader.getId() == REVIEWS_LOADER_ID) {
            mReviewsAdapter.swapCursor(null);
        }
    }

    @Override
    public void onClick(String key) {
        //onTrailerclick go to youtube
        Log.d(TAG, "Playing movie");
        Uri uri = Uri.parse("http://www.youtube.com/watch?v=" + key);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public class MovieContentObserver extends ContentObserver {

        public MovieContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
            Log.d(TAG, "onChange: 1");
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            loadMovieFromDb();
            Log.d(TAG, "onChange: 2");
        }
    }

    public class TrailersContentObserver extends ContentObserver {

        public TrailersContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
            Log.d(TAG, "onChange: trailers 1");
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            loadTrailersFromDb();
            Log.d(TAG, "onChange trailers 2");
        }
    }

    public class ReviewsContentObserver extends ContentObserver {

        public ReviewsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
            Log.d(TAG, "onChange: reviews 1");
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            loadReviewsFromDb();
            Log.d(TAG, "onChange: reviews 2");
        }
    }

    private void colorActiveTab() {
        if (mActiveTrailers) {
            mTrailersTab.setTextColor(getResources().getColor(R.color.favorite));
            mReviewsTab.setTextColor(getResources().getColor(R.color.white));
        } else {
            mReviewsTab.setTextColor(getResources().getColor(R.color.favorite));
            mTrailersTab.setTextColor(getResources().getColor(R.color.white));
        }
    }


    private void showError() {
        mTitle.setText(R.string.notReceived);
    }

    private void updateScreen() {
        Log.d(TAG, "updateScreen: ");
        if (mMovieDataObject != null) {
            mTitle.setText(mMovieDataObject.getTitle());
            mOverview.setText(mMovieDataObject.getOverview());
            mRating.setText(getResources().getString(R.string.ratingString, String.format("%.2f", mMovieDataObject.getVoteAverage())));
            mRelease.setText(mMovieDataObject.getReleaseDate().substring(0, 4));
            mFavorite.setSelected(mMovieDataObject.isFavorite());
            URL url = NetworkUtils.buildImageUrl(mMovieDataObject.getPosterPath());
            Picasso.with(this).load(url.toString()).into(mPoster);
        }
        colorActiveTab();
    }
}
