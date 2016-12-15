package com.vukoye.popularmovies;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.vukoye.popularmovies.data.MoviesDbHelper;

import org.junit.runner.RunWith;

/**
 * Created by nemanja on 12/12/16.
 */

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private final Context mContext = InstrumentationRegistry.getTargetContext();
    private final Class mDbHelperClass = MoviesDbHelper.class;
}
