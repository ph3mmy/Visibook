package com.jcedar.visibook.lautech.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.provider.DataContract;

import java.util.ArrayList;

public class AllStudentDetailsActivity extends BaseActivity
            implements AllStudentListFragment.Listener{

    public static final String ARG_ALL_LIST = "ARG_ALL_LIST";
    private static final String TAG = AllStudentDetailsActivity.class.getSimpleName();
    public ArrayList<Long> mStudents;
    AllStudentListFragment mHomeFragment = null;
    private ViewPager mPager;
    //private AllStudentPagerAdapter mPagerAdapter;
    Uri mSelectedStudent;
    private boolean dualPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_student_details);

        dualPanel = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        Intent intent = getIntent();
        mSelectedStudent = intent.getData();
        AllStudentsDetailsFragment fragment = AllStudentsDetailsFragment.newInstance(mSelectedStudent, this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.details, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();

        if ( dualPanel ){
            if (getResources().getConfiguration().smallestScreenWidthDp <= 600) {
                mHomeFragment =
                        (AllStudentListFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.studentFrag);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                mHomeFragment =
                        (AllStudentListFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.studentFrag);
            }
        }

        final Toolbar toolbar = getActionBarToolbar();
        if(toolbar == null) return;
        toolbar.setNavigationIcon(R.drawable.ic_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle("Details");
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_student_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAllSelected(long studentId) {
        //mPager.setCurrentItem(mStudents.indexOf(studentId), true);
        Uri uri = DataContract.Students.buildStudentUri(studentId);

        AllStudentsDetailsFragment fragment = AllStudentsDetailsFragment.newInstance(uri, this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.details, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFragmentAttached(Fragment fragment) {

    }

    @Override
    public void onFragmentDetached(Fragment fragment) {

    }

    /*private class AllStudentPagerAdapter extends FragmentStatePagerAdapter {


        private final int mSize;

        public AllStudentPagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
            Log.d(TAG, "Calling AllStudentPagerAdapter");
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "Calling Fragment");
            return AllStudentsDetailsFragment.newInstance(position, AllStudentDetailsActivity.this);
        }

        @Override
        public int getCount() {
            return mSize;
        }
    }*/
}
