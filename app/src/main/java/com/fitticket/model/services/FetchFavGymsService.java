package com.fitticket.model.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.GetFavouriteGymResponse;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.WebServices;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Fiticket on 07/03/16.
 */
public class FetchFavGymsService extends IntentService {

    private static final String TAG = FetchFavGymsService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public FetchFavGymsService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int userId=PreferencesManager.getInstance(this).getUserId();
        if (userId!=0) {
            Log.w("userid is", userId + " ");

            String url = Apis.GET_FAVORITE_GYM_URL + userId;
            WebServices.triggerVolleyGetRequest(this, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG,"Fav gym's get response:"+response);
                    GetFavouriteGymResponse json = new Gson().fromJson(response, GetFavouriteGymResponse.class);
                    ArrayList<GetFavouriteGymResponse.FavGymIdJson> favGymList = json.getGetFavouriteGymListResult();
                    MySingleton.getInstance(FetchFavGymsService.this).setFavGymList(favGymList);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

        }
    }
}
