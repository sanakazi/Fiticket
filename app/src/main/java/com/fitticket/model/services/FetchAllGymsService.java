package com.fitticket.model.services;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.constants.Constants;
import com.fitticket.model.pojos.GetGymsByLocationResponse;
import com.fitticket.model.pojos.GymDataJson;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import io.paperdb.Paper;

/**
 * Created by Fiticket on 11/02/16.
 */
public class FetchAllGymsService extends IntentService{
    private static final String TAG = FetchAllGymsService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public FetchAllGymsService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String cityId= PreferencesManager.getInstance(this).getSelectedCityId();
        String url= Apis.GET_GYMS_BY_CITY_URL+cityId;
        WebServices.triggerVolleyGetRequest(this, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                GetGymsByLocationResponse json = new Gson().fromJson(response, GetGymsByLocationResponse.class);
                if (json.getStatusCode().equals(WebServices.SUCCESS_CODE)) {
                    ArrayList<GymDataJson> gymsList = json.getData().getGyms();
                    MySingleton.getInstance(FetchAllGymsService.this).setmAllGymsList(gymsList);
                    Paper.book().write(Constants.ALL_GYM_LIST, gymsList);
                    PreferencesManager.getInstance(FetchAllGymsService.this)
                            .saveActivitiesTimeStamp(Calendar.getInstance().getTimeInMillis());

                } else {
                    Utilities.showToast(FetchAllGymsService.this, json.getStatusMsg());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
