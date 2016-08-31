package com.fitticket.viewmodel.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.Gson;

public class BuySubscriptionActivity extends AppCompatActivity {

    private ListView mLstSubscription;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_subscription);
        mLstSubscription = (ListView)findViewById(R.id.lstSubscription);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        String userId= String.valueOf(PreferencesManager.getInstance(BuySubscriptionActivity.this).getUserId());
        String url= Apis.GET_ACTIVE_SUB_URL+userId;
        WebServices.triggerVolleyGetRequest(BuySubscriptionActivity.this, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("Response",response);

                    /*Gson gson = new Gson();

                /*ActiveSubDataJson[] activeSubDataList = new Gson().fromJson(response, ActiveSubDataJson[].class);
                if (activeSubDataList == null || activeSubDataList.length == 0) {
                    mProgressBar.setVisibility(View.GONE);
                    mText.setText("No Active Subscription");
                    noFavLayout.setVisibility(View.VISIBLE);
                }else {
                    mProgressBar.setVisibility(View.GONE);
                    mLayoutManager = new LinearLayoutManager(mParentActivity);
                    mGymsListView.setLayoutManager(mLayoutManager);
                    ActiveSubAdapter adapter= new ActiveSubAdapter(mParentActivity,activeSubDataList);
                    mGymsListView.setAdapter(adapter);
                }*/



                /*try{
                        activeList = new ArrayList<ActiveSubDataJson>();
                        JSONArray jsonarray = new JSONArray(response);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            ActiveSubDataJson activeSubDataJson = new ActiveSubDataJson();
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            String endDate = jsonobject.getString("endDate");
                            String packageName = jsonobject.getString("packageName");
                            String purchaseDate = jsonobject.getString("purchaseDate");
                            String purchasePrice = jsonobject.getString("purchasePrice");
                            String startDate = jsonobject.getString("startDate");
                            activeSubDataJson.setEndDate(endDate);
                            activeSubDataJson.setPackageName(packageName);
                            activeSubDataJson.setPurchaseDate(purchaseDate);
                            activeSubDataJson.setPurchasePrice(purchasePrice);
                            activeSubDataJson.setStartDate(startDate);
                            activeList.add(activeSubDataJson);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if (!activeList.isEmpty()){
                        mProgressBar.setVisibility(View.GONE);
                        mLayoutManager = new LinearLayoutManager(mParentActivity);
                        mGymsListView.setLayoutManager(mLayoutManager);
                        ActiveSubAdapter adapter= new ActiveSubAdapter(mParentActivity,activeList);
                        mGymsListView.setAdapter(adapter);
                    }
                        else {
                            mProgressBar.setVisibility(View.GONE);
                            mText.setText("No Active Subscription");
                            noFavLayout.setVisibility(View.VISIBLE);
                        }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                Utilities.handleVolleyError(BuySubscriptionActivity.this, error);
            }
        });

    }

}
