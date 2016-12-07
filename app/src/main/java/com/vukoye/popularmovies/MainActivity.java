package com.vukoye.popularmovies;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.net.URL;

import static com.vukoye.popularmovies.MoviesPreferences.SORT_ORDER_POPULAR;
import static com.vukoye.popularmovies.MoviesPreferences.SORT_ORDER_TOP_RATED;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MOVIE = "MOVIE";

    private RecyclerView mRecyclerView;

    private MoviesAdapter mMoviesAdapter;

    private TextView mErrorMessage;

    private ProgressBar mProgressBar;

    private MovieDataObject[] mMovieDataObjects;

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

        new FetchMovieData().execute(buildUrl);
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
                loadMoviesData();
                break;
            case R.id.action_sort_rated:
                MoviesPreferences.setSortOrder(SORT_ORDER_TOP_RATED, this);
                loadMoviesData();
                break;
            case R.id.action_refresh:
                loadMoviesData();
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

    private class FetchMovieData extends AsyncTask<URL, Void, MovieDataObject[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override

        protected MovieDataObject[] doInBackground(final URL... urls) {

            if (urls.length == 0) {
                return null;
            }
            mMovieDataObjects = null;
            try {
                String response = NetworkUtils.getResponseFromHttpUrl(urls[0]);
                mMovieDataObjects = MoviesJsonUtil.getMovieObjectsFromJSON(MainActivity.this, response);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mMovieDataObjects;
        }

        @Override
        protected void onPostExecute(final MovieDataObject[] moviesDataObjects) {
            super.onPostExecute(moviesDataObjects);
            mProgressBar.setVisibility(View.INVISIBLE);
            if (moviesDataObjects != null) {
                mMoviesAdapter.setMoviesData(moviesDataObjects);
                showMoviesData();
            } else {
                showErrorMessage();
            }
        }
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

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

}
