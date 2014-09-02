package com.gcmandroid.app2.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by lakshay on 29/8/14.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    public static String TAG = GcmBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG , "GcmBroadcastReceiver.java.onReceive");

        ComponentName componentName = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        startWakefulService(context, intent.setComponent(componentName));
        setResultCode(Activity.RESULT_OK);
    }
}
