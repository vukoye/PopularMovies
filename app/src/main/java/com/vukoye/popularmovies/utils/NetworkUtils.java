package com.vukoye.popularmovies.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String IMAGE_AUTHORITY = "image.tmdb.org";

    private static final String API_AUTHORITY = "api.themoviedb.org";

    private static final String VERSION_PATH = "3";

    private static final String TYPE_PATH = "movie";

    private static final String POPULAR_PATH = "popular";

    private static final String TOP_RATED_PATH = "top_rated";

    private static final String API_KEY_QUERY_PARAM = "api_key";

    private static final String IMAGE_SIZE_PATH = "w185";
    private static final String VIDEOS_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";

    public static URL buildTopRatedUrl(String api_key) {
        return buildMoviesUrl(api_key, TOP_RATED_PATH);
    }

    public static URL buildPopularRatedUrl(String api_key) {
        return buildMoviesUrl(api_key, POPULAR_PATH);
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }

        } finally {
            urlConnection.disconnect();
        }
    }

    public static URL buildImageUrl(String imagePath) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("http").authority(IMAGE_AUTHORITY).appendPath("t").appendPath("p").appendPath(IMAGE_SIZE_PATH).appendPath(imagePath).build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static URL buildMoviesUrl(String api_key, String order) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("http")
                         .authority(API_AUTHORITY)
                         .appendPath(VERSION_PATH)
                         .appendPath(TYPE_PATH)
                         .appendPath(order)
                         .appendQueryParameter(API_KEY_QUERY_PARAM, api_key)
                         .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildTrailersUrl(String api_key, String movieId) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("http")
                         .authority(API_AUTHORITY)
                         .appendPath(VERSION_PATH)
                         .appendPath(TYPE_PATH)
                         .appendPath(movieId)
                         .appendPath(VIDEOS_PATH)
                         .appendQueryParameter(API_KEY_QUERY_PARAM, api_key)
                         .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static URL buildReviewsUrl(String api_key, String movieId) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("http")
                         .authority(API_AUTHORITY)
                         .appendPath(VERSION_PATH)
                         .appendPath(TYPE_PATH)
                         .appendPath(movieId)
                         .appendPath(REVIEWS_PATH)
                         .appendQueryParameter(API_KEY_QUERY_PARAM, api_key)
                         .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

}
