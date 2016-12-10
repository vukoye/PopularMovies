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

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.movie_details_title)TextView mTitle;

    @BindView(R.id.movie_details_release)TextView mRelease;

    @BindView(R.id.movie_details_rating)TextView mRating;

    @BindView(R.id.movie_details_overview)TextView mOverview;

    @BindView(R.id.movie_details_poster)ImageView mPoster;

    MovieDataObject mMovieDataObject;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
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
