package com.jcedar.visibook.lautech.ui;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.helper.PrefUtils;
import com.jcedar.visibook.lautech.helper.UIUtils;

public class ProfileActivity extends BaseActivity {

    private Toolbar toolbar;
    private ImageView imageView;
    private TextView fullName, emailView, dobView, courseView, numberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(AccountUtils.getUserName(this));
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.my_profile_image);
        fullName = (TextView) findViewById(R.id.fullNameProfile);
        emailView = (TextView) findViewById(R.id.tvEmailProfile);
        dobView = (TextView ) findViewById(R.id.tvDateOfBirthProfile);
        courseView = (TextView) findViewById(R.id.tvCourseSchool);
        numberView = (TextView) findViewById(R.id.tvPhoneNumberProfile);

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(new Intent(ProfileActivity.this, PicViewActivity.class));
                return false;
            }
        });
        Drawable d = new BitmapDrawable(getResources(), PrefUtils.getPhoto(this));

        toolbarLayout.setStatusBarScrim(d);
        toolbarLayout.setStatusBarScrimColor(UIUtils.getDominantColor(PrefUtils.getPhoto(this)));
        //imageView.setImageBitmap(UIUtils.getProfilePic(this));
        fullName.setText(AccountUtils.getUserName(this));
        emailView.setText(
                AccountUtils.getUserEmail(this)
                        != null ?
                        AccountUtils.getUserEmail(this) : " ");
        dobView.setText(
                AccountUtils.getUserDOB(this) != null
                        ? AccountUtils.getUserDOB(this) :
                        " ");
        courseView.setText(
                AccountUtils.getUserCourse(this) != null
                        ? AccountUtils.getUserCourse(this)
                        : " "
                + "( "
                + AccountUtils.getUserChapter(this) != null
                ? AccountUtils.getUserChapter(this)
                : " " +" )");
        numberView.setText(AccountUtils.getUserPhoneNumber(this) != null ? AccountUtils.getUserPhoneNumber(this) : " ");

       /* int colorr = UIUtils.getDominantColor( UIUtils.getProfilePic(this));
        toolbar.setBackgroundColor(colorr);*/

        //School shld contain Chapter and Course
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

//    @Override
//    protected int getSelfNavDrawerItem() {
//        return NavigationDrawerFragment.MenuConstants.NAVDRAWER_ITEM_PROFILE;
//    }
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
