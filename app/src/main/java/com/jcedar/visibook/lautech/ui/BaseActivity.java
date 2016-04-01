package com.jcedar.visibook.lautech.ui;

import android.accounts.Account;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.provider.DataContract;
import com.jcedar.visibook.lautech.ui.view.MultiSwipeRefreshLayout;

import java.util.ArrayList;

//import com.google.android.gcm.GCMRegistrar;
//import com.jcedar.visibook.lautech.gcm.ServerUtilities;
//import com.jcedar.visibook.lautech.sync.SyncHelper;
//import com.jcedar.visibook.lautech.util.PrefUtils;

//import static com.jcedar.visibook.lautech.helper.LogUtils.LOGI;


public abstract class BaseActivity extends ActionBarActivity implements
        MultiSwipeRefreshLayout.CanChildScrollUpCallback {

    protected static final String NAIRA = "\u20A6";
    protected static final String UNIT = "KWh";
    private static final String TAG = BaseActivity.class.getSimpleName();
    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;
    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;
    // SwipeRefreshLayout allows the user to swipe the screen down to trigger a manual refresh
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ViewGroup mDrawerItemsListContainer;
    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;
    private boolean mManualSyncRequest;
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        @Override
        public void onStatusChanged(int which) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String accountName = AccountUtils.getChosenAccountName(BaseActivity.this);
                    if (TextUtils.isEmpty(accountName)) {
                        onRefreshingStateChanged(false);
                        mManualSyncRequest = false;
                        return;
                    }

                    Account account = new Account(accountName, getResources().getString(R.string.account_type));
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, DataContract.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, DataContract.CONTENT_AUTHORITY);
                    if (!syncActive && !syncPending) {
                        mManualSyncRequest = false;
                    }
                    onRefreshingStateChanged(syncActive || (mManualSyncRequest && syncPending));
                }
            });
        }
    };
    // handle to our sync observer (that notifies us about changes in our sync state)
    private Object mSyncObserverHandle;
    // asynctask that performs GCM registration in the background
    private AsyncTask<Void, Void, Void> mGCMRegisterTask;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    // Navigation drawer
    private DrawerLayout mDrawerLayout;
    private CharSequence mTitle;
    private Handler mHandler;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;

    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
     */
    protected static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mHandler = new Handler();

        // set orientation for screen sizes
       /* if (getResources().getConfiguration().smallestScreenWidthDp <= 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }*/
        if(!isFinishing()) {
            registerGCMClient();
        }

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Long> convertToArray(Bundle bundle, String key) {
        ArrayList<Long> items = new ArrayList<Long>();
        int n = 0;
        while (true) {
            long item = bundle.getLong(key + Integer.toString(n++), -1);
            // terminate once we cant retrieve items
            if (item == -1) break;
            items.add(item);
        }
        return items;
    }

    /*private void trySetupSwipeRefresh() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(
                    R.color.refresh_progress_1,
                    R.color.refresh_progress_2,
                    R.color.refresh_progress_3,
                    R.color.refresh_progress_1);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestDataRefresh();
                }
            });

            if (mSwipeRefreshLayout instanceof MultiSwipeRefreshLayout) {
                MultiSwipeRefreshLayout mswrl = (MultiSwipeRefreshLayout) mSwipeRefreshLayout;
                mswrl.setCanChildScrollUpCallback(this);
            }
        }
    }*/

    protected void onRefreshingStateChanged(boolean refreshing) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    protected void enableDisableSwipeRefresh(boolean enable) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enable);
        }
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return true;
    }

    protected void requestDataRefresh() {
        Account activeAccount = AccountUtils.getChosenAccount(this);
        ContentResolver contentResolver = getContentResolver();
        if (contentResolver.isSyncActive(activeAccount, DataContract.CONTENT_AUTHORITY)) {
            Log.d(TAG, "Ignoring manual sync request because a sync is already in progress.");
            return;
        }
        mManualSyncRequest = true;
        Log.d(TAG, "Requesting manual data refresh.");
        //SyncHelper.requestManualSync(activeAccount);
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {

        return NavigationDrawerFragment.MenuConstants.NAVDRAWER_ITEM_INVALID;
    }

    private void trySetupNavDrawer() {

        int selfItem = getSelfNavDrawerItem();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }

        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.theme_accent_1_light));

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        if (selfItem == NavigationDrawerFragment.MenuConstants.NAVDRAWER_ITEM_INVALID) {
            // do not show a nav drawer
            if (mNavigationDrawerFragment != null) {
                // ((ViewGroup) mNavigationDrawerFragment.getParent()).removeView(navDrawer);
            }
            mDrawerLayout = null;
            return;
        }

        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
            mNavigationDrawerFragment.setAccount();
            // what nav drawer item should be selected?
            mNavigationDrawerFragment.setSelection(getSelfNavDrawerItem());

        }

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //clear invoice count and notifications
        //clearNotification();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();

        // Watch for sync state changes
        mSyncStatusObserver.onStatusChanged(0);
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);

        // reset selection to correct item when fragment becomes visible again
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.setSelection(getSelfNavDrawerItem());
        }
        AppEventsLogger.activateApp(this);
    }

/*    private void clearNotification() {
        PrefUtils.setDebitBatchApproval(this, 0);
        PrefUtils.setCreditBatchApproval(this, 0);
        PrefUtils.setSettlementApproval(this, 0);
        PrefUtils.setCreditBatch(this, 0);
        PrefUtils.setDebitBatch(this, 0);
        PrefUtils.setSettlement(this, 0);
        PrefUtils.setAccounts(this, 0);
        PrefUtils.setWorkflow(this, 0);
        PrefUtils.setParticipants(this, 0);
    }*/

    @Override
    public void onStart() {
        super.onStart();
        //EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }

        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
       // trySetupSwipeRefresh();
        trySetupNavDrawer();
        /*View mainContent = findViewById(R.id.container);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            Log.w(TAG, "No view with ID main_content to fade in.");
        }*/
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            super.setTitle(mTitle); // was getting a stackover flow error from recursion
        // then decided to call super
    }


    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed(); // is this really needed
            if (getSelfNavDrawerItem() == NavigationDrawerFragment.MenuConstants.NAVDRAWER_ITEM_INVALID) {
                super.onBackPressed();
            } else if (getSelfNavDrawerItem() == NavigationDrawerFragment.MenuConstants.NAVDRAWER_ITEM_DASHBOARD) {
                finish();
            } else {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            }
        }
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    private void registerGCMClient() {

//        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
//        gcm.register()
      /*  GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        //Reg on Google
        final String regId = GCMRegistrar.getRegistrationId(this);

        if (TextUtils.isEmpty(regId)) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, AppSettings.GCM_SENDER_ID);

        } else {
            // Device is already registered on GCM, needs to check if it is
            // registered on our server as well.
            if (ServerUtilities.isRegisteredOnServer(this)) {
                // Skips registration.
                LOGI(TAG, "Already registered on the C2DM server");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                mGCMRegisterTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered = ServerUtilities.register(BaseActivity.this, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(BaseActivity.this);
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mGCMRegisterTask = null;


                    }
                };
                mGCMRegisterTask.execute(null, null, null);
            }
            Log.d(TAG, "Device's registration id is " + regId);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mGCMRegisterTask != null) {
            Log.d(TAG, "Cancelling GCM registration task.");
            mGCMRegisterTask.cancel(true);
        }

        profileTracker.stopTracking();

        /*try {
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.w(TAG, "C2DM unregistration error", e);
        }*/
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
