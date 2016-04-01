package com.jcedar.visibook.lautech.gcm.command;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.jcedar.visibook.lautech.gcm.GCMCommand;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.provider.DataContract;
import com.jcedar.visibook.lautech.sync.SyncHelper;

/**
 * Created by Afolayan on 28/1/2016.
 */
public class UpdateCommand extends GCMCommand {
    private static final String TAG = UpdateCommand.class.getSimpleName();

    @Override
    public void execute(Context context, String type, String extraData) {
        Log.d(TAG, "Trying to sync deleted account");
        Bundle settings = new Bundle();
        settings.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settings.putBoolean(SyncHelper.GCM_TRIGGERED, true);
        settings.putInt(SyncHelper.ACTION, Integer.parseInt(type));

        ContentResolver.requestSync(
                AccountUtils.getChosenAccount(context),
                DataContract.CONTENT_AUTHORITY, settings);
    }
}
