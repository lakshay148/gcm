package com.gcmandroid.app2;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gcmandroid.app2.Fragments.CustomizeNotificationFragment;
import com.gcmandroid.app2.Fragments.RegistrationFragment;
import com.gcmandroid.app2.gcm.GcmConfig;
import com.gcmandroid.app2.gcm.UpdaterService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class Gcm extends ActionBarActivity implements View.OnClickListener {

    public static String TAG = Gcm.class.getSimpleName();
    Button bRegister,bCustomize;
    TextView tvRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcm);
        bRegister = (Button) findViewById(R.id.bRegister);
        bCustomize = (Button) findViewById(R.id.bCustomize);
        tvRegistration = (TextView) findViewById(R.id.tvRegistrationId);
        bRegister.setOnClickListener(this);
        bCustomize.setOnClickListener(this);
        startRegistration();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gcm, menu);
        return true;
    }

    public void startRegistration()
    {
        if(checkPlayServices(getApplicationContext()))
        {
            initiateGcmRegistration();
        }
        else
        {
            Log.e(TAG, "check Play service false");
        }
    }

    public static boolean checkPlayServices(Context context)
    {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (isAvailable != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable))
            {
                Log.e(TAG, "user is Recoverable ");
//                GooglePlayServicesUtil.getErrorDialog(isAvailable, context, ).show();
            }
            else
            {
                Log.e(TAG, "Device Not Supported !!");
            }
            return false;
        }
        return true;
    }

    private void initiateGcmRegistration() {

        Log.e("LAKSHAY","Gcm.java.initiateGcmRegistration");
        String registered = checkIfRegistered();
        if(null != registered)
        {
            Log.e("LAKSHAY","Gcm.java.initiateGcmRegistration device already registered ");
            tvRegistration.setText(registered);
            sendRegIdToServer(registered);
        }
        else {
            registerForGcm(getApplicationContext());
        }
    }

    private String checkIfRegistered() {
        Log.e("LAKSHAY","Gcm.java.checkIfRegistered");
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(GcmConfig.SHARED_PREFS,0);
        String registrationId = preferences.getString(GcmConfig.REGISTRATION_ID,null);
        Log.e("LAKSHAY","Gcm.java.checkIfRegistered Registration Id in preferences : "+ registrationId + " ");
        return registrationId;
    }

    private void registerForGcm(final Context context) {
        Log.e("LAKSHAY","Gcm.java.registerForGcm");

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
                try {
                    String registrationId = googleCloudMessaging.register(GcmConfig.senderId);
                    Log.e("LAKSHAY","Gcm.java.run Registration : " + registrationId );
                    processRegistrationId(registrationId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void processRegistrationId(String registrationId) {
        Log.e("LAKSHAY","Gcm.java.processRegistrationId");
        storeInPrefs(registrationId);
        storeInDB(registrationId);
        sendRegIdToServer(registrationId);
    }

    private void storeInPrefs(String registrationId) {
        Log.e("LAKSHAY","Gcm.java.storeInPrefs");

        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(GcmConfig.SHARED_PREFS,0).edit();
        editor.putString(GcmConfig.REGISTRATION_ID ,registrationId);
        editor.commit();
    }

    private void storeInDB(String registrationId) {
        Log.e("LAKSHAY","Gcm.java.storeInDB");
    }

    private void sendRegIdToServer(final String registrationId) {
        Log.e("LAKSHAY","Gcm.java.sendRegIdToServer");

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://192.168.136.122/gcm_server_php/register.php");
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                Log.e("LAKSHAY","Gcm.java.doInBackground : " + registrationId);
                nameValuePairList.add(new BasicNameValuePair("registrationId",registrationId));
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    Log.e("LAKSHAY","Gcm.java.doInBackground Response : " + httpResponse.toString());
                    Log.e("LAKSHAY","Gcm.java.doInBackground Status" + httpResponse.getStatusLine());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.startService)
        {
            startService(new Intent(this, UpdaterService.class));
        }
        else if(id == R.id.stopService)
        {
            stopService(new Intent(this,UpdaterService.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.bRegister:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                RegistrationFragment registrationFragment = new RegistrationFragment();
                transaction.replace(R.id.fragment_container,registrationFragment);
                transaction.commit();
                break;
            case R.id.bCustomize:
                FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
                CustomizeNotificationFragment customizeNotificationFragment = new CustomizeNotificationFragment();
                transaction1.replace(R.id.fragment_container,customizeNotificationFragment);
                transaction1.commit();
                break;
        }
    }
}
