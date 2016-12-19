package com.vukoye.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vukoye.popularmovies.data.MovieContract;

/**
 * Created by nemanja on 12/16/16.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {


    private Cursor mCursor;

    private Context mContext;

    public ReviewsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_list_item, parent, false);
        return new ReviewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewsAdapter.ViewHolder holder, final int position) {
        int authorIndex = mCursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR);
        int contentIndex = mCursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT);
        if (mCursor.moveToPosition(position)) {
            holder.reviewAuthor.setText(mCursor.getString(authorIndex));
            holder.reviewContent.setText(mCursor.getString(contentIndex));
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
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView reviewAuthor;
        TextView reviewContent;

        public ViewHolder(final View itemView) {
            super(itemView);
            reviewAuthor = (TextView) itemView.findViewById(R.id.review_author);
            reviewContent = (TextView) itemView.findViewById(R.id.review_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            //nvtd implent expand collapse on click
        }
    }
}
