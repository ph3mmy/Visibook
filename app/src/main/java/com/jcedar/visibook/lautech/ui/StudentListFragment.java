package com.jcedar.visibook.lautech.ui;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Rect;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.adapter.RecyclerCursorAdapter;
import com.jcedar.visibook.lautech.io.adapters.StudentCursorAdapter;
import com.jcedar.visibook.lautech.provider.DataContract;
import com.jcedar.visibook.lautech.ui.view.SimpleSectionedListAdapter;

public class StudentListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    protected static final String NAIRA = "\u20A6";
    private static final String TAG = StudentListFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";

    private int mPosition;
    private String mParam2;
    public static final String STUDENT_POSITION = "position";

    private StudentCursorAdapter mAdapter;
    private SimpleSectionedListAdapter sSectionAdapter;
    private ListView listView;
    private TextView tvError;
    private Bundle mStudentBundle = Bundle.EMPTY;
    private Listener mCallback;
    private String context;
    RecyclerView recyclerView;
    RecyclerCursorAdapter resultsCursorAdapter;


    public static StudentListFragment newInstance(int position) {
        StudentListFragment fragment = new StudentListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);

        fragment.setArguments(args);
        return fragment;
    }

    public StudentListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            getLoaderManager().initLoader(0, null, this);
           Loader loader = getLoaderManager().getLoader(0);
            if (loader != null && !loader.isReset()) {
                getLoaderManager().restartLoader(0, null, this);
            } else {
                getLoaderManager().initLoader(0, null, this);
            }





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

        //setListAdapter(mAdapter);
       // setListAdapter(sSectionAdapter);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView;
        if( !context.equalsIgnoreCase("StudentDetailsActivity")) {
            rootView =
                    (ViewGroup) inflater.inflate(R.layout.fragment_dash, container, false);
        } else {
            rootView =
                    (ViewGroup) inflater.inflate(R.layout.fragment_home1, container, false);
        }
        tvError = (TextView) rootView.findViewById(R.id.tvErrorMag);
        recyclerView = (RecyclerView) rootView.findViewById( R.id.recyclerview );
        resultsCursorAdapter = new RecyclerCursorAdapter( getActivity() );

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setLayoutManager(new WrappingLinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(resultsCursorAdapter);
        recyclerView.addItemDecoration( new VerticalSpaceItemDecoration(3) );
        tvError = (TextView) rootView.findViewById(R.id.tvErrorMag);

        resultsCursorAdapter.setOnItemClickListener(new RecyclerCursorAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Cursor data) {

                long Id = data.getLong(
                        data.getColumnIndex(DataContract.Students._ID));
                Log.d(TAG, "selectedId = " + Id + STUDENT_POSITION);
                // add position to bundle
                //mHomeBundle.putInt(_POSITION, position);
                mCallback.onSchoolSelected(Id);
            }
        });


        return rootView;
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Listener) activity;
            mCallback.onFragmentAttached(this);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Listener");
        }

        activity.getContentResolver().registerContentObserver(
                DataContract.StudentsChapter.CONTENT_URI, true, mObserver);

        updateDashboard();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof Listener) {
            ((Listener) getActivity()).onFragmentDetached(this);
        }
        getActivity().getContentResolver().unregisterContentObserver(mObserver);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.StudentsChapter.CONTENT_URI;
        CursorLoader cursorLoader = null;

        return new CursorLoader(
                getActivity(),
                uri,
                DataContract.StudentsChapter.PROJECTION_ALL,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        resultsCursorAdapter.swapCursor(data);

        /*
        Bundle bundle = new Bundle();
        int count = 0;

        if( data.moveToFirst() ) {
            mAdapter.swapCursor(data);
            mAdapter.notifyDataSetChanged();

            data.moveToFirst();
            while ( !data.isAfterLast()) {

                long studentId = data.getLong(
                        data.getColumnIndexOrThrow(DataContract.Students._ID));

                bundle.putLong(StudentDetailsActivity.ARG_STUDENT_LIST
                        + Integer.toString(count++), studentId);
                data.moveToNext();
            }
           this.mStudentBundle = bundle;
        } else {
            mAdapter.swapCursor(null);
            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Error retrieving data");

        }*/

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        resultsCursorAdapter.swapCursor(null);
    }
   /* @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        final Cursor cursor = (Cursor) sSectionAdapter.getItem(position);
        if (cursor != null) {
            long Id = cursor.getLong(
                    cursor.getColumnIndex(DataContract.Students._ID));
            Log.d(TAG, "selectedId = " + Id + STUDENT_POSITION);
            // add position to bundle
            mStudentBundle.putInt(STUDENT_POSITION, position);
            mCallback.onSchoolSelected(Id);
        }

    }*/

    interface Listener {
        void onSchoolSelected(long studentId);
        void onFragmentAttached(Fragment fragment);
        void onFragmentDetached(Fragment fragment);
    }


    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (!isAdded()) {
                return;
            }
            getLoaderManager().restartLoader(0, null, StudentListFragment.this);
        }
    };

    private void updateDashboard() {
        // do work
        try {
            getLoaderManager().restartLoader(0, null, this);
        } catch (Exception e) {
            Log.e(TAG, "" + e);

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.activateApp( getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.deactivateApp( getActivity());
    }
}
