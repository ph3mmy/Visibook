package com.jcedar.visibook.lautech.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.io.adapters.UpdateCursorAdapter;
import com.jcedar.visibook.lautech.provider.DataContract;

public class AddUpdateFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = AddUpdateFragment.class.getSimpleName();
    UpdateCursorAdapter updateCursorAdapter;
    ListView listView;
    private Toolbar toolbar;
    private Listener mCallback;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_add_update, container, false);

        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setItemsCanFocus(true);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        updateCursorAdapter = new UpdateCursorAdapter(getActivity(), null, R.layout.list_item_update);
        listView.setAdapter( updateCursorAdapter );



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Cursor cursor = ((UpdateCursorAdapter) parent.getAdapter()).getCursor();
                if(cursor != null) {
                    cursor.moveToPosition(position);

                    long student_id = cursor.getLong(
                            cursor.getColumnIndex(DataContract.StudentsChapter._ID));
                    UpdateFragment updateFragment = UpdateFragment.newInstance( student_id );
                    updateFragment.show( getFragmentManager(), "Update Dialog Fragment");
                }
            }
        });
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getActivity().setTitle("Update Names");
            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_update, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == R.id.action_update ){
            startActivity( new Intent( getActivity(), AddActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.Students.CONTENT_URI;

        return new CursorLoader(
                getActivity(),
                uri,
                DataContract.Students.PROJECTION_ALL,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        updateCursorAdapter.swapCursor( data );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateCursorAdapter.swapCursor(null);
    }

    interface Listener {
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

    }

    @Override
    public void onDetach() {
        super.onDetach();
        super.onDetach();
        if (getActivity() instanceof Listener) {
            ((Listener) getActivity()).onFragmentDetached(this);
        }
    }

}
