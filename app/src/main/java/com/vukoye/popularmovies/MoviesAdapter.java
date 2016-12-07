package com.vukoye.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vukoye.popularmovies.data.MovieDataObject;
import com.vukoye.popularmovies.utils.NetworkUtils;

import java.net.URL;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private MovieDataObject[] mMoviesData;

    private Context mContext;

    public interface MoviesAdapterOnClickHandler {
        void onClick(int position);
    }

    private final MoviesAdapterOnClickHandler mClickHandler;

    public MoviesAdapter(final MoviesAdapterOnClickHandler clickHandler, Context context) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false);
        int width = parent.getMeasuredWidth();
        int desiredHeight = width/2*277/185;
        //view.setMinimumHeight(desiredHeight);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MovieDataObject movieDataObject= mMoviesData[position];
        URL url = NetworkUtils.buildImageUrl(movieDataObject.getPosterPath());
        Picasso.with(mContext).load(url.toString()).into(holder.movieImage);
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) return 0;
        return mMoviesData.length;
    }

    public void setMoviesData(MovieDataObject[] moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView movieImage;

        public ViewHolder(final View itemView) {
            super(itemView);
            movieImage = (ImageView)itemView.findViewById(R.id.movie_list_item_image);
            movieImage.setOnClickListener(this);

        }

        @Override
        public void onClick(final View view) {
            mClickHandler.onClick(getAdapterPosition());
        }
    }

}
