package com.jcedar.visibook.lautech.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.appevents.AppEventsLogger;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.provider.DataContract;
import com.jcedar.visibook.lautech.sync.SyncHelper;
import com.jcedar.visibook.lautech.ui.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashboardActivity extends BaseActivity
        implements   StudentListFragment.Listener, AllStudentListFragment.Listener{

    private Toolbar toolbar;
    private SlidingTabLayout tabs;
    private  Context context = DashboardActivity.this;
    private Set<Fragment> mHomeFragments = new HashSet<>();
    private static final String TAG = DashboardActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("NAAS WNUC");
        setSupportActionBar(toolbar);
        toolbar.setCollapsible(true);
        DesignPagerAdapter adapter = new DesignPagerAdapter(getSupportFragmentManager());
       // ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Resources res = getResources();
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(res.getColor(R.color.tab_selected_strip));
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StudentListFragment(), AccountUtils.getUserChapter(this));
        adapter.addFragment(new AllStudentListFragment(), "ALL NAAS");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onSchoolSelected(long studentId) {
        Intent detailIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = DataContract.StudentsChapter.buildStudentUri(studentId);
        detailIntent.setData(uri);
        startActivity(detailIntent);
    }

    @Override
    public void onFragmentAttached(Fragment fragment) {
        mHomeFragments.add(fragment);
    }

    @Override
    public void onFragmentDetached(Fragment fragment) {
        mHomeFragments.remove(fragment);
    }

    @Override
    public void onAllSelected(long studentId) {
        Intent detailIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = DataContract.Students.buildStudentUri(studentId);
        detailIntent.setData(uri);
        startActivity(detailIntent);
    }


    class DesignPagerAdapter extends FragmentStatePagerAdapter {

        public DesignPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if ( position == 0)
                return StudentListFragment.newInstance(position);
            else
                return AllStudentListFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if ( position == 0)
                return AccountUtils.getUserChapter(context);

            else return "All NAAS";
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_student_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                searchView.setQueryRefinementEnabled(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch ( id ){
            case R.id.action_settings:
            break;
            case R.id.action_update:
                SyncHelper mSyncHelper = new SyncHelper(this);
                mSyncHelper.requestManualSync();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NavigationDrawerFragment.MenuConstants.NAVDRAWER_ITEM_DASHBOARD;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    protected Toolbar getActionBarToolbar() {
        return super.getActionBarToolbar();
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {

        for (Fragment fragment : mHomeFragments) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                if (!fragment.getUserVisibleHint()) {
                    continue;
                }
            }

            return ViewCompat.canScrollVertically(fragment.getView(), -1);
        }

        return false;

    }
}
