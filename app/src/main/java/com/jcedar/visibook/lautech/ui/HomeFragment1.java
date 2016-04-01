package com.jcedar.visibook.lautech.ui;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.io.adapters.StudentCursorAdapter;
import com.jcedar.visibook.lautech.provider.DataContract;
import com.jcedar.visibook.lautech.ui.view.SimpleSectionedListAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment1 extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    protected static final String NAIRA = "\u20A6";
    private static final String TAG = AllStudentListFragment.class.getSimpleName();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mPosition;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private StudentCursorAdapter mAdapter;
    private SimpleSectionedListAdapter sSectionAdapter;
    private ListView listView;
    private TextView tvError;
    private Bundle mHomeBundle = Bundle.EMPTY;
    private String _POSITION = "position";
    private Listener mCallback;


    public static HomeFragment1 newInstance(int position) {
        HomeFragment1 fragment = new HomeFragment1();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);

        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment1() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Initialize loader
        getLoaderManager().initLoader(1, null, this);


                Loader loader1 = getLoaderManager().getLoader(1);
                if (loader1 != null && !loader1.isReset()) {
                    getLoaderManager().restartLoader(1, null, this);
                } else {
                    getLoaderManager().initLoader(1, null, this);
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
        // setListAdapter(mAdapter);
        setListAdapter(sSectionAdapter);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.fragment_dash, container, false);

        tvError = (TextView) rootView.findViewById(R.id.tvErrorMag);
        listView = (ListView) rootView.findViewById(android.R.id.list);

        listView.setItemsCanFocus(true);
        listView.setCacheColorHint(getResources().getColor(
                R.color.white));
        listView.setVerticalScrollBarEnabled(true);
        listView.setDividerHeight(0);

        return rootView;

    }

    public ListView getListView() {
        return listView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        final Cursor cursor = (Cursor) sSectionAdapter.getItem(position);
        if (cursor != null) {
            long Id = cursor.getLong(
                    cursor.getColumnIndex(DataContract.Students._ID));
            Log.d(TAG, "selectedId = " + Id + _POSITION);
            // add position to bundle
            mHomeBundle.putInt(_POSITION, position);
            mCallback.onAllSelected(Id, mHomeBundle);
        }

    }

    interface Listener {
        void onAllSelected(long courseId, Bundle data);
        void onFragmentAttached(ListFragment fragment);
        void onFragmentDetached(ListFragment fragment);
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
            getLoaderManager().restartLoader(1, null, HomeFragment1.this);
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.Students.CONTENT_URI;
        CursorLoader cursorLoader =  new CursorLoader(
                    getActivity(),
                    uri,
                    DataContract.Students.PROJECTION_ALL,
                    null,    // selection
                    null,           // arguments
                    DataContract.Students.CHAPTER + " ASC"
            );
        return cursorLoader;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Bundle bundle = new Bundle();
        int count = 0;

        /*if( data.getCount() == 0) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(  "No data yet");
        }*/

        List<SimpleSectionedListAdapter.Section> sections =
                new ArrayList<SimpleSectionedListAdapter.Section>();
        String chapter, dummy="dummy";

        if( data.moveToFirst() ) {
            mAdapter.swapCursor(data);
            mAdapter.notifyDataSetChanged();

            data.moveToFirst();
            while ( !data.isAfterLast()) {

               chapter = data.getString( data.getColumnIndex( DataContract.Students.CHAPTER));

                if( !chapter.equalsIgnoreCase(dummy)){
                    sections.add( new SimpleSectionedListAdapter.Section( data.getPosition(),
                            chapter));
                }

                dummy = chapter;

                long studentId = data.getLong(
                        data.getColumnIndexOrThrow(DataContract.Students._ID));
                SimpleSectionedListAdapter.Section[] sectionArray =
                        new SimpleSectionedListAdapter.Section[sections.size()];
                sSectionAdapter.setSections(sections.toArray(sectionArray));
                bundle.putLong(AllStudentDetailsActivity.ARG_ALL_LIST
                        + Integer.toString(count++), studentId);
                data.moveToNext();
            }
          this.mHomeBundle = bundle;
        } else {
            mAdapter.swapCursor(null);
            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Error retrieving data ");

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }



}
