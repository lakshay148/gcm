package com.gcmandroid.app2.gcm;

import android.app.Service;
import android.content.Intent;
import android.os.Debug;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by lakshay on 28/8/14.
 */
public class UpdaterService extends Service {

    private static final String TAG = UpdaterService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        Debug.startMethodTracing();
        new UpdateThread().run();

    }

    public class UpdateThread extends Thread{


        @Override
        public void run() {

            while(true) {
                try {
                    Log.e(TAG , "In the thread ");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
