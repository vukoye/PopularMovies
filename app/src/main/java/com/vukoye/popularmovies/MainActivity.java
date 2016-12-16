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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vukoye.popularmovies.data.DownloadMoviesData;
import com.vukoye.popularmovies.data.MovieContract;
import com.vukoye.popularmovies.data.MovieDataObject;
import com.vukoye.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.Calendar;

import static com.vukoye.popularmovies.MoviesPreferences.SORT_ORDER_POPULAR;
import static com.vukoye.popularmovies.MoviesPreferences.SORT_ORDER_TOP_RATED;
import static com.vukoye.popularmovies.MoviesPreferences.getLastUpdate;
import static com.vukoye.popularmovies.MoviesPreferences.getSortOrder;
import static com.vukoye.popularmovies.data.DownloadMoviesData.ACTION_MOVIES;
import static com.vukoye.popularmovies.data.DownloadMoviesData.ACTION_TYPE;
import static com.vukoye.popularmovies.data.DownloadMoviesData.MOVIE_ORDER_TOP_RATED;
import static com.vukoye.popularmovies.data.DownloadMoviesData.MOVIE_URL_ID;

//nvtd no data if disabled network

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MOVIE = "MOVIE";

    private static final int MOVIE_LOADER_ID = 54;

    private static final long MS_BETWEEN_UPDATES = 5 * 60 * 1000; // 5 MINUTES

    private RecyclerView mRecyclerView;

    private MoviesAdapter mMoviesAdapter;

    private TextView mErrorMessage;

    private ProgressBar mProgressBar;

    private MovieDataObject[] mMovieDataObjects;
    private String mResponse;

    private String lastLoadedWith;
    private MoviesContentObserver mMoviesContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycle_view);

        mErrorMessage = (TextView) findViewById(R.id.main_error_message);

        mProgressBar = (ProgressBar) findViewById(R.id.main_progress_bar);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        mMoviesAdapter = new MoviesAdapter(this, this);

        mRecyclerView.setAdapter(mMoviesAdapter);

        mMoviesContentObserver = new MoviesContentObserver(new Handler());

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

    public class MoviesContentObserver extends ContentObserver {

        public MoviesContentObserver(Handler handler) {
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
            downloadMoviesIntent.putExtra(MOVIE_ORDER_TOP_RATED, MoviesPreferences.getSortOrder(getApplicationContext())
                                                                                  .equals(MoviesPreferences.SORT_ORDER_TOP_RATED));
            startService(downloadMoviesIntent);
        } else {
            Log.d(TAG, "No need for reloading from network");
        }

    }

    void loadDataFromDb() {
        if (getLastUpdate(getApplicationContext()) > 0) {
            Log.d(TAG, "loadDataFromDb: startloader");
            if (getSupportLoaderManager().getLoader(MOVIE_LOADER_ID) == null) {
                getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
            } else {
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            }
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
                mResponse = null;
                reloadMovies();
                break;
            case R.id.action_sort_rated:
                MoviesPreferences.setSortOrder(SORT_ORDER_TOP_RATED, this);
                mResponse = null;
                reloadMovies();
                break;
            case R.id.action_refresh:
                reloadMovies();
                mResponse = null;
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
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        if (MOVIE_LOADER_ID == id) {
            return new AsyncTaskLoader<Cursor>(this) {
                Cursor mMoviesData = null;

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
                        whereArgs = new String[] { String.valueOf(lastUpdate)};
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
        if (data == null) {
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

        public SpacesItemDecoration(int space) {
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
