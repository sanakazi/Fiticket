package com.fitticket.model.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.constants.Constants;
import com.fitticket.model.pojos.CategoryJson;
import com.fitticket.model.pojos.CategoryJsonResponse;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.activities.SplashActivity;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Fiticket on 17/03/16.
 */
public class FetchCategoriesService extends IntentService {
    private static final String TAG = FetchCategoriesService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public FetchCategoriesService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String cityId= PreferencesManager.getInstance(this).getSelectedCityId();
        Log.d(TAG, "Get categories Requst: " + Apis.GET_CATEGORIES_GROUP_URL + cityId);
        WebServices.triggerVolleyGetRequest(this, Apis.GET_CATEGORIES_GROUP_URL + cityId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Get categories response: " + response);
                        Gson gson = new Gson();
                        CategoryJsonResponse jsonResponse = gson.fromJson(response, CategoryJsonResponse.class);
                        if (jsonResponse.getStatusCode().equalsIgnoreCase("0")) {
                            if (jsonResponse.getData() != null && jsonResponse.getData().getCategories() != null
                                    && !jsonResponse.getData().getCategories().isEmpty()) {
                                ArrayList<CategoryJson> mCategoryList = addFavorites(jsonResponse.getData().getCategories());
                                MySingleton.getInstance(FetchCategoriesService.this).setCategoryList(mCategoryList);

                            }

                        } else {
                            Log.e(TAG, jsonResponse.getStatusMsg());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utilities.handleVolleyError(FetchCategoriesService.this, error);
                    }
                });
    }

    //Method adds the favorite status to activities based on previous user preference
    private ArrayList<CategoryJson> addFavorites(ArrayList<CategoryJson> categories) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> favSet= mPrefs.getStringSet(Constants.FAVORITE, new HashSet<String>());
        if(!favSet.isEmpty()){
            for(CategoryJson category: categories){
                category.setIsFavorite(favSet.contains(category));
            }
        }
        return categories;
    }
}
