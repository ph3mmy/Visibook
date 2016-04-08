package com.jcedar.visibook.lautech.ui;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.adapter.RecyclerCursorAdapterAll;
import com.jcedar.visibook.lautech.io.adapters.StudentCursorAdapter;
import com.jcedar.visibook.lautech.provider.DataContract;
import com.jcedar.visibook.lautech.ui.view.SimpleSectionedListAdapter;

public class AllStudentListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    protected static final String NAIRA = "\u20A6";
    private static final String TAG = AllStudentListFragment.class.getSimpleName();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mPosition;
    private String mParam2;

    private StudentCursorAdapter mAdapter;
    private SimpleSectionedListAdapter sSectionAdapter;
    private ListView listView;
    private TextView tvError;
    private Bundle mHomeBundle = Bundle.EMPTY;
    private String _POSITION = "position";
    private Listener mCallback;
    static String context;

    RecyclerView recyclerView;
    RecyclerCursorAdapterAll resultsCursorAdapter;
    View rootView;

    public static AllStudentListFragment newInstance(int position) {
        AllStudentListFragment fragment = new AllStudentListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);


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
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_PARAM1);
        }

        mAdapter = new StudentCursorAdapter(getActivity(), null,
                R.layout.list_n_item_student);

        sSectionAdapter = new SimpleSectionedListAdapter(getActivity(),
                R.layout.list_group_header, mAdapter);
        context = getActivity().getClass().getSimpleName();

        // setListAdapter(mAdapter);
        /*setListAdapter(sSectionAdapter);*/
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*super.onCreateView(inflater, container, savedInstanceState);*/

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

        recyclerView = (RecyclerView) rootView.findViewById( R.id.recyclerview );
        resultsCursorAdapter = new RecyclerCursorAdapterAll( getActivity() );

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setLayoutManager(new WrappingLinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(resultsCursorAdapter);
        tvError = (TextView) rootView.findViewById(R.id.tvErrorMag);

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
                Log.d(TAG, "selectedId = " + Id + _POSITION);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
            getLoaderManager().restartLoader(1, null, AllStudentListFragment.this);
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.Students.CONTENT_URI;
        return new CursorLoader(
                    getActivity(),
                    uri,
                    DataContract.Students.PROJECTION_ALL,
                    null,    // selection
                    null,           // arguments
                    DataContract.Students.NAME + " ASC"
            );
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        resultsCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        resultsCursorAdapter.swapCursor(null);
    }


    private void updateDashboard() {
        // do work
        try {
            getLoaderManager().restartLoader(0, null, this);
        } catch (Exception e) {
            Log.e(TAG, "" + e);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateDashboard();
    }


}
