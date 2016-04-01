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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.appevents.AppEventsLogger;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.provider.DataContract;

import java.util.ArrayList;

public class StudentDetailsActivity extends BaseActivity
        implements StudentListFragment.Listener, StudentDetailsFragment.DetailsListener{

    //ViewPagerAdapter detailsAdapter;
    StudentListFragment mFragment = null;
    private static final String TAG = StudentDetailsActivity.class.getSimpleName();
    public ArrayList<Long> mStudents;
    private ViewPager mPager;
    Uri mSelectedStudent;
    String phoneNumber, emailAddress, name;

    boolean dualPanel;
    public static String ARG_STUDENT_LIST = "ARG_STUDENT_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        dualPanel = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        Intent intent = getIntent();
        mSelectedStudent = intent.getData();
        StudentDetailsFragment fragment = StudentDetailsFragment.newInstance(mSelectedStudent, this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.details, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();

        if ( dualPanel ){
            if (getResources().getConfiguration().smallestScreenWidthDp <= 600) {
                mFragment =
                        (StudentListFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.studentFrag);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                mFragment =
                        (StudentListFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.studentFrag);
            }
        }

       /* if (findViewById(R.id.payItemDetailsPane) != null) {
            mFragment =
                    (StudentDetailsFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.studentDetailFrag);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Intent intent = getIntent();
        if(intent != null){
            mSelectedStudent = intent.getData();
            if( mSelectedStudent == null ) {
                return;
            }
            Bundle payItemBundle = intent.getBundleExtra(ARG_STUDENT_LIST);


            if(payItemBundle != null){
                mStudents = convertToArray(payItemBundle, ARG_STUDENT_LIST);
                Log.d(TAG, "Student id bundle is not null: "
                        + UIUtils.bundle2string(payItemBundle) + "    "+mStudents);
            }else {
                Log.d(TAG, "Student id Bundle is null");
            }
        }

        detailsAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                mStudents.size());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(detailsAdapter);

        int selectedIndex = mStudents.indexOf(
                Long.parseLong(mSelectedStudent.getLastPathSegment()));
        mPager.setCurrentItem(selectedIndex);

*/
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



    public void  fabClicked (View v){
        int id = v.getId();
        switch (id){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_details, menu);
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
    public void onSchoolSelected(long studentId) {
        Log.d(TAG, studentId +" courseId");
        /*mPager.setCurrentItem(mStudents.indexOf(courseId), true);*/
        Uri uri = DataContract.StudentsChapter.buildStudentUri(studentId);

        StudentDetailsFragment fragment = StudentDetailsFragment.newInstance(uri, this);
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

    @Override
    public void getUserData(String phoneNumber, String emailAddress, String name) {
        setPhoneNumber(phoneNumber);
        setEmailAddress(emailAddress);
        setName(name);
    }

 /*   private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final int mSize;

        public ViewPagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
            Log.d( TAG, "Calling "+ViewPagerAdapter.class.getSimpleName());
        }

        @Override
        public Fragment getItem(int position) {
            return StudentDetailsFragment.newInstance(position, StudentDetailsActivity.this);
        }

        @Override
        public int getCount() {
            return mSize;
        }
    }*/


    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.deactivateApp(this);
    }

}
