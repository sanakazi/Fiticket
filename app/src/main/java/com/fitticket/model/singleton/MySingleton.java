package com.fitticket.model.singleton;

import android.content.Context;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.fitticket.model.constants.Constants;
import com.fitticket.model.pojos.ActivityDetailJson;
import com.fitticket.model.pojos.CategoryJson;
import com.fitticket.model.pojos.GetOperatingCitiesResponse;
import com.fitticket.model.pojos.GymDataJson;
import com.fitticket.model.pojos.GetFavouriteGymResponse;
import com.fitticket.viewmodel.utils.LruBitmapCache;

import java.util.ArrayList;

import io.paperdb.Paper;

public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Location myLocation;
    private static Context mContext;
    private ArrayList<ActivityDetailJson> mAllAcivitiesList;
    private ArrayList<GymDataJson> mAllGymsList;
    private ArrayList<GymDataJson> mNearByGymsList;
    private ArrayList<CategoryJson> categoryList;
    private GymDataJson selectedGym;
    private ArrayList<GetFavouriteGymResponse.FavGymIdJson> favGymList;
    private ArrayList<GetOperatingCitiesResponse.CitiesJson> operatingCities;

    public void setmMapAcivitiesList(ArrayList<ActivityDetailJson> mMapAcivitiesList) {
        this.mMapAcivitiesList = mMapAcivitiesList;
    }

    private ArrayList<ActivityDetailJson> mMapAcivitiesList;

    public ArrayList<GetOperatingCitiesResponse.CitiesJson> getOperatingCities() {
        return operatingCities;
    }

    public void setOperatingCities(ArrayList<GetOperatingCitiesResponse.CitiesJson> operatingCities) {
        this.operatingCities = operatingCities;
    }

    private MySingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,new LruBitmapCache(LruBitmapCache.getCacheSize(mContext)));
        mAllAcivitiesList= Paper.book().read(Constants.ALL_ACTIVITY_LIST,new ArrayList<ActivityDetailJson>());
        mAllGymsList=Paper.book().read(Constants.ALL_GYM_LIST,new ArrayList<GymDataJson>());
        mNearByGymsList= new ArrayList<>();
        categoryList= new ArrayList<>();
        favGymList= new ArrayList<>();
        operatingCities=new ArrayList<>();
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());

        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public Location getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }

    public ArrayList<ActivityDetailJson> getmAllAcivitiesList() {
        return mAllAcivitiesList;
    }

    public void setmAllAcivitiesList(ArrayList<ActivityDetailJson> mAllAcivitiesList) {
        this.mAllAcivitiesList = mAllAcivitiesList;
    }

    public ArrayList<GymDataJson> getmAllGymsList() {
        return mAllGymsList;
    }

    public void setmAllGymsList(ArrayList<GymDataJson> mAllGymsList) {
        this.mAllGymsList = mAllGymsList;
    }

    public void setNearByGymsList(ArrayList<GymDataJson> nearByGymsList) {
        this.mNearByGymsList = nearByGymsList;
    }

    public ArrayList<GymDataJson> getmNearByGymsList() {
        return mNearByGymsList;
    }


    public void setCategoryList(ArrayList<CategoryJson> categoryList) {
        this.categoryList = categoryList;
    }

    public ArrayList<CategoryJson> getCategoryList() {
        return categoryList;
    }

    public void setSelectedGym(GymDataJson selectedGym) {
        this.selectedGym = selectedGym;
    }

    public GymDataJson getSelectedGym() {
        return selectedGym;
    }

    public void setFavGymList(ArrayList<GetFavouriteGymResponse.FavGymIdJson> favGymList) {
        this.favGymList = favGymList;
    }

    public ArrayList<GetFavouriteGymResponse.FavGymIdJson> getFavGymList() {
        return favGymList;
    }

    public ArrayList<ActivityDetailJson> getmMapAcivitiesList() {
        return mMapAcivitiesList;
    }


    public void clearApplicationData() {
        Paper.book().delete(Constants.ALL_ACTIVITY_LIST);
        Paper.book().delete(Constants.ALL_GYM_LIST);
    }
}