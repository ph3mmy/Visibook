package com.jcedar.visibook.lautech.gcm;

import android.content.Context;

public abstract class GCMCommand
{
    public abstract void execute(Context context, String type, String extraData);
}
