package com.jcedar.visibook.lautech.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Afolayan on 28/1/2016.
 */
public class SyncService extends Service {

    private static final String TAG = SyncService.class.getSimpleName();
    public SyncService(){
        Log.d(TAG, "Inside sync service");
    }
    private static SyncAdapter sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();
    @Override
    public IBinder onBind(Intent intent) {
        return  sSyncAdapter.getSyncAdapterBinder();
    }

    @Override
    public void onCreate() {
        //super.onCreate();
        synchronized (sSyncAdapterLock){
            if(sSyncAdapter == null){
                sSyncAdapter = new SyncAdapter(getApplicationContext(), false);
            }
        }
    }
}
