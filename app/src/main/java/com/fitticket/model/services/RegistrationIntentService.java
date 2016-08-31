/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fitticket.model.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.DeviceTokenJson;
import com.fitticket.model.pojos.DeviceTokenResponseJson;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.HttpConnectionUtil;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getSimpleName();
    private static final String[] TOPICS = {"global"};
    private PreferencesManager sPrefs;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sPrefs = PreferencesManager.getInstance(RegistrationIntentService.this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_sender_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            DeviceTokenResponseJson deviceTokenResponseJson=sendRegistrationToServer(token);

            // Subscribe to topic channels
//            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.

            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sPrefs.saveSentTokenToServer(false);
            sPrefs.saveSentTokenIdToServer(false);

        }
       /* // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(ApplicationData.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);*/
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private DeviceTokenResponseJson sendRegistrationToServer(String token) {
        LocationManager locationManager= (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Location lastLocation;
//        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        }else{
//            lastLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        }
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        int id = sPrefs.getUserId();
        DeviceTokenJson dtj= new DeviceTokenJson();
        if(id!=0) {           //if customer id ==0, user had not logged in. Don't send customer id.

            dtj.setCustomerId(id);
        }
        String deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String versionName=null;
        try {
            versionName = getApplicationContext().getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(!TextUtils.isEmpty(versionName)){
            dtj.setAppVersion(versionName);

        }
        dtj.setOsVersion(Build.VERSION.RELEASE);
        dtj.setDeviceModel(getDeviceName());
        Log.d(TAG, "Model:"+getDeviceName());
        dtj.setDeviceId(deviceId);
        dtj.setDeviceToken(token);
        dtj.setDeviceType("2");
        DeviceTokenResponseJson dtrj=null;
        int status = 0;
        try {
        connection = HttpConnectionUtil.getPostConnection(Apis.URL_DEVICE_TOKEN_2);
        String requestData = HttpConnectionUtil.gson.toJson(dtj);

        PrintWriter out = new PrintWriter(connection.getOutputStream());
        out.print(requestData);
        out.flush();
        status = connection.getResponseCode();
        if (status == HttpConnectionUtil.POST_OK) {

            reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String responseData = sb.toString();
            Log.i(TAG, "server response : " + responseData);

            dtrj = HttpConnectionUtil.gson.fromJson(responseData,
                    new TypeToken<DeviceTokenResponseJson>() {
                    }.getType());
        }
        else {
            reader = new BufferedReader(new InputStreamReader(
                    connection.getErrorStream()));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            Log.e(TAG, "server response : " + sb.toString());
        }

    }
    catch (Exception e) {
        e.printStackTrace();
    }
        if (dtrj!=null && dtrj.getId()==0) {

                sPrefs.saveSentTokenToServer(true);

            if(id!=0) {
                sPrefs.saveSentTokenIdToServer(true);
            }
        }else{
            sPrefs.saveSentTokenToServer(false);
            sPrefs.saveSentTokenIdToServer(false);
        }
        return dtrj;
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]


    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;

        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


}