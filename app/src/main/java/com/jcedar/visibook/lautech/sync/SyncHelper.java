package com.jcedar.visibook.lautech.sync;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.jcedar.visibook.lautech.gcm.GcmIntentServices;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.helper.AppHelper;
import com.jcedar.visibook.lautech.helper.AppSettings;
import com.jcedar.visibook.lautech.helper.PrefUtils;
import com.jcedar.visibook.lautech.helper.ServiceHandler;
import com.jcedar.visibook.lautech.helper.UIUtils;
import com.jcedar.visibook.lautech.io.jsonhandlers.StudentHandler;
import com.jcedar.visibook.lautech.io.jsonhandlers.StudentUpdateHandler;
import com.jcedar.visibook.lautech.provider.DataContract;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Afolayan on 28/1/2016.
 */
public class SyncHelper {
    public static final String GCM_TRIGGERED = "gcm_triggered";
    public static final String NEW_STUDENT_COUNT = "new_student_count";
    public static final String UPDATE_COUNT = "update_count";
    private static final String TAG = SyncHelper.class.getSimpleName();

    private Context mContext;
    public static final String ACTION = "Action";

    public SyncHelper(Context context) {
        mContext = context;
    }

    public static void requestManualSync(Account chosenAccount) {
        Bundle settings = new Bundle();
        settings.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settings.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(
                chosenAccount,
                DataContract.CONTENT_AUTHORITY, settings);
    }

    public Bundle performNewStudentSync(){
        Log.d(TAG, "Syncing");

        //Send lastId to /add.php
        //get new chapter updates

        ContentResolver resolver = mContext.getContentResolver();
        ArrayList<ContentProviderOperation> batch
                = new ArrayList<>();
        long id=0;
        Bundle syncSummary = new Bundle();

        //SELECT id FROM naas ORDER BY id DESC LIMIT 0 , 1
        if( isOnline() ){
            Cursor cursor = resolver.query(
                    DataContract.Students.CONTENT_URI,
                    new String[]{ DataContract.Students._ID},
                    null, null,
                    DataContract.Students._ID+" DESC LIMIT 0, 1"
            );
            if (cursor != null) {
                cursor.moveToFirst();
                id = cursor.getLong(0);
                Log.e(TAG, " id is "+id);

                cursor.close();
            }
            if ( id > 0 ){
                try{
                    String response =  ServiceHandler.makeServiceCall
                            (AppSettings.SERVER_URL +"add.php?lastId="+id, ServiceHandler.GET);
                    if(!TextUtils.isEmpty( response )){

                        Log.e(TAG, response + " response for new update");
                        StudentHandler studentHandler = new StudentHandler(mContext);
                        ArrayList<ContentProviderOperation> operations =
                                studentHandler.parse(response);
                        syncSummary.putInt(NEW_STUDENT_COUNT, studentHandler.getStudentCount());
                        batch.addAll( operations );

                        if ( AccountUtils.getUserChapter(mContext) != null){
                            String chapter = AccountUtils.getUserChapter(mContext);
                            Log.e(TAG, chapter + " chapter for new update");
                            new AppHelper(mContext).pullAndSaveStudentChapterDataForSync(chapter);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if ( batch.size() > 0 ){
                    try {
                        resolver.applyBatch(DataContract.CONTENT_AUTHORITY, batch);
                    } catch (RemoteException | OperationApplicationException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Problem applying batch operation" + e);
                    }
                }

            }

        }
        Log.i(TAG, "Sync complete");
        return syncSummary;
    }

    public Bundle performUpdateStudentSync( long lastId){
        Log.d(TAG, "Syncing");

        ContentResolver resolver = mContext.getContentResolver();
        ArrayList<ContentProviderOperation> batch
                = new ArrayList<>();

        Bundle syncSummary = new Bundle();

        if( isOnline() ){

                try{
                    String response =  ServiceHandler.makeServiceCall
                            (AppSettings.SERVER_URL +"update.php?updatedId="+lastId, ServiceHandler.GET);
                    if(!TextUtils.isEmpty( response )){

                        Log.e(TAG, response + " response for new update");
                        StudentUpdateHandler studentHandler = new StudentUpdateHandler(mContext);
                        ArrayList<ContentProviderOperation> operations =
                                studentHandler.parse(response);
                        syncSummary.putInt(UPDATE_COUNT, studentHandler.getStudentCount());
                        batch.addAll( operations );
                        if ( AccountUtils.getUserChapter(mContext) != null){
                            String chapter = AccountUtils.getUserChapter(mContext);
                            Log.e(TAG, chapter + " chapter for new update");
                            new AppHelper(mContext).pullAndSaveStudentChapterDataForSync(chapter);
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if ( batch.size() > 0 ){
                    try {
                        resolver.applyBatch(DataContract.CONTENT_AUTHORITY, batch);
                    } catch (RemoteException | OperationApplicationException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Problem applying batch operation" + e);
                    }
                }



        }
        Log.i(TAG, "Sync complete");
        return syncSummary;
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    Bundle syncSummary;
    public void requestManualSync(){
        UIUtils.showToast(mContext, "Performing sync" );
        GcmIntentServices u = new GcmIntentServices();
        if ( isOnline() ) t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int newStudentNo = syncSummary.getInt(SyncHelper.NEW_STUDENT_COUNT, 0);
        if (newStudentNo != 0 && PrefUtils.hasNotification(mContext)) {
            u.displayNotification(mContext, "", u.newStudent, newStudentNo, "New Student Added");
        } else {
            UIUtils.showAlert("Update", "No new names are added so far", mContext);
        }
    }

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            syncSummary = performNewStudentSync();
        }
    });

}
