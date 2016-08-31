package com.fitticket.viewmodel.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.constants.Constants;
import com.fitticket.model.pojos.ActivityDetailJson;
import com.fitticket.model.pojos.GetGymsByLocationResponse;
import com.fitticket.model.pojos.GymDataJson;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.adapters.ActivitiesAdapter;
import com.fitticket.viewmodel.adapters.GymAdapter;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *  interface
 * to handle interaction events.
 * Use the {@link NearByFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearByFragment extends Fragment {


    private AppCompatActivity mParentActivity;
    private Location mLocation;
    //Butterknife binding of views
    @Bind(R.id.gyms_listview)
    RecyclerView mGymsListView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    private LinearLayoutManager mLayoutManager;
    ArrayList<GymDataJson> gymList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NearByFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearByFragment newInstance() {
        NearByFragment fragment = new NearByFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public NearByFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
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
        setHasOptionsMenu(true);

        mLocation= MySingleton.getInstance(mParentActivity).getMyLocation();
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mParentActivity);
        mGymsListView.setLayoutManager(mLayoutManager);

        gymList = MySingleton.getInstance(mParentActivity).getmAllGymsList();
        //if activities list in singleton is empty, trigger getActivitiesByLocation request,
        //else show all activities from singleton
        if (gymList.isEmpty()) {
            triggerGetGymsRequest();
        } else {
            GymAdapter adapter= new GymAdapter(mParentActivity,gymList);
            mGymsListView.setAdapter(adapter);
        }


    }

    private void triggerGetGymsRequest() {
        String url= Apis.GET_GYMS_BY_LOCATION+"?latitude="+mLocation.getLatitude()+"&longitude="+mLocation.getLongitude()+"&distance="+ Constants.NEARBY_DISTANCE;
//        String url= Apis.GET_GYMS_BY_LOCATION+"?latitude="+19.13+"&longitude="+72.83+"&distance="+ Constants.NEARBY_DISTANCE; //Mumbai lat long For testing
        mProgressBar.setVisibility(View.VISIBLE);
        WebServices.triggerVolleyGetRequest(mParentActivity, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressBar.setVisibility(View.GONE);
                GetGymsByLocationResponse json = new Gson().fromJson(response, GetGymsByLocationResponse.class);
                gymList = json.getData().getGyms();
                if (gymList != null) {
                    Location mLocation = MySingleton.getInstance(mParentActivity).getMyLocation();
                    if (mLocation != null) {
                        Utilities.sortGymList(gymList,mLocation);
                    }
                    MySingleton.getInstance(mParentActivity).setNearByGymsList(gymList);
                    GymAdapter adapter = new GymAdapter(mParentActivity, gymList);
                    mGymsListView.setAdapter(adapter);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                Utilities.handleVolleyError(mParentActivity, error);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_gym, menu);
        SearchManager searchManager = (SearchManager)
                mParentActivity.getSystemService(Context.SEARCH_SERVICE);

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(mParentActivity.getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(queryListener);
    }
    final private android.support.v7.widget.SearchView.OnQueryTextListener queryListener=new android.support.v7.widget.SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {

            refreshList(newText);
            return false;
        }
    };


    public void refreshList(String newText) {
        ArrayList<GymDataJson>filteredGymList=new ArrayList<GymDataJson>();
        if (!TextUtils.isEmpty(newText)&&gymList!=null&&gymList.size()>0){
            for (GymDataJson gymDataJson:gymList)
            {
                if (gymDataJson.getGymName().toLowerCase().contains(newText.toLowerCase())||
                        gymDataJson.getBranchLocation().toLowerCase().contains(newText.toLowerCase())||
                        gymDataJson.getBranchLocation().toLowerCase().contains(newText.toLowerCase())){
                    filteredGymList.add(gymDataJson);
                }
            }
                GymAdapter adapter = new GymAdapter(mParentActivity, filteredGymList);
                mGymsListView.setAdapter(adapter);

        }
        else if (TextUtils.isEmpty(newText)){
            if (gymList != null && !gymList.isEmpty()) {
                GymAdapter adapter = new GymAdapter(mParentActivity, gymList);
                mGymsListView.setAdapter(adapter);
            }
        }

    }
}
