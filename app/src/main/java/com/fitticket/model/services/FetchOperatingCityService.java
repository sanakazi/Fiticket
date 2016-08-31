package com.fitticket.model.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.GetOperatingCitiesResponse;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.utils.WebServices;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Fiticket on 10/03/16.
 */
public class FetchOperatingCityService extends IntentService {

    private static final String TAG = FetchOperatingCityService.class.getSimpleName();

    public FetchOperatingCityService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = Apis.GET_OPERATING_CITIES ;
        WebServices.triggerVolleyGetRequest(this, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Operating cities get response:" + response);
                GetOperatingCitiesResponse json = new Gson().fromJson(response, GetOperatingCitiesResponse.class);
                ArrayList<GetOperatingCitiesResponse.CitiesJson> operatingCities = json.getData().getCities();
                MySingleton.getInstance(FetchOperatingCityService.this).setOperatingCities(operatingCities);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


    }
}
