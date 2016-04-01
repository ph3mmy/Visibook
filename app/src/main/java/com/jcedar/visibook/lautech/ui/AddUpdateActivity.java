package com.jcedar.visibook.lautech.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.io.adapters.UpdateCursorAdapter;
import com.jcedar.visibook.lautech.provider.DataContract;

public class AddUpdateActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = AddUpdateActivity.class.getSimpleName();
    UpdateCursorAdapter updateCursorAdapter;
    ListView listView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Update Names");
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(android.R.id.list);
        listView.setItemsCanFocus(true);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        updateCursorAdapter = new UpdateCursorAdapter(this, null, R.layout.list_item_update);
        listView.setAdapter( updateCursorAdapter );

        getSupportLoaderManager().initLoader(0, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Cursor cursor = ((UpdateCursorAdapter) parent.getAdapter()).getCursor();
                if(cursor != null) {
                    cursor.moveToPosition(position);

                    long student_id = cursor.getLong(
                            cursor.getColumnIndex(DataContract.StudentsChapter._ID));
                    UpdateFragment updateFragment = UpdateFragment.newInstance( student_id );
                    updateFragment.show( getSupportFragmentManager(), "Update Dialog Fragment");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == R.id.action_update ){
            startActivity( new Intent( this, AddActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NavigationDrawerFragment.MenuConstants.NAVDRAWER_ITEM_ADD_UPDATE;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.StudentsChapter.CONTENT_URI;

        return new CursorLoader(
                this,
                uri,
                DataContract.StudentsChapter.PROJECTION_ALL,
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
}
