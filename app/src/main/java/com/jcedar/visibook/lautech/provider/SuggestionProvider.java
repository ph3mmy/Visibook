package com.jcedar.visibook.lautech.provider;

import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Afolayan on 22/1/2016.
 */
public class SuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "com.jcedar.visibook.lautech.provider.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String sel,
                        String[] selArgs, String sortOrder) {
        Cursor recentCursor = super.query(uri, projection, sel, selArgs,
                sortOrder);
        return recentCursor;
    }

    @Override
    public boolean onCreate() {
        return super.onCreate();
    }

}
