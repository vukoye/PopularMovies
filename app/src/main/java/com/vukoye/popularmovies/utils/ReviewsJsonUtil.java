package com.vukoye.popularmovies.utils;


import android.content.ContentValues;
import android.content.Context;

import com.vukoye.popularmovies.MoviesPreferences;
import com.vukoye.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ReviewsJsonUtil {
    //12-17 20:17:14.581 3745-3837/com.vukoye.popularmovies D/DownloadMoviesData: {"id":329865,"page":1,"results":[{"id":"584312a4925141676400049c","author":"Smashing UK Productions","content":"Prior to approaching this film, a word of warning that it is what many like to call a \"thinking person's sci-fi\". If you're going to watch this, I beg that you dedicate your utmost attention to it, as it is truly one rewarding experiences, one of the smartest, most well-constructed science fiction marvels of recent years. 'Arrival' is Villeneuve's magnum opus.\r\n\r\nFirstly, to put your mind at ease, I won't be analysing the plot, thus avoiding the use of spoilers. This decade, Villeneuve has crafted some fantastic works of art in the form of 'Prisoners', 'Sicario' and now this science fiction gem, and here's hoping his career further develops with more movie masterpieces coming our way. In a world where mysteries remain and the possibility of extraterrestrial life still stands unanswered, 'Arrival' approaches this with it's cliche-free take on the genre.\r\n\r\nThe relatively unknown Bradford Young provides the film with some of the most stunning cinematography ever conceived, taking advantage of the twilight hour to give the film its somewhat unique look, supported magnificently by Icelandic composer Jóhann Jóhannsson whose score is both haunting and beautiful. If you're someone looking for a science-fiction tale that keeps you guessing and thinking throughout, with fantastic performances, cinematography, music and near-flawless direction, then 'Arrival' is the film for you. The masterpiece of 2016!","url":"https://www.themoviedb.org/review/584312a4925141676400049c"},{"id":"5844cdd89251416a7400fc24","author":"Gimly","content":"Denis Villenueve offers a great film, but one that is exactly what the trailer put forth, this is not a Sci-Fi War film waiting to happen, or a modern-day horror. It's a character-driven piece about politics, life, humanity and communication.\r\n\r\n_Final rating:★★★½ - I strongly recommend you make the time._","url":"https://www.themoviedb.org/review/5844cdd89251416a7400fc24"}],"total_pages":1,"total_results":2}



     static final String RESULTS = "results";
    static final String ID = "id";
    static final String AUTHOR = "author";
    static final String CONTENT = "content";
    public static ContentValues[] getContentValuesFromJson(Context context, String jsonStr, final String movieId) throws JSONException {

        JSONObject moviesJson = new JSONObject(jsonStr);
        JSONArray reviewsArray = moviesJson.getJSONArray(RESULTS);
        ContentValues[] contentValuesArray = new ContentValues[reviewsArray.length()];
        JSONObject jsonReview;
        if (reviewsArray.length() > 0) {
            Calendar calendar = Calendar.getInstance();
            long currentTime = calendar.getTimeInMillis();
            MoviesPreferences.setLastReviewsUpdate(context, currentTime);
        }
        ContentValues cv;
        for (int i = 0; i < reviewsArray.length(); i++) {
            jsonReview = reviewsArray.getJSONObject(i);
            cv = new ContentValues();
            cv.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
            cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID,jsonReview.getString(ID));
            cv.put(MovieContract.ReviewEntry.COLUMN_AUTHOR,jsonReview.getString(AUTHOR));
            cv.put(MovieContract.ReviewEntry.COLUMN_CONTENT,jsonReview.getString(CONTENT));
            contentValuesArray[i] = cv;
        }
        return contentValuesArray;
    }

}
