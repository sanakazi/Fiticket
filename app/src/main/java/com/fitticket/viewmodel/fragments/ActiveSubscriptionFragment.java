package com.fitticket.viewmodel.fragments;

import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.constants.Constants;
import com.fitticket.model.pojos.ActiveSubDataJson;
import com.fitticket.model.pojos.GetActiveSubscriptionResponse;
import com.fitticket.model.pojos.GetFavouriteGymResponse;
import com.fitticket.model.pojos.GetGymsByLocationResponse;
import com.fitticket.model.pojos.GymDataJson;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.adapters.ActiveSubAdapter;
import com.fitticket.viewmodel.adapters.GymAdapter;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class ActiveSubscriptionFragment extends Fragment {
    private AppCompatActivity mParentActivity;
    private Location mLocation;
    //Butterknife binding of views
    @Bind(R.id.gyms_listview)
    RecyclerView mGymsListView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.nofavLayout)
    RelativeLayout noFavLayout;
    @Bind(R.id.noText)
    TextView mText;

    private LinearLayoutManager mLayoutManager;
    ArrayList<GymDataJson> gymList;
    private ArrayList<ActiveSubDataJson> activeList;


    public static ActiveSubscriptionFragment newInstance() {
        ActiveSubscriptionFragment fragment = new ActiveSubscriptionFragment();

        return fragment;
    }

    public ActiveSubscriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gymList=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_near_by, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentActivity=(AppCompatActivity)getActivity();
        mProgressBar.setVisibility(View.VISIBLE);
        //if user is not logged in disable navigation drawer
        if (PreferencesManager.getInstance(mParentActivity).getUserId()!=0) {
            /*ArrayList<GymDataJson> allGymList=MySingleton.getInstance(mParentActivity).getmAllGymsList();
            ArrayList<GetFavouriteGymResponse.FavGymIdJson> favGymList=MySingleton.getInstance(mParentActivity).getFavGymList();
            if (!favGymList.isEmpty()&&!allGymList.isEmpty()){
                for (GetFavouriteGymResponse.FavGymIdJson favGym:favGymList)
                {
                    for (GymDataJson gym:allGymList){
                        if (favGym.getId()==gym.getGymId()){
                            gymList.add(gym);
                        }
                    }

                }
                if (!gymList.isEmpty()){
                    // use a linear layout manager
                    mLayoutManager = new LinearLayoutManager(mParentActivity);
                    mGymsListView.setLayoutManager(mLayoutManager);
                    GymAdapter adapter= new GymAdapter(mParentActivity,gymList);
                    mGymsListView.setAdapter(adapter);
                }
            }
            else {
                noFavLayout.setVisibility(View.VISIBLE);
            }*/
            String userId= String.valueOf(PreferencesManager.getInstance(mParentActivity).getUserId());
            String url= Apis.GET_ACTIVE_SUB_URL+userId;
            WebServices.triggerVolleyGetRequest(getActivity(), url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.i("Response",response);

                    /*Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<ActiveSubDataJson>>(){}.getType();
                    ArrayList<ActiveSubDataJson> posts = (ArrayList<ActiveSubDataJson>) gson.fromJson(response, listType);*/
                    ActiveSubDataJson[] activeSubDataList = new Gson().fromJson(response, ActiveSubDataJson[].class);
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
                    }
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


                    //GetActiveSubscriptionResponse[] json = new Gson().fromJson(response, GetActiveSubscriptionResponse[].class);
                    //Log.i("Response",posts.toString());

                    // if (json.getStatusCode().equals(WebServices.SUCCESS_CODE)) {
//                        ActiveSubDataJson activeList = json.getData().getActive();
                        //Log.i("activeList",activeList.toString());
                        /*ArrayList<GymDataJson> gymsList = json.getData().getGyms();
                        MySingleton.getInstance(getActivity()).setmAllGymsList(gymsList);
                        Paper.book().write(Constants.ALL_GYM_LIST, gymsList);
                        PreferencesManager.getInstance(getActivity())
                                .saveActivitiesTimeStamp(Calendar.getInstance().getTimeInMillis());*/

                    /*} else {
                        //Utilities.showToast(FetchAllGymsService.this, json.getStatusMsg());
                    }*/
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressBar.setVisibility(View.GONE);
                    Utilities.handleVolleyError(getActivity(), error);
                }
            });
        }
    }
}
