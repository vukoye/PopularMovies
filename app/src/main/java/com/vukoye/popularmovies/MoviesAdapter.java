package com.vukoye.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vukoye.popularmovies.data.MovieContract;
import com.vukoye.popularmovies.utils.NetworkUtils;

import java.net.URL;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private Cursor mCursor;

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
        int id = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int posterPathIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        int titleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        holder.movieImage.setVisibility(View.VISIBLE);
        holder.movieName.setVisibility(View.GONE);
        if (mCursor.moveToPosition(position)) {
            URL url = NetworkUtils.buildImageUrl(mCursor.getString(posterPathIndex));
            Picasso.with(mContext).load(url.toString()).into(holder.movieImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            holder.movieName.setVisibility(View.VISIBLE);
                            holder.movieImage.setVisibility(View.GONE);
                        }
                    });
                    holder.movieImage.setContentDescription(mCursor.getString(titleIndex));
            holder.movieImage.setTag(mCursor.getInt(id));
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView movieImage;
        TextView movieName;

        public ViewHolder(final View itemView) {
            super(itemView);
            movieImage = (ImageView)itemView.findViewById(R.id.movie_list_item_image);
            movieName = (TextView)itemView.findViewById(R.id.movie_list_item_name);
            movieImage.setOnClickListener(this);

        }

        @Override
        public void onClick(final View view) {
            mClickHandler.onClick((int)view.getTag());
        }
    }

}
