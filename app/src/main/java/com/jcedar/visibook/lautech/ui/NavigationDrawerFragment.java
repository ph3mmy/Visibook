package com.jcedar.visibook.lautech.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.helper.PrefUtils;
import com.jcedar.visibook.lautech.ui.view.BezelImageView;
import com.jcedar.visibook.lautech.ui.view.nav.NavDrawerAdapter;
import com.jcedar.visibook.lautech.ui.view.nav.NavDrawerItem;
import com.jcedar.visibook.lautech.ui.view.nav.NavMenuBuilder;


/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {


    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String TAG = NavigationDrawerFragment.class.getSimpleName();
    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;
    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;
    private static final int REQUEST_DEVICE = 2;
    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;
    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private View mCurrentSelectedNavItem;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private NavDrawerAdapter mNavDrawerAdapter;
    private int lastItemChecked;
    private Handler mHandler;
    //private ImageView imageView;
    private BezelImageView imageView;
    private TextView profileText;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView) root.findViewById(R.id.drawer_listview);
        //imageView = (ImageView) root.findViewById(R.id.profile_image1);
        imageView = (BezelImageView) root.findViewById(R.id.profile_image1);
        profileText = (TextView) root.findViewById(R.id.profile_name_text);

        if( ! AccountUtils.isFirstRun(getActivity()) ) {
//            imageView.setImageBitmap(UIUtils.getProfilePic(getActivity()));
            imageView.setImageBitmap(PrefUtils.getPhoto(getActivity()));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity( new Intent(getActivity(), PicViewActivity.class));
                }
            });
        }
        if ( AccountUtils.getUserName( getActivity() ) != null){
            profileText.setText(  AccountUtils.getUserName( getActivity() ) );
        }

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentSelectedNavItem = view;
                selectItem(position);
            }
        });

        mNavDrawerAdapter = new NavDrawerAdapter(getActivity(), android.R.id.text1);
        // list item is navdrawer_item.xml
        mNavDrawerAdapter.setData(getNavDrawerItem());
        mDrawerListView.setAdapter(mNavDrawerAdapter);

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        return root;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */

        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.

       /* if (!PrefUtils.isWelcomeDone(getActivity())) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
            PrefUtils.markWelcomeDone(getActivity());
        }*/

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        //selectDrawerItem(position);

        mCurrentSelectedPosition = position;

        if (mNavDrawerAdapter == null) return;
        NavDrawerItem selectedItem = mNavDrawerAdapter.getItem(position);
        onNavDrawerItemClicked(selectedItem.getId());
    }

    private void selectItem(int position, View view) {


        mCurrentSelectedPosition = position;

        if (mNavDrawerAdapter == null) return;
        NavDrawerItem selectedItem = mNavDrawerAdapter.getItem(position);

        onNavDrawerItemClicked(selectedItem.getId());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            //throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        // but Deji's decided not to implement it.
        /*if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }*/
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    private NavDrawerItem[] getNavDrawerItem() {
        NavMenuBuilder builder = new NavMenuBuilder();

                    builder.addSectionItem(MenuConstants.NAVDRAWER_ITEM_DASHBOARD,
                                    R.string.nav_drawer_item_dashboard,
                                    R.drawable.ic_group, false, true)
                            .addSectionItem(MenuConstants.NAVDRAWER_ITEM_PROFILE,
                                    R.string.nav_drawer_item_profile,
                                    R.drawable.ic_account_balance, false, true)
                            .addSectionItem(MenuConstants.NAVDRAWER_ITEM_ADD_UPDATE,
                                    R.string.nav_drawer_item_add_update,
                                    R.drawable.ic_assessment, false, true);

                            builder.addSection(-3, R.string.nav_drawer_item_about_us)
                             //About should be added to settings
                            .addSectionItem(MenuConstants.NAVDRAWER_ITEM_SETTINGS, R.string.nav_drawer_item_settings,
                                    R.drawable.ic_drawer_settings, true, true)
                            .addSectionItem(MenuConstants.NAVDRAWER_ITEM_SIGN_OUT, R.string.nav_drawer_item_sign_out,
                                    R.drawable.ic_sign_out, true, true);



        return builder.build();
    }

    public void goToNavDrawerItem(int itemId) {
        Intent intent;
        switch (itemId) {
            case MenuConstants.NAVDRAWER_ITEM_DASHBOARD:
                intent = new Intent(getActivity(), DashboardActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;

            case MenuConstants.NAVDRAWER_ITEM_PROFILE:
                intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case MenuConstants.NAVDRAWER_ITEM_ADD_UPDATE:
                intent = new Intent(getActivity(), AddUpdateFragment.class);
                startActivity(intent);
                getActivity().finish();
                break;

            case MenuConstants.NAVDRAWER_ITEM_SETTINGS:
                intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
                break;


            case MenuConstants.NAVDRAWER_ITEM_SIGN_OUT:
                if (!AccountUtils.signOut(getActivity())) { // if not sync
                    getActivity().finish();

                } else {
                    Toast.makeText(getActivity(), "Can't sign you out while sync runs.", Toast.LENGTH_LONG).show();
                }
                ;
                break;



            case MenuConstants.NAVDRAWER_ITEM_INVALID:
            case MenuConstants.NAVDRAWER_ITEM_SEPARATOR:
            case MenuConstants.NAVDRAWER_ITEM_SEPARATOR_SPECIAL:
                //do something
                break;

        }
    }

    public void setAccount() {
        /*try{
            String role = AccountUtils.getRole(getActivity());
            String userName = AccountUtils.getFullName(getActivity());
            String userRole;
            switch (role) {
                case AppSettings.Role.MARKET_OPERATOR:
                    userRole = getString(R.string.role_MO);
                    break;
                case AppSettings.Role.NBET:
                case
           *//* case 2:
                userRole = getString(R.string.role_agency);
                break;
            case 3:
                userRole = getString(R.string.role_customer);
                break;*//*
                default:
                    userRole = null;
                    break;
            }


            TextView roleView = (TextView) mDrawerLayout.findViewById(R.id.profile_role_text);
            roleView.setText(userRole);

            TextView nameView = (TextView) mDrawerLayout.findViewById(R.id.profile_name_text);
            nameView.setText(userName);
        }
        catch(Exception e){
            e.printStackTrace();
        }
*/
        TextView roleView = (TextView) mDrawerLayout.findViewById(R.id.profile_role_text);
        //  roleView.setText(AccountUtils.getRole(getActivity()));
        try {
            if (AccountUtils.getRole(getActivity()) == null) {
                return;
            }
            if (AccountUtils.getRole(getActivity()).equalsIgnoreCase("ServiceProvider")) {
                roleView.setText("Service Provider");
            } else {
                roleView.setText(AccountUtils.getRole(getActivity()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        TextView nameView = (TextView) mDrawerLayout.findViewById(R.id.profile_name_text);
        nameView.setText(AccountUtils.getFullName(getActivity()));
    }

    // This is useful when you want to show what item has been selected from inside the activity
    public void setSelection(int itemId) {
        int position = -1;
        for (NavDrawerItem item : getNavDrawerItem()) {
            position++;

            if (item.getId() == itemId)
                break;
        }
        mDrawerListView.setItemChecked(position, true);
        //TODO: find a better way to do selection
        //formatNavDrawerItem(mDrawerListView.getSelectedView(), itemId, true);
    }

    private void formatNavDrawerItem(View view, int itemId, boolean selected) {
        /*if (isSeparator(itemId)) {
            // not applicable
            return;
        }

        if (view == null) {
            Log.d(TAG, "View is null");
            return;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.navmenuitem_icon);
        TextView titleView = (TextView) view.findViewById(R.id.navmenuitem_label);

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(R.color.navdrawer_text_color_selected) :
                getResources().getColor(R.color.navdrawer_text_color));
        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.navdrawer_icon_tint_selected) :
                getResources().getColor(R.color.navdrawer_icon_tint));*/
    }

    private boolean isSpecialItem(int itemId) {
        return itemId == MenuConstants.NAVDRAWER_ITEM_SETTINGS;
    }

    private boolean isSeparator(int itemId) {
        return itemId == MenuConstants.NAVDRAWER_ITEM_SEPARATOR || itemId == MenuConstants.NAVDRAWER_ITEM_SEPARATOR_SPECIAL;
    }

    private void onNavDrawerItemClicked(final int itemId) {
        BaseActivity activity = (BaseActivity) getActivity();
        if (itemId == activity.getSelfNavDrawerItem()) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (isSpecialItem(itemId)) {
            goToNavDrawerItem(itemId);
        } else {
            // launch the target Activity after a short delay, to allow the close animation to play
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToNavDrawerItem(itemId);
                }
            }, NAVDRAWER_LAUNCH_DELAY);

            //formatNavDrawerItem(mCurrentSelectedNavItem, itemId, true);
        }

        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public interface MenuConstants {
        // symbols for navdrawer items (indices must correspond to array below). This is
        // not a list of items that are necessarily *present* in the Nav Drawer; rather,
        // it's a list of all possible items.
        /*int NAVDRAWER_ITEM_MKT_PARTICIPANT = 0;
        int NAVDRAWER_ITEM_SETTLEMENTS = 1;
        int NAVDRAWER_ITEM_REMIITTANCE = 2;*/
        int NAVDRAWER_ITEM_PROFILE = 3;
        int NAVDRAWER_ITEM_MKT_DEBT = 4;
        int NAVDRAWER_ITEM_SETTINGS = 5;
        int NAVDRAWER_ITEM_ADD_UPDATE = 6;
        int NAVDRAWER_ITEM_ABOUT = 8;
        int NAVDRAWER_ITEM_HELP = 9;
        int NAVDRAWER_ITEM_DASHBOARD = 10;
        int NAVDRAWER_ITEM_ONE_PARTICIPANT = 11;
        int NAVDRAWER_ITEM_SIGN_OUT = 15;
        int NAVDRAWER_ITEM_INVALID = -1;
        int NAVDRAWER_ITEM_SEPARATOR = -2;
        int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;
    }

}
