package com.fitticket.viewmodel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.FavoriteGymPostRequest;
import com.fitticket.model.pojos.GetFavouriteGymResponse;
import com.fitticket.model.pojos.GymDataJson;
import com.fitticket.model.pojos.PostResponseJson;
import com.fitticket.model.services.FetchFavGymsService;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.adapters.ActivitiesAdapterNew;
import com.fitticket.viewmodel.adapters.GymPagerAdapter;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GymDetailsActivity extends AppCompatActivity implements ActivitiesAdapterNew.ActivitySelectedListener, Utilities.BookingSuccessfulListener {

    public static final String GYM_ARRAY_INDEX = "GYM_ARRAY_INDEX";
    private static final String TAG = GymDetailsActivity.class.getSimpleName();

    @Bind(R.id.gymViewPager)
    ViewPager mGymViewPager;
    @Bind(R.id.tabs)
    TabLayout mTabLayout;
    @Bind(R.id.gymBanner)
    NetworkImageView gymBannerImgVw;
    @Bind(R.id.distanceTxtView)
    TextView mDistanceTxtVw;
    @Bind(R.id.gymName)
    TextView mGymName;
    private GymDataJson mGymDetail;
    private GymPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_details_new);
        ButterKnife.bind(this);

        setUpNavigation();
        mGymDetail = MySingleton.getInstance(this).getSelectedGym();
        mGymName.setText(mGymDetail.getGymName());
        gymBannerImgVw.setDefaultImageResId(R.drawable.ic_default);
        gymBannerImgVw.setImageUrl(mGymDetail.getGymBanner()[0], MySingleton.getInstance(this).getImageLoader());
        mPagerAdapter = new GymPagerAdapter(getSupportFragmentManager(), mGymDetail.getGymId());
        if (MySingleton.getInstance(this).getMyLocation() != null &&
                !TextUtils.isEmpty(mGymDetail.getBranchLatitude()) &&
                !TextUtils.isEmpty(mGymDetail.getBranchLongitude())) {
            double myLat, myLong, gymLat, gymLong;
            myLat = MySingleton.getInstance(this).getMyLocation().getLatitude();
            myLong = MySingleton.getInstance(this).getMyLocation().getLongitude();
            gymLat = Double.parseDouble(mGymDetail.getBranchLatitude());
            gymLong = Double.parseDouble(mGymDetail.getBranchLongitude());
            long distance = Math.round(Utilities.distFrom(myLat, myLong, gymLat, gymLong));
            mDistanceTxtVw.setText("" + distance + " km");
        }
        mGymViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mGymViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gym_details, menu);
        setFavouriteIcon(menu);
        return true;
    }

    private void setFavouriteIcon(Menu menu) {
        ArrayList<GetFavouriteGymResponse.FavGymIdJson> favGymList = MySingleton.getInstance(this).getFavGymList();
        if (!favGymList.isEmpty()) {
            for (GetFavouriteGymResponse.FavGymIdJson favGym : favGymList) {
                if (favGym.getId() == mGymDetail.getGymId()) {
                    mGymDetail.setFavorite(true);
                    menu.findItem(R.id.action_fav).setIcon(R.drawable.ic_fav_on);
                    return;
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fav) {
            if (PreferencesManager.getInstance(this).getUserId() != 0) {
                if (mGymDetail.isFavorite()) {
                    item.setIcon(R.drawable.ic_fav_off);
                    markGymAsFavourite(mGymDetail.getGymId(), "0");
                    mGymDetail.setFavorite(false);

                } else {
                    item.setIcon(R.drawable.ic_fav_on);
                    markGymAsFavourite(mGymDetail.getGymId(), "1");
                    mGymDetail.setFavorite(true);
                }
            } else {
                Toast.makeText(this, "Please login to mark favorite gyms", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void markGymAsFavourite(int gymId, String favStatus) {
        FavoriteGymPostRequest json = new FavoriteGymPostRequest();
        json.setCustomerid(PreferencesManager.getInstance(this).getUserId());
        json.setGymid(gymId);
        json.setFavstatus(favStatus);
        WebServices.triggerVolleyPostRequest(this, json, Apis.MARK_GYM_FAV_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Fav gym's post response:" + response.toString());

                PostResponseJson result = new Gson().fromJson(response.toString(), PostResponseJson.class);
                if (result.getId() == 0) {
                    Toast.makeText(GymDetailsActivity.this, "Favourite updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GymDetailsActivity.this, FetchFavGymsService.class);
                    startService(intent);
                } else {
                    Toast.makeText(GymDetailsActivity.this, "Error !! Favourite could not be updated", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, FavoriteGymPostRequest.class);
    }

    @Override
    public void onActivitySelected(String activityId) {
        launchActivitiesDetailActivity(activityId);
    }

    private void launchActivitiesDetailActivity(String activityId) {
        Intent i = new Intent(this, ActivityDetailsActivity.class);
        i.putExtra(ActivityDetailsActivity.ACTIVITY_ID, activityId);
        startActivity(i);
        overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
    }

    protected void setUpNavigation() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBookingSucessful() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.SHOW_UPCOMING, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
