package com.fitticket.model.services;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.constants.Constants;
import com.fitticket.model.pojos.ActivityDetailJson;
import com.fitticket.model.pojos.GetActivityByCityResponseJson;
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
public class FetchAllActivitiesService extends IntentService {
    private static final String TAG = FetchAllActivitiesService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetchAllActivitiesService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String cityId = PreferencesManager.getInstance(this).getSelectedCityId();
        String url = Apis.GET_ACTVITIES_BY_CITY_URL + cityId;
        WebServices.triggerVolleyGetRequest(this, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                GetActivityByCityResponseJson json = new Gson().fromJson(response, GetActivityByCityResponseJson.class);
                if (json.getStatusCode().equals(WebServices.SUCCESS_CODE)) {
                    ArrayList<ActivityDetailJson> activitiesList = json.getData().getActivities();
                    MySingleton.getInstance(FetchAllActivitiesService.this).setmAllAcivitiesList(activitiesList);
                    Paper.book().write(Constants.ALL_ACTIVITY_LIST, activitiesList);
                    PreferencesManager.getInstance(FetchAllActivitiesService.this).saveActivitiesTimeStamp(Calendar.getInstance().getTimeInMillis());
                } else {
                    Utilities.showToast(FetchAllActivitiesService.this, json.getStatusMsg());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
