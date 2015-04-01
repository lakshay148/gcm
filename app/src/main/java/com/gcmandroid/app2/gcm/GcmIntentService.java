package com.gcmandroid.app2.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gcmandroid.app2.Gcm;
import com.gcmandroid.app2.R;

import java.util.Random;

/**
 * Created by lakshay on 28/8/14.
 */
public class GcmIntentService extends IntentService {

    public  static String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService()
    {
        super("GcmIntentService");
    }
    public GcmIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e("LAKSHAY", "GcmIntentService.java.onHandleIntent");

        Bundle bundle = intent.getExtras();


        generateNotification(bundle);

    }

    private void generateNotification(Bundle bundle) {

        Random random = new Random();
        Intent intent = new Intent(this, Gcm.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,random.nextInt(200),intent, PendingIntent.FLAG_UPDATE_CURRENT );
        String message = bundle.getString(GcmConfig.MESSAGE_NOTIFICATION);
        String landingScreen =  bundle.getString(GcmConfig.LANDING_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true).setContentText(message);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("Test Title");
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
