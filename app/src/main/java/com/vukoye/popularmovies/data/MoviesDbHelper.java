package com.vukoye.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MoviesDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createMoviesTable(sqLiteDatabase);
        createTrailersTable(sqLiteDatabase);
        createReviewsTable(sqLiteDatabase);
    }

    private void createReviewsTable(SQLiteDatabase sqLiteDatabase) {
        //nvtd
    }

    private void createTrailersTable(SQLiteDatabase sqLiteDatabase) {
        //nvtd
    }

    private void createMoviesTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " ("
                + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE +  " REAL NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0, "
                + MovieContract.MovieEntry.COLUMN_POPULAR_C + " INTEGER DEFAULT 10000, "
                + MovieContract.MovieEntry.COLUMN_LAST_UPDATED_POPULAR + " INTEGER DEFAULT -1, "
                + MovieContract.MovieEntry.COLUMN_TOP_RATED_C + " INTEGER DEFAULT 10000, "
                + MovieContract.MovieEntry.COLUMN_LAST_UPDATED_TOP_RATED+ " INTEGER DEFAULT -1"
                + "); ";
                //+ " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int i, final int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
