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


    private void createTrailersTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " ("
                + MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL UNIQUE, "
                + MovieContract.TrailerEntry.COLUMN_KEY + " TEXT NOT NULL, "
                + MovieContract.TrailerEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + MovieContract.TrailerEntry.COLUMN_SITE + " TEXT NOT NULL, "
                + MovieContract.TrailerEntry.COLUMN_SIZE + " INTEGER DEFAULT 0, "
                + MovieContract.TrailerEntry.COLUMN_TYPE + " TEXT "
                + "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
    }

    private void createReviewsTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " ("
                + MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL UNIQUE, "
                + MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL "
                + "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
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
                + MovieContract.MovieEntry.COLUMN_LAST_UPDATED_TOP_RATED + " INTEGER DEFAULT -1"
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
