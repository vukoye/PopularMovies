package com.vukoye.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by nemanja on 12/12/16.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.vukoye.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";



    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id"; //id from the api

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_POPULARITY = "popularity";

        public static final String COLUMN_FAVORITE = "favorite";

        // added for caching

        public static final String COLUMN_TOP_RATED_C = "top_rated";

        public static final String COLUMN_POPULAR_C = "popular";

        public static final String COLUMN_LAST_UPDATED_POPULAR = "last_update_popular";

        public static final String COLUMN_LAST_UPDATED_TOP_RATED = "last_update_top_rated";



        //not needed for now

        public static final String COLUMN_ADULT = "adult";

        public static final String COLUMN_GENRE_IDS = "genre_ids";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";


        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";


        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";





        public static final String COLUMN_VOTE_COUNT = "vote_count";


        public static final String COLUMN_HAS_VIDEO = "has_video";


        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
    }

    public static final class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_MOVIE_ID = "movie_id"; //id from the api

    }

    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_MOVIE_ID = "movie_id"; //id from the api

        public static final String COLUMN_TRAILER_ID = "trailer_id"; //string

        public static final String COLUMN_KEY = "key";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_SITE = "site";

        public static final String COLUMN_SIZE = "size";

        public static final String COLUMN_TYPE = "type";

    }


}
