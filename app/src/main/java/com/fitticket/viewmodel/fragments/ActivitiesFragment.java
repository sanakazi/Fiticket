package com.fitticket.viewmodel.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.R;
import com.fitticket.model.pojos.ActivityDetailJson;
import com.fitticket.model.pojos.GetActivityByGymResponse;
import com.fitticket.model.pojos.GetActivityByLocationResponse;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.adapters.ActivitiesAdapter;
import com.fitticket.viewmodel.custom.RangeSeekBar;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ActivitiesFragment extends Fragment {

    private static final String GET_URL = "GET_URL";
    private static final String GYM_ACTIVITY_FLAG ="GYM_ACTIVITY_FLAG" ;
    private static final String TAG = ActivitiesFragment.class.getSimpleName();
    //Butterknife binding of views
    @Bind(R.id.activities_listview)
    RecyclerView mActivityListView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.timePickerTextview)
    TextView timePickerTextView;
    @Bind(R.id.sliderNumber)
    RangeSeekBar<Integer> numberSlider;


    private AppCompatActivity mParentActivity;
    private LinearLayoutManager mLayoutManager;
    private String mUrl;
    private boolean mGymActivityFlag;
    ArrayList<ActivityDetailJson> activitiesList;
    private int startTime;
    private int endTime;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *@param gymActivityFlag - if true, the fragment is for GymDetail Activity, else it is for Activities Tab
     *@param url- url for get request
     * @return A new instance of fragment ActivitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivitiesFragment newInstance(boolean gymActivityFlag,String url) {
        ActivitiesFragment fragment = new ActivitiesFragment();
        Bundle args = new Bundle();
        args.putString(GET_URL, url);
        args.putBoolean(GYM_ACTIVITY_FLAG, gymActivityFlag);
        fragment.setArguments(args);
        return fragment;
    }

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl=getArguments().getString(GET_URL);
        mGymActivityFlag=getArguments().getBoolean(GYM_ACTIVITY_FLAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_activities, container, false);
        ButterKnife.bind(this, view);
        numberSlider.setRangeValues(new Integer(6), new Integer(23));
        startTime = numberSlider.getAbsoluteMinValue();
        endTime = numberSlider.getAbsoluteMaxValue();

        numberSlider.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                startTime = minValue;
                endTime = maxValue;
                updateActivitiesList(startTime,endTime);
                setActivitiesAdapter();
            }
        });
        return view;
    }

    private void updateActivitiesList(int startTime, int endTime) {
        if (!activitiesList.isEmpty()){
            ArrayList<ActivityDetailJson> filteredActivityList=new ArrayList<>();
            for (ActivityDetailJson activity:activitiesList){
                for (int i=0;i<activity.getSchedule().size();i++){

                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_Timer:
                if (timePickerTextView.getVisibility()==View.GONE){
                    timePickerTextView.setVisibility(View.VISIBLE);
                    numberSlider.setVisibility(View.VISIBLE);
                }
                else {
                    timePickerTextView.setVisibility(View.GONE);
                    numberSlider.setVisibility(View.GONE);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentActivity= (AppCompatActivity)getActivity();
        setHasOptionsMenu(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mParentActivity);
        mActivityListView.setLayoutManager(mLayoutManager);
        //if Fragment request is from GymDetails Activity, trigger getActivitiesByGym request,else get activities list from singleton
        if(mGymActivityFlag){
            triggerGetActivitiesRequest();
        }else {
            activitiesList = MySingleton.getInstance(mParentActivity).getmAllAcivitiesList();
            //if activities list in singleton is empty, trigger getActivitiesByLocation request,
            //else show all activities from singleton
            setActivitiesAdapter();

        }

    }

    private void setActivitiesAdapter() {
        if (activitiesList.isEmpty()) {
            triggerGetActivitiesRequest();
        } else {
            ActivitiesAdapter adapter= new ActivitiesAdapter(mParentActivity,activitiesList);
            mActivityListView.setAdapter(adapter);
        }
    }

    private void triggerGetActivitiesRequest() {
        mProgressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Get Activities URL: " + mUrl);


        WebServices.triggerVolleyGetRequest(mParentActivity, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Activities Response: " + response);
                mProgressBar.setVisibility(View.GONE);
                activitiesList = new ArrayList<ActivityDetailJson>();
                Gson gson = new Gson();
                if (mGymActivityFlag) {
                    GetActivityByGymResponse jsonGymResponse = gson.fromJson(response, GetActivityByGymResponse.class);
                    if (jsonGymResponse.getStatusCode().equalsIgnoreCase(WebServices.SUCCESS_CODE)) {
                        activitiesList = jsonGymResponse.getData().getActivities();
                    } else {
                        Log.e(TAG, jsonGymResponse.getStatusMsg());
                    }
                } else {
                    GetActivityByLocationResponse jsonResponse = gson.fromJson(response, GetActivityByLocationResponse.class);
                    if (jsonResponse.getStatusCode().equalsIgnoreCase(WebServices.SUCCESS_CODE)) {
                        activitiesList = jsonResponse.getData().getActivities();
                    } else {
                        Log.e(TAG, jsonResponse.getStatusMsg());
                    }
                }
                if (activitiesList != null && !activitiesList.isEmpty()) {
                    ActivitiesAdapter adapter = new ActivitiesAdapter(mParentActivity, activitiesList);
                    mActivityListView.setAdapter(adapter);
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
        inflater.inflate(R.menu.menu_activities, menu);
        SearchManager searchManager = (SearchManager)
                mParentActivity.getSystemService(Context.SEARCH_SERVICE);

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(mParentActivity.getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(queryListener);

        /*// Catch event on [x] button inside search view
        int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) searchView.findViewById(searchCloseButtonId);
// Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Manage this event.
                if (allActivitiesList != null && !allActivitiesList.isEmpty()) {
                    ActivitiesAdapter adapter = new ActivitiesAdapter(mParentActivity, allActivitiesList);
                    mActivityListView.setAdapter(adapter);
                }
            }
        });*/

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
        ArrayList<ActivityDetailJson>filteredActivitiesList=new ArrayList<ActivityDetailJson>();
        if (!TextUtils.isEmpty(newText)&&activitiesList!=null&&activitiesList.size()>0){
            for (ActivityDetailJson activityDetailJson:activitiesList)
            {
                if (activityDetailJson.getActivityName().toLowerCase().contains(newText.toLowerCase())||
                        activityDetailJson.getGymName().toLowerCase().contains(newText.toLowerCase())){
                    filteredActivitiesList.add(activityDetailJson);
                }
            }
            ActivitiesAdapter adapter = new ActivitiesAdapter(mParentActivity, filteredActivitiesList);
            mActivityListView.setAdapter(adapter);
        }else if (TextUtils.isEmpty(newText)){
            if (activitiesList != null && !activitiesList.isEmpty()) {
                ActivitiesAdapter adapter = new ActivitiesAdapter(mParentActivity, activitiesList);
                mActivityListView.setAdapter(adapter);
            }
        }

    }

}
