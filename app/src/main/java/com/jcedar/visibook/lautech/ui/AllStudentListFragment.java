package com.jcedar.visibook.lautech.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.adapter.RecyclerCursorAdapterAll;
import com.jcedar.visibook.lautech.provider.AndroidDatabaseManager;
import com.jcedar.visibook.lautech.provider.DataContract;

import java.util.Arrays;

import static com.jcedar.visibook.lautech.ui.NewDashBoardActivity.getToolbar;

public class AllStudentListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    protected static final String NAIRA = "\u20A6";
    private static final String TAG = AllStudentListFragment.class.getSimpleName();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_IDS = "ids";
    private static final String SEARCH_KEY = "SEARCH_KEY";

    // TODO: Rename and change types of parameters
    private int mPosition;
    private String mParam2;
    private TextView tvError;
    private Bundle mHomeBundle = Bundle.EMPTY;
    private String _POSITION = "position";
    private String LOADER_KEY = "loader_key";
    private Listener mCallback;
    static String context;

    RecyclerView recyclerView;
    RecyclerCursorAdapterAll resultsCursorAdapter;
    View rootView;
    String[] idsToLoad;

    private static final int NORMAL_LOADER_ID = 1;
    private static final int BIRTHDAY_LOADER_ID = 2;
    private static final int SEARCH_LOADER_ID = 3;

    static int presentId;

    public static AllStudentListFragment newInstance(int position) {
        AllStudentListFragment fragment = new AllStudentListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);


        fragment.setArguments(args);
        return fragment;
    }
    public static AllStudentListFragment newInstance(String[] ids) {
            AllStudentListFragment fragment = new AllStudentListFragment();
            Bundle args = new Bundle();
            args.putStringArray(ARG_IDS, ids);


            fragment.setArguments(args);
            return fragment;
        }

    public AllStudentListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Initialize loader
        //getLoaderManager().initLoader(NORMAL_LOADER_ID, null, this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( savedInstanceState != null){
            presentId = savedInstanceState.getInt(LOADER_KEY);
        } else {
            presentId = NORMAL_LOADER_ID;
        }

        if (getArguments() != null) {
            idsToLoad = getArguments().getStringArray(ARG_IDS);
            Log.e(TAG, "ids " + Arrays.toString(idsToLoad));
            getLoaderManager().initLoader(BIRTHDAY_LOADER_ID, null, this);
        }


        context = getActivity().getClass().getSimpleName();

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(LOADER_KEY, presentId);
        Log.e(TAG, "present id " + presentId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if( rootView != null ){
            if(  rootView.getParent() != null ){
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        ((ViewGroup) rootView.getParent()).removeView(rootView);
                    }
                });

            }
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        tvError = (TextView) rootView.findViewById(R.id.tvErrorMag);


        if(getToolbar() != null)
            getToolbar().setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) rootView.findViewById( R.id.recyclerview );
        resultsCursorAdapter = new RecyclerCursorAdapterAll( getActivity() );

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setLayoutManager(new WrappingLinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        recyclerView.setAdapter(resultsCursorAdapter);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getActivity().setTitle("Lautech");
            }
        });
        resultsCursorAdapter.setOnItemClickListener(new RecyclerCursorAdapterAll.OnItemClickListener() {
            @Override
            public void onItemClicked(Cursor data) {

                long Id = data.getLong(
                        data.getColumnIndex(DataContract.Students._ID));

                // add position to bundle
                //mHomeBundle.putInt(_POSITION, position);
                mCallback.onAllSelected(Id);


            }
        });

        return rootView;

    }



    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId() == R.id.action_update){
            startActivity( new Intent(getActivity(), AndroidDatabaseManager.class));
        }
        return true;
    }

    interface Listener {
        void onAllSelected(long courseId);
        void onFragmentDetached(Fragment fragment);
        void onFragmentAttached(Fragment fragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof Listener) {
            mCallback = (Listener) activity;
            mCallback.onFragmentAttached(this);
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement fragments listener");
        }
        activity.getContentResolver().registerContentObserver(
                DataContract.Students.CONTENT_URI, true, mObserver);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        super.onDetach();
        if (getActivity() instanceof Listener) {
            ((Listener) getActivity()).onFragmentDetached(this);
        }
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
    }

    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (!isAdded()) {
                return;
            }
            getLoaderManager().restartLoader(presentId, null, AllStudentListFragment.this);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_all_student_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(SEARCH_KEY, query);

                    getLoaderManager().restartLoader(SEARCH_LOADER_ID,
                            bundle, AllStudentListFragment.this);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(SEARCH_KEY, newText);

                    getLoaderManager().restartLoader(SEARCH_LOADER_ID,
                            bundle, AllStudentListFragment.this);
                }
                return false;
            }
        });
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.Students.CONTENT_URI;

        switch (id){
            case NORMAL_LOADER_ID:
                presentId = NORMAL_LOADER_ID;
                return new CursorLoader(
                    getActivity(),
                    uri,
                    DataContract.Students.PROJECTION_ALL,
                    null,    // selection
                    null,           // arguments
                    DataContract.Students.NAME + " ASC"
            );
            case BIRTHDAY_LOADER_ID:{
                presentId = BIRTHDAY_LOADER_ID;

                String selection =  DataContract.Students._ID+ " in (";
                for(int i=0; i<idsToLoad.length; i++)
                    selection += "?, ";

                selection = selection.substring(0, selection.length() - 2) + ")";


                CursorLoader cursor = new CursorLoader(
                        getActivity(),
                        uri,
                        DataContract.Students.PROJECTION_ALL,
                        selection,    // selection
                        idsToLoad,           // arguments
                        null
                        //DataContract.Students.NAME + " ASC"
                );
                String cc = cursor.getSelection();
                Log.e(TAG, "query =="+cc);
                Log.e(TAG, "query =="+Arrays.toString(cursor.getSelectionArgs()));
                return cursor;
            }

            case SEARCH_LOADER_ID: {
                presentId = SEARCH_LOADER_ID;

                if (args != null) {
                    String query = args.getString(SEARCH_KEY);

                    String selection1 = DataContract.Students.NAME + " LIKE '%" +query + "%'";

                    return new CursorLoader(
                            getActivity(),
                            uri,
                            DataContract.Students.PROJECTION_ALL,
                            selection1,    // selection
                            null,           // arguments
                            DataContract.Students.NAME + " ASC");

                    } else {
                    return new CursorLoader(
                            getActivity(),
                            uri,
                            DataContract.Students.PROJECTION_ALL,
                            null,    // selection
                            null,           // arguments
                            DataContract.Students.NAME + " ASC");
                    }
                }

            default:{
                presentId = NORMAL_LOADER_ID;
                return new CursorLoader(
                        getActivity(),
                        uri,
                        DataContract.Students.PROJECTION_ALL,
                        null,    // selection
                        null,           // arguments
                        DataContract.Students.NAME + " ASC");
            }
        }

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        if(cursor.moveToFirst()) {
            resultsCursorAdapter.swapCursor(cursor);

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //resultsCursorAdapter.swapCursor(null);
    }


    private void updateDashboard() {
        try {
            getLoaderManager().restartLoader(presentId, null, this);
        } catch (Exception e) {
            Log.e(TAG, "" + e);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateDashboard();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }


}
