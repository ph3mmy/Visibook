package com.jcedar.visibook.lautech.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.gcm.GcmIntentServices;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.helper.PrefUtils;
import com.jcedar.visibook.lautech.provider.DataContract;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by oluwafemi.bamisaye on 3/8/2016.
 */
public class NewDashBoardActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, AllStudentListFragment.Listener,
        AddUpdateFragment.Listener, ProfileFragment.Listener
{
    private static final String TAG = NewDashBoardActivity.class.getName();
    //Defining Variables
    private static Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private GoogleApiClient mGoogleApiClient;
    private Set<Fragment> mHomeFragments = new HashSet<>();
    private ActionBarDrawerToggle drawerToggle;

    public static Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }*/

        setContentView(R.layout.activity_new_dashboard);

//        mGoogleApiClient = new GoogleApiClient(this);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.inflateHeaderView(R.layout.drawer_header);


        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        drawerLayout.setDrawerListener(drawerToggle);


        //initialize elements of the drawer
        ImageView profilePhoto = (ImageView) headerView.findViewById(R.id.profile_image);
        TextView username = (TextView) headerView.findViewById(R.id.username);
        TextView email = (TextView) headerView.findViewById(R.id.email);
        TextView roleString = (TextView) headerView.findViewById(R.id.roleString);

        //set drawer Item
        //String photoString = PrefUtils.getPhoto(this);

        //Log.d(TAG, " Handle signIn email of user" + photoString);
        Bitmap decodedImg = PrefUtils.getPhoto(this);
        //Bitmap decodedImg = PrefUtils.decodeBase64(photoString);
        profilePhoto.setImageBitmap(decodedImg);

        //set UserName
        final String user = PrefUtils.getPersonal(this);
        username.setText(user);
        username.setTextColor(getResources().getColor(R.color.white));

        //set User Email
        String mailTxt = PrefUtils.getEmail(this);
        email.setText(mailTxt);
        email.setTextColor(getResources().getColor(R.color.white));

        //set User Role
//        String roleTxt = PrefUtils.getRole(this);
        roleString.setText(R.string.member);
        roleString.setTextColor(getResources().getColor(R.color.white));

        AllStudentListFragment aslt;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();


        if( getIntent().getExtras() != null ){ //coming from GcmIntentService
            String[] ids = getIntent().getStringArrayExtra(GcmIntentServices.BUNDLE_ID_ARRAY);
            Log.e(TAG, "ids "+ Arrays.toString(ids));

            aslt =  AllStudentListFragment.newInstance(ids);
        } else {
            aslt = new AllStudentListFragment();
        }

        ft.add(R.id.frame, aslt);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();


        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if(item.isChecked()) item.setChecked(false);
                else item.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                Intent intent;
                Fragment fragment = null;
                Class fragmentClass = AllStudentListFragment.class;

                //Check to see which item was being clicked and perform appropriate action
                switch (item.getItemId()){

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_contactList:
                        fragmentClass = AllStudentListFragment.class;
                        break;
                    case R.id.nav_excos:
                        Toast.makeText(getApplicationContext(),"Excos Selected",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_profile:
                        toolbar.setVisibility(View.GONE);
                        fragmentClass = ProfileFragment.class;
                        break;

                    case R.id.nav_update:
                        toolbar.setVisibility(View.VISIBLE);
                        fragmentClass = AddUpdateFragment.class;
                        break;

                    case R.id.nav_settings:
                        intent = new Intent(NewDashBoardActivity.this, Settings.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_logout:
                        if (!AccountUtils.signOut(NewDashBoardActivity.this)) { // if not sync
                            finish();

                        } else {
                            Toast.makeText(NewDashBoardActivity.this, "Can't sign you out while sync runs.", Toast.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        fragmentClass = AllStudentListFragment.class;
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        break;

                }
                try{
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception  e) {
                    e.printStackTrace();
                }

                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.frame, fragment).commit();

                return true;
            }
        });
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, R.mipmap.ic_menu, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    private void signOutUser() {
/*        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
//                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });*/
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onAllSelected(long studentId) {
        Intent detailIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = DataContract.Students.buildStudentUri(studentId);
        detailIntent.setData(uri);
        startActivity(detailIntent);
    }

    @Override
    public void onFragmentDetached(Fragment fragment) {
        mHomeFragments.remove(fragment);
    }

    @Override
    public void onFragmentAttached(Fragment fragment) {
        mHomeFragments.add(fragment);
    }

/*    @Override
    protected void onPause() {

        super.onPause();

        getSupportFragmentManager().findFragmentByTag("MyFragment")
                .setRetainInstance(true);
    }

    @Override
    protected void onResume() {

        super.onResume();

        getSupportFragmentManager().findFragmentByTag("MyFragment")
                .getRetainInstance();

    }*/

}
