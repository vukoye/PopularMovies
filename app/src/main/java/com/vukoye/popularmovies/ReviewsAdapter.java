package com.vukoye.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nemanja on 12/16/16.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(final ReviewsAdapter.ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final View itemView) {
            super(itemView);
        }
    }
}
