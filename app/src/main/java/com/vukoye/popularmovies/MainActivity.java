package com.vukoye.popularmovies;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vukoye.popularmovies.data.MovieDataObject;
import com.vukoye.popularmovies.utils.MoviesJsonUtil;
import com.vukoye.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.vukoye.popularmovies.MoviesPreferences.SORT_ORDER_POPULAR;
import static com.vukoye.popularmovies.MoviesPreferences.SORT_ORDER_TOP_RATED;
import static com.vukoye.popularmovies.MoviesPreferences.getSortOrder;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MOVIE = "MOVIE";

    private static final int MOVIE_LOADER_ID = 54;

    private static final String MOVIE_URL_ID = "movie_url";

    private RecyclerView mRecyclerView;

    private MoviesAdapter mMoviesAdapter;

    private TextView mErrorMessage;

    private ProgressBar mProgressBar;

    private MovieDataObject[] mMovieDataObjects;
    private String mResponse;

    private String lastLoadedWith;

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

        loadMoviesData();
    }

    private void loadMoviesData() {
        URL buildUrl;
        if (MoviesPreferences.getSortOrder(this).equals(MoviesPreferences.SORT_ORDER_TOP_RATED)) {
            buildUrl = NetworkUtils.buildTopRatedUrl(MoviesPreferences.API_KEY);
        } else {
            buildUrl = NetworkUtils.buildPopularRatedUrl(MoviesPreferences.API_KEY);
        }
        Bundle bundle = new Bundle();
        bundle.putString(MOVIE_URL_ID, buildUrl.toString());
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(MOVIE_LOADER_ID, bundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_LOADER_ID, bundle, this);
        }
        //new FetchMovieData().execute(buildUrl);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        MenuItem item_popular = menu.findItem(R.id.action_sort_popularity);
        MenuItem item_rated = menu.findItem(R.id.action_sort_rated);
        item_popular.setVisible(!MoviesPreferences.getSortOrder(this).equals(SORT_ORDER_POPULAR));
        item_rated.setVisible(!MoviesPreferences.getSortOrder(this).equals(SORT_ORDER_TOP_RATED));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_sort_popularity:
                MoviesPreferences.setSortOrder(SORT_ORDER_POPULAR, this);
                mResponse = null;
                loadMoviesData();
                break;
            case R.id.action_sort_rated:
                MoviesPreferences.setSortOrder(SORT_ORDER_TOP_RATED, this);
                mResponse = null;
                loadMoviesData();
                break;
            case R.id.action_refresh:
                loadMoviesData();
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
    public void onClick(final int position) {
        Intent detailsIntent = new Intent(this, DetailsActivity.class);
        detailsIntent.putExtra(MOVIE, mMovieDataObjects[position]);
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
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) {
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);

                if (mResponse != null) {
                    deliverResult(mResponse);
                } else {
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(String response) {
                mResponse = response;
                super.deliverResult(mResponse);
            }

            @Override
            public String loadInBackground() {
                String urlString = args.getString(MOVIE_URL_ID);
                if (TextUtils.isEmpty(urlString)) {
                    return null;
                }
                String response = null;
                try {
                    URL url = new URL(urlString);
                    response = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        try {
            mResponse = data;
            mMovieDataObjects = null;
            if (!TextUtils.isEmpty(mResponse)) {
                mMovieDataObjects = MoviesJsonUtil.getMovieObjectsFromJSON(MainActivity.this, mResponse);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mMovieDataObjects != null) {
            mMoviesAdapter.setMoviesData(mMovieDataObjects);
            showMoviesData();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                RecyclerView parent, RecyclerView.State state) {
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
