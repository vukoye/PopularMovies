package com.vukoye.popularmovies;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vukoye.popularmovies.data.DownloadMoviesData;
import com.vukoye.popularmovies.data.MovieContract;
import com.vukoye.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.Calendar;

import static com.vukoye.popularmovies.MoviesPreferences.NETWORK_STATE_ERROR;
import static com.vukoye.popularmovies.MoviesPreferences.SORT_ORDER_POPULAR;
import static com.vukoye.popularmovies.MoviesPreferences.SORT_ORDER_TOP_RATED;
import static com.vukoye.popularmovies.MoviesPreferences.getLastUpdate;
import static com.vukoye.popularmovies.MoviesPreferences.getSortOrder;
import static com.vukoye.popularmovies.data.DownloadMoviesData.ACTION_MOVIES;
import static com.vukoye.popularmovies.data.DownloadMoviesData.ACTION_TYPE;
import static com.vukoye.popularmovies.data.DownloadMoviesData.MOVIE_ORDER_TOP_RATED;
import static com.vukoye.popularmovies.data.DownloadMoviesData.MOVIE_URL_ID;

//nvtd no data if disabled network

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor>,
        DownloadResultsReceiver.Receiver {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MOVIE_LOADER_ID = 54;

    //private static final long MS_BETWEEN_UPDATES = 5 * 60 * 1000; // 5 MINUTES
    private static final long MS_BETWEEN_UPDATES = 0; // 0 MINUTES (always update)

    private RecyclerView mRecyclerView;

    private MoviesAdapter mMoviesAdapter;

    private TextView mErrorMessage;

    private ProgressBar mProgressBar;

    private TextView mNetworkState;

    private DownloadResultsReceiver mReceiver;

    private LinearLayout mDataContainer;

    private MoviesContentObserver mMoviesContentObserver;

    Cursor mMoviesData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycle_view);

        mErrorMessage = (TextView) findViewById(R.id.main_error_message);
        mNetworkState = (TextView) findViewById(R.id.main_network_state);
        mDataContainer = (LinearLayout) findViewById(R.id.main_recycle_container);

        mProgressBar = (ProgressBar) findViewById(R.id.main_progress_bar);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        mMoviesAdapter = new MoviesAdapter(this, this);

        mRecyclerView.setAdapter(mMoviesAdapter);

        mMoviesContentObserver = new MoviesContentObserver(new Handler());

        mReceiver = new DownloadResultsReceiver(new Handler());
        mReceiver.setReceiver(this);

        reloadMovies();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, mMoviesContentObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mMoviesContentObserver);
    }

    @Override
    public void onReceiveResult(final int resultCode, final Bundle resultData) {

        if (resultCode == MoviesPreferences.NETWORK_STATE_QUERYNG || resultCode == NETWORK_STATE_ERROR) {
            Log.d(TAG, "onReceiveResult: ERROR");
            mNetworkState.setVisibility(View.VISIBLE);
        } else  {
            Log.d(TAG, "onReceiveResult: IDLE");
            mNetworkState.setVisibility(View.GONE);
        }
    }

    public class MoviesContentObserver extends ContentObserver {

        MoviesContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            loadDataFromDb();
        }

    }

    private void reloadMovies() {
        Log.d(TAG, "reloadMovies: ");
        loadDataFromDb(); // first we load local data if has one
        if (isNetworkUpdateNeeded()) {
            Log.d(TAG, "reloadMovies network updated is needed");
            URL buildUrl;
            if (MoviesPreferences.getSortOrder(getApplicationContext()).equals(MoviesPreferences.SORT_ORDER_TOP_RATED)) {
                buildUrl = NetworkUtils.buildTopRatedUrl(MoviesPreferences.API_KEY);
            } else {
                buildUrl = NetworkUtils.buildPopularRatedUrl(MoviesPreferences.API_KEY);
            }

            Intent downloadMoviesIntent = new Intent(this, DownloadMoviesData.class);
            downloadMoviesIntent.putExtra(ACTION_TYPE, ACTION_MOVIES);
            downloadMoviesIntent.putExtra(MOVIE_URL_ID, buildUrl.toString());
            downloadMoviesIntent.putExtra(DownloadMoviesData.RECEIVER, mReceiver);
            downloadMoviesIntent.putExtra(MOVIE_ORDER_TOP_RATED, MoviesPreferences.getSortOrder(getApplicationContext())
                                                                                  .equals(MoviesPreferences.SORT_ORDER_TOP_RATED));
            startService(downloadMoviesIntent);
        } else {
            Log.d(TAG, "No need for reloading from network");
        }

    }

    void loadDataFromDb() {

        Log.d(TAG, "loadDataFromDb: start loader");
        if (getSupportLoaderManager().getLoader(MOVIE_LOADER_ID) == null) {
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);

        }
    }

    boolean isNetworkUpdateNeeded() {
        Calendar calendar = Calendar.getInstance();
        long currentTimeInMs = calendar.getTimeInMillis();
        return currentTimeInMs - MoviesPreferences.getLastUpdate(getApplicationContext()) > MS_BETWEEN_UPDATES;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        MenuItem item_popular = menu.findItem(R.id.action_sort_popularity);
        MenuItem item_rated = menu.findItem(R.id.action_sort_rated);
        item_popular.setVisible(!MoviesPreferences.getSortOrder(getApplicationContext()).equals(SORT_ORDER_POPULAR));
        item_rated.setVisible(!MoviesPreferences.getSortOrder(getApplicationContext()).equals(SORT_ORDER_TOP_RATED));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_sort_popularity:
                MoviesPreferences.setSortOrder(SORT_ORDER_POPULAR, this);
                mMoviesData = null;
                reloadMovies();
                break;
            case R.id.action_sort_rated:
                MoviesPreferences.setSortOrder(SORT_ORDER_TOP_RATED, this);
                mMoviesData = null;
                reloadMovies();
                break;
            case R.id.action_refresh:
                mMoviesData = null;
                reloadMovies();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(final int movie_id) {
        Intent detailsIntent = new Intent(this, DetailsActivity.class);
        detailsIntent.putExtra(DownloadMoviesData.MOVIE_ID, movie_id);
        startActivity(detailsIntent);
    }

    private void showMoviesData() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mDataContainer.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

    }

    private void showErrorMessage() {
        mDataContainer.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        if (MOVIE_LOADER_ID == id) {
            return new AsyncTaskLoader<Cursor>(this) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    mProgressBar.setVisibility(View.VISIBLE);
                    if (mMoviesData != null) {
                        deliverResult(mMoviesData);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {
                    try {
                        Log.d(TAG, "Start Loading in Background");
                        String sortOrder;
                        String where;
                        String[] whereArgs;
                        long lastUpdate = getLastUpdate(getApplicationContext());
                        if (getSortOrder(getApplicationContext()).equals(SORT_ORDER_TOP_RATED)) {
                            Log.d(TAG, "Load By top rated");
                            sortOrder = MovieContract.MovieEntry.COLUMN_TOP_RATED_C;
                            where = MovieContract.MovieEntry.COLUMN_LAST_UPDATED_TOP_RATED + " = ?";
                        } else {
                            Log.d(TAG, "Load By popular");
                            sortOrder = MovieContract.MovieEntry.COLUMN_POPULAR_C;
                            where = MovieContract.MovieEntry.COLUMN_LAST_UPDATED_POPULAR + " = ?";
                        }
                        whereArgs = new String[] {String.valueOf(lastUpdate)};
                        return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, where, whereArgs, sortOrder);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to asynchronously load data.");
                        e.printStackTrace();
                        return null;
                    }
                }

                public void deliverResult(Cursor data) {
                    mMoviesData = data;
                    super.deliverResult(data);
                }
            };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mProgressBar.setVisibility(View.INVISIBLE);
        if (data == null || data.getCount() <= 0) {
            Log.d(TAG, "Load finished no data");
            showErrorMessage();
        } else {
            Log.d(TAG, "Load finished showing data");
            showMoviesData();
            mMoviesAdapter.swapCursor(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

}
