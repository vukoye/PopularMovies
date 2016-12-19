package com.vukoye.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vukoye.popularmovies.data.MovieContract;
import com.vukoye.popularmovies.data.TrailerDataObject;

import java.net.URL;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    private Cursor mCursor;

    private Context mContext;

    public interface TrailersAdapterOnClickHandler {
        void onClick(String key);
    }

    private final TrailersAdapterOnClickHandler mClickHandler;

    public TrailersAdapter(final TrailersAdapterOnClickHandler clickHandler, Context context) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_list_item, parent, false);
//        int width = parent.getMeasuredWidth();
//        int desiredHeight = width / 2 * 277 / 185;
        //view.setMinimumHeight(desiredHeight);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int keyIndex = mCursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_KEY);
        int nameIndex = mCursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_NAME);
        if (mCursor.moveToPosition(position)) {
            holder.itemView.setTag(mCursor.getString(keyIndex));
            holder.trailerName.setText(mCursor.getString(nameIndex));
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

        TextView trailerName;

        public ViewHolder(final View itemView) {
            super(itemView);
            trailerName = (TextView) itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            mClickHandler.onClick( (String)view.getTag());
        }
    }
}
