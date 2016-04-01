package com.jcedar.visibook.lautech.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.facebook.appevents.AppEventsLogger;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.adapter.RecyclerCursorAdapter;
import com.jcedar.visibook.lautech.provider.DataContract;
import com.jcedar.visibook.lautech.provider.SuggestionProvider;

public class SearchActivity extends BaseActivity
        implements SearchFragment.Listener{

    private static final String TAG = SearchActivity.class.getSimpleName();
    RecyclerView recyclerView;
    RecyclerCursorAdapter resultsCursorAdapter;
    private boolean mTwoPane = false;
    private SearchFragment mSearchFragment;
    private SlidingPaneLayout mSlidingLayout;
    private String mQuery;
    private long mSelectedStudent;
    private Fragment mDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mTwoPane = (findViewById(R.id.large_screen) != null);
        if (mTwoPane) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        mSearchFragment = (SearchFragment) fragmentManager.findFragmentById(
                R.id.fragment_container_master);
        if (mSearchFragment == null) {
            mSearchFragment = new SearchFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container_master, mSearchFragment)
                    .commit();
        }

        mSlidingLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
        if (null != mSlidingLayout) {
            mSlidingLayout.setPanelSlideListener(new SliderListener());
            mSlidingLayout.openPane();
        }

        // set toolbar
        final Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_up);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        }
        onNewIntent(getIntent());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {

        setIntent(intent);
        String query = intent.getStringExtra(SearchManager.QUERY);
        mQuery = query;
        setTitle(Html.fromHtml(getString(R.string.title_search_query, query)));
        Log.e(TAG, mQuery+" query");

        //save query
        SearchRecentSuggestions suggestions =
                new SearchRecentSuggestions(this,
                        SuggestionProvider.AUTHORITY,
                        SuggestionProvider.MODE);
        suggestions.saveRecentQuery(query, null);

        Intent modernIntent
                = new Intent(Intent.ACTION_VIEW, DataContract.Students.buildSearchUri(query));
        Bundle arguments = intentToFragmentArguments(modernIntent);
        Uri uri = modernIntent.getData();
        Log.e(TAG, uri.toString() + " uri");

        mSearchFragment.reloadFromArguments(uri, mQuery);

        //close
        if (!mTwoPane) {
            if (null != mSlidingLayout) {
                if (!mSlidingLayout.isOpen()) {
                    mSlidingLayout.openPane();
                }
            }
        }
    }

    @Override
    public void onListItemSelected(long id) {

        if (!mTwoPane && id == mSelectedStudent) {
            if (null != mSlidingLayout && mSlidingLayout.isOpen()) {
                mSlidingLayout.closePane();
            }
            return;
        }

        AllStudentsDetailsFragment detailFragment
                = AllStudentsDetailsFragment.newInstance(id);
        Bundle argument = new Bundle();

        argument.putLong(AllStudentsDetailsFragment.ARGS_ALL_STUDENT_ID, id);
        detailFragment.setArguments(argument);

        if (mTwoPane) {
            // animate on wide screens
            if (id == mSelectedStudent) return;
            if (TextUtils.isEmpty(String.valueOf(id))) {
                //slide in from left
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left,
                                R.anim.slide_out_right)
                        .replace(R.id.fragment_container_detail, detailFragment)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right,
                                R.anim.slide_out_left)
                        .replace(R.id.fragment_container_detail, detailFragment)
                        .commit();
            }
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_detail, detailFragment)
                    .commit();
        }

        mDetailFragment = detailFragment;

        if (!mTwoPane) {
            if (null != mSlidingLayout) {
                if (mSlidingLayout.isOpen()) {
                    new Handler() {
                    }.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSlidingLayout.closePane();
                        }
                    }, 250);

                }
            }
        }

        mSelectedStudent  = id;

    }

    private class SliderListener extends SlidingPaneLayout.SimplePanelSlideListener {

        public SliderListener() {
            super();
        }

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            super.onPanelSlide(panel, slideOffset);
        }

        @Override
        public void onPanelOpened(View panel) {
            super.onPanelOpened(panel);
            //setTitle(Html.fromHtml(getString(R.string.title_search_query, mQuery)));
        }

        @Override
        public void onPanelClosed(View panel) {
            super.onPanelClosed(panel);
            //setTitle(Html.fromHtml(getString(R.string.title_search_query, mQuery)));
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.deactivateApp( this);
    }


}
