package com.vukoye.popularmovies;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vukoye.popularmovies.data.MovieDataObject;
import com.vukoye.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DetailsActivity extends AppCompatActivity {

    TextView mTitle;

    TextView mRelease;

    TextView mRating;

    TextView mOverview;

    ImageView mPoster;

    MovieDataObject mMovieDataObject;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mTitle = (TextView)findViewById(R.id.movie_details_title);
        mOverview = (TextView)findViewById(R.id.movie_details_overview);
        mRating = (TextView)findViewById(R.id.movie_details_rating);
        mRelease = (TextView)findViewById(R.id.movie_details_release);
        mPoster = (ImageView)findViewById(R.id.movie_details_poster);


        if (getIntent().getExtras().containsKey(MainActivity.MOVIE)) {
            mMovieDataObject = (MovieDataObject) getIntent().getExtras().get(MainActivity.MOVIE);
            updateScreen();
        } else {
            showError();
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState, final PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    private void showError() {
        mTitle.setText(R.string.notReceived);
    }

    private void updateScreen() {
        mTitle.setText(mMovieDataObject.getTitle());
        mOverview.setText(mMovieDataObject.getOverview());
        NumberFormat formatter = new DecimalFormat("#0.00");
        mRating.setText(getResources().getString(R.string.ratingString, String.format("%.2f", mMovieDataObject.getVoteAverage())));
        mRelease.setText(mMovieDataObject.getReleaseDate().substring(0, 4));
        URL url = NetworkUtils.buildImageUrl(mMovieDataObject.getPosterPath());
        Picasso.with(this).load(url.toString()).into(mPoster);
    }
}
