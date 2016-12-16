package com.vukoye.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by nemanja on 12/12/16.
 */

public class MovieContentProvider extends ContentProvider {

    public static final int MOVIES = 100;

    public static final int MOVIES_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String TAG = MovieContentProvider.class.getSimpleName();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }

    MoviesDbHelper mMoviesDbHelper;

    @Override
    public boolean onCreate() {
        mMoviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        final SQLiteDatabase db = mMoviesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor = null;
        switch (match) {
            case MOVIES:
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String selection1 = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
                String[] selectionArgs1 = new String[]{id};
                Log.d(TAG, "query: for moviewith id: " + id);
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection1,
                        selectionArgs1,
                        null,
                        null,
                        sortOrder);

                break;
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(final Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(final Uri uri, final ContentValues contentValues) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        Uri returnUri = null;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    //no item will be added if exception occurs
    @Override
    public int bulkInsert(final Uri uri, final ContentValues[] values) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        db.beginTransaction();
        int numInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                try {
                    for (ContentValues cv : values) {
                        long newID = db.replace(MovieContract.MovieEntry.TABLE_NAME, null, cv);
                        Log.d(TAG, "insert: " + cv.get(MovieContract.MovieEntry.COLUMN_TITLE));
                        if (newID < 0) {
                            throw new SQLException("Failed to insert row into " + uri);
                        }
                    }
                    db.setTransactionSuccessful();
                    getContext().getContentResolver().notifyChange(uri, null);
                    numInserted = values.length;
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return numInserted;
    }

    @Override
    public int delete(final Uri uri, final String s, final String[] strings) {
        return 0;
    }

    @Override
    public int update(final Uri uri, final ContentValues contentValues, final String s, final String[] strings) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        int updated = 0;
        switch (sUriMatcher.match(uri)) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String where = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
                String[] whereArgs = new String[] {id};
                updated = db.update(MovieContract.MovieEntry.TABLE_NAME, contentValues, where, whereArgs);
        }
        if (updated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updated;
    }
}
