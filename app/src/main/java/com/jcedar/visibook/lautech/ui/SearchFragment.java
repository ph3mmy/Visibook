package com.jcedar.visibook.lautech.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

public class SearchFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final String _POSITION = "POSITION";

    private Listener mListener;
    StudentCursorAdapter studentListAdapter;
    Uri dataUri;
    TextView error;

    public SearchFragment() {
        // Required empty public constructor
    }
    public static AllStudentListFragment newInstance(int position) {
        AllStudentListFragment fragment = new AllStudentListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentListAdapter = new StudentCursorAdapter(getActivity(), null, R.layout.list_n_item_student);
        setListAdapter( studentListAdapter );
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(2, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView;

            rootView =
                    (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);

        ListView listView = (ListView) rootView.findViewById(android.R.id.list );

        error = (TextView) rootView.findViewById(R.id.tvErrorMag);

        listView.setItemsCanFocus(true);
        listView.setDividerHeight(0);
        listView.setCacheColorHint(Color.WHITE);
        listView.setSelector(R.drawable.list_selector);
        TextView emptyView = new TextView(getActivity(), null, android.R.attr.state_empty);

        ((ViewGroup) rootView.findViewById(android.R.id.empty)).addView(emptyView);


       /* setOnItemClickListener(new RecyclerCursorAdapterAll.OnItemClickListener() {
            @Override
            public void onItemClicked(Cursor data) {

                long Id = data.getLong(
                        data.getColumnIndex(DataContract.Students._ID));
                Log.d(TAG, "selectedId = " + Id + _POSITION);
                mListener.onListItemSelected(Id);
            }
        });*/

        return rootView;
    }

    public void onButtonPressed(long id) {
        if (mListener != null) {
            mListener.onListItemSelected(id);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = (Listener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onListItemSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri studenturi;

        /*if( args != null) {
            String uri = args.getString("uri");
            studenturi = Uri.parse(uri);

        Log.e(TAG, studenturi +" uri in oncreateloader");
        } else {
            studenturi = DataContract.Students.CONTENT_URI;
            Log.e(TAG, studenturi +" uri in oncreateloader no bundle");
        }
        */
        Loader<Cursor> cursorLoader;

        if (dataUri != null ){
            Log.e(TAG, dataUri +" dataUri inside loader");

            cursorLoader = new CursorLoader(getActivity(), dataUri,
                    DataContract.Students.PROJECTION_ALL,
                    null, null, DataContract.Students.NAME +" ASC");

            return cursorLoader;
        }
        else {
            cursorLoader = new CursorLoader(getActivity(), DataContract.Students.CONTENT_URI,
                    DataContract.Students.PROJECTION_ALL,
                    null, null, DataContract.Students.SORT_ORDER_DEFAULT);
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        studentListAdapter.swapCursor(data);

        data.moveToFirst();
        Log.e(TAG, data.getCount() +" cursor value");

        if ( data.getCount() == 0){
            error.setVisibility(View.VISIBLE);
            error.setText("No result found");
        }

        /*while ( !data.isAfterLast()){
            data.moveToNext();
        } */

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void reloadFromArguments(Uri uri, String query) {
        Log.d(TAG, "reloading fragment");

        // Load new arguments
        if(uri.toString() != null) {
            Uri studenturi = uri;

            dataUri = studenturi;

            /*if (studenturi == null) {
                studenturi = DataContract.Students.CONTENT_URI;
            }*/

            Log.e(TAG, studenturi.toString() + " uri search");

            Bundle arguments = new Bundle();
            arguments.putString("uri", studenturi.toString());

            if(isAdded()){
               getLoaderManager().restartLoader(2, arguments, this);
                 /*Cursor c = new DatabaseHelper(getActivity()).getSearch(query);
                if (c.moveToFirst()) {
                   studentListAdapter.swapCursor(c);
                    Log.e(TAG, "I got something for "+query);

                }c.close();*/
            }

        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (isAdded()){
            Cursor cursor = (Cursor) studentListAdapter.getItem(position);
            if( cursor != null ){
                long studentId = cursor.getLong(
                        cursor.getColumnIndex( DataContract.Students._ID)
                );
                mListener.onListItemSelected(studentId);
            }
        }
    }

    public interface Listener {
        void onListItemSelected(long studentId);
    }

}
