package com.jcedar.visibook.lautech.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Afolayan on 28/1/2016.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = SyncAdapter.class.getSimpleName();
    private final Context mContext;
    public Uri uri;
    public int vibrate;
    public int defaultSound;
    Bundle syncSummary = Bundle.EMPTY;
    private SyncHelper mSyncHelper;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(TAG, "Sync Extras " + extras.toString());
        int action = extras.getInt(SyncHelper.ACTION, 0);
        /*
    From update: {"status":"Success","code":"201","id":"104"}
    From add: {"status":"Success","code":"101","id":"104"}
     */

        switch (action){
            case 101:
                Log.e(TAG, "new name added");
                syncSummary = mSyncHelper.performNewStudentSync();
                break;
            case 201:
                Log.e(TAG, "a name updated");
                long id = Long.getLong(extras.getString("code"));
                syncSummary = mSyncHelper.performUpdateStudentSync(id);
                break;
        }

    }
}
