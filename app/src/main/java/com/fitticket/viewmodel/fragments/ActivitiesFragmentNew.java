package com.fitticket.viewmodel.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.ActivityDetailJson;
import com.fitticket.model.pojos.GetActivityByGymResponse;
import com.fitticket.model.pojos.GetActivityByLocationResponse;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.activities.MapActivity;
import com.fitticket.viewmodel.adapters.ActivitiesAdapterNew;
import com.fitticket.viewmodel.custom.RangeSeekBar;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ActivitiesFragmentNew extends Fragment {

    public static final int FROM_MAIN_ACTIVITY = 0;
    public static final int FROM_NEARBY = 1;
    public static final int FROM_MAP = 2;
    public static final int FROM_CATEGORY = 3;
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String CAT_ID = "CAT_ID";
    private static final String GET_URL = "GET_URL";
    private static final String GYM_ACTIVITY_FLAG = "GYM_ACTIVITY_FLAG";
    private static final String TAG = ActivitiesFragment.class.getSimpleName();
    private static final String FROM_FLAG = "FROM_FLAG";

    //Butterknife binding of views
    @Bind(R.id.activities_listview)
    RecyclerView mActivityListView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.timePickerTextview)
    TextView timePickerTextView;
    @Bind(R.id.sliderNumber)
    RangeSeekBar<Integer> numberSlider;
    @Bind(R.id.daysLayout)
    LinearLayout daysLayout;
    @Bind(R.id.noActLayout)
    RelativeLayout noActLayout;
    @Bind(R.id.map_fab)
    FloatingActionButton mapFab;
    int dayOfTheWeek;
    ArrayList<ActivityDetailJson> allActivitiesList;
    ArrayList<ActivityDetailJson> currentActivitiesList;
    ArrayList<ArrayList<ActivityDetailJson>> sevenDaysList;
    int dayOfTheWeekToday;
    Calendar c;
    String searchString = "";
    String latitude, longitude, catId;
    private int[] dayViewArray = {
            R.id.day1,
            R.id.day2,
            R.id.day3,
            R.id.day4,
            R.id.day5,
            R.id.day6,
            R.id.day7,
    };
    private int[] dayNameViewArray = {
            R.id.day1Name,
            R.id.day2Name,
            R.id.day3Name,
            R.id.day4Name,
            R.id.day5Name,
            R.id.day6Name,
            R.id.day7Name,
    };
    private AppCompatActivity mParentActivity;
    private LinearLayoutManager mLayoutManager;
    private String mUrl;
    private boolean mGymActivityFlag;
    private int startTime;
    private int endTime;
    final private android.support.v7.widget.SearchView.OnQueryTextListener queryListener = new android.support.v7.widget.SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            searchString = newText;
            refreshList(newText);
            return false;
        }
    };
    private int radius = 25;
    private Location mLocation;
    private MenuItem timeFilter;
    private MenuItem categoryFilter;
    private int from;
    private int currentHour;

    public ActivitiesFragmentNew() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param from - tells from where the fragment was called
     * @param url- url for get request
     * @return A new instance of fragment ActivitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivitiesFragmentNew newInstance(int from, String url) {
        ActivitiesFragmentNew fragment = new ActivitiesFragmentNew();
        Bundle args = new Bundle();
        args.putString(GET_URL, url);
        args.putInt(FROM_FLAG, from);
        fragment.setArguments(args);
        return fragment;
    }

    public static ActivitiesFragmentNew newInstance(int from, String url, String latitude, String longitude, String cateId) {
        ActivitiesFragmentNew fragment = new ActivitiesFragmentNew();
        Bundle args = new Bundle();
        args.putString(GET_URL, url);
        args.putInt(FROM_FLAG, from);
        args.putString(LATITUDE, latitude);
        args.putString(LONGITUDE, longitude);
        args.putString(CAT_ID, cateId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString(GET_URL);
        from = getArguments().getInt(FROM_FLAG);
        latitude = getArguments().getString(LATITUDE);
        catId = getArguments().getString(CAT_ID);
        longitude = getArguments().getString(LONGITUDE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activities_new, container, false);
        ButterKnife.bind(this, view);
        numberSlider.setRangeValues(new Integer(4), new Integer(23));
        startTime = 0;
        endTime = 24;

        numberSlider.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                startTime = minValue;
                endTime = maxValue;
                if (searchString.equals("")) {
                    updateActivitiesListWithTimeFilter(currentActivitiesList, startTime, endTime);
                } else {
                    refreshList(searchString);
                }
            }
        });

        if (from == FROM_CATEGORY) {
            mapFab.setVisibility(View.VISIBLE);
        } else {
            mapFab.setVisibility(View.GONE);

        }

        mapFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityMapFragment fragment = ActivityMapFragment.newInstance(MapActivity.latitude, MapActivity.longitude, MapActivity.catId);
                fragment.setMyLocation(MapActivity.location);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
            }
        });

        return view;
    }

    private void updateActivitiesListWithTimeFilter(ArrayList<ActivityDetailJson> activitiesList, int startTime, int endTime) {
        if (!activitiesList.isEmpty()) {
            ArrayList<ActivityDetailJson> filteredActivityList = new ArrayList<>();
            //is start time is more than or equal to 23 hrs, do not show actvities
            if (startTime < 23) {
                for (ActivityDetailJson activity : activitiesList) {

                    ArrayList<ActivityDetailJson.ScheduleDetail> timeSlots = new ArrayList<>();
                    for (int i = 0; i < activity.getSchedule().get(dayOfTheWeek - 1).size(); i++) {
                        if (activity.getSchedule().get(dayOfTheWeek - 1).get(i).getIsFullDay().equalsIgnoreCase("true")) {
                            filteredActivityList.add(activity);
                            break;
                        } else {
                            int hour = Integer.parseInt(activity.getSchedule().get(dayOfTheWeek - 1).get(i).getStartTime().substring(0, 2));
                            if (hour >= startTime && hour < endTime) {
                                filteredActivityList.add(activity);
                                break;
                            }
                        }
                    }
                }
            }
            setActivitiesAdapter(filteredActivityList, c, startTime, endTime);
        } else {
            noActLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_Timer:
                if (timePickerTextView.getVisibility() == View.GONE) {
                    timePickerTextView.setVisibility(View.VISIBLE);
                    numberSlider.setVisibility(View.VISIBLE);
                } else {
                    timePickerTextView.setVisibility(View.GONE);
                    numberSlider.setVisibility(View.GONE);
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentActivity = (AppCompatActivity) getActivity();
        mLocation = MySingleton.getInstance(mParentActivity).getMyLocation();
        setHasOptionsMenu(true);
        initializeSevenDaysList();
        c = Calendar.getInstance();
        dayOfTheWeekToday = c.get(Calendar.DAY_OF_WEEK);
        currentHour = c.get(Calendar.HOUR_OF_DAY);
        numberSlider.setSelectedMinValue(currentHour);
        Utilities.setDayViews(daysLayout);
        Utilities.setSelectedDay(mParentActivity, 0, daysLayout);
        addDayClickListeners(mParentActivity, daysLayout);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mParentActivity);
        mActivityListView.setLayoutManager(mLayoutManager);
        //if Fragment request is from GymDetails Activity, trigger getActivitiesByGym request,else get activities list from singleton
        if (from == FROM_NEARBY) {
            triggerGetActivitiesRequest();
        } else if (from == FROM_MAIN_ACTIVITY) {
            allActivitiesList = MySingleton.getInstance(mParentActivity).getmAllAcivitiesList();
            if (allActivitiesList.isEmpty()) {
                triggerGetActivitiesRequest();
            } else {
                fillSevenDaysList();
                dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
                currentActivitiesList = sevenDaysList.get(dayOfTheWeek - 1);
                //if activities list in singleton is empty, trigger getActivitiesByLocation request,
                //else show all activities from singleton
                updateActivitiesListWithTimeFilter(currentActivitiesList, currentHour, endTime);
            }
        } else if (from == FROM_MAP) {
            allActivitiesList = MySingleton.getInstance(mParentActivity).getmMapAcivitiesList();
            if (mLocation != null && allActivitiesList != null) {
                Utilities.sortActivitiesList(allActivitiesList, mLocation);
            }
            if (allActivitiesList != null && !allActivitiesList.isEmpty()) {
                fillSevenDaysList();
                dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
                currentActivitiesList = sevenDaysList.get(dayOfTheWeek - 1);
                //if activities list in singleton is empty, trigger getActivitiesByLocation request,
                //else show all activities from singleton
                updateActivitiesListWithTimeFilter(currentActivitiesList, currentHour, endTime);
            }
        } else if (from == FROM_CATEGORY) {
            triggerVolleyRequestForActivitiesList();
        }
    }

    private void triggerVolleyRequestForActivitiesList() {
        String url = Apis.GET_ACTIVITIES_BY_LOCATION_URL + "?latitude=" + latitude + "&longitude=" + longitude +
                "&category=" + catId + "&distance=" + radius;
        Log.d(TAG, "Get activities by location URL: " + url);
        WebServices.triggerVolleyGetRequest(mParentActivity, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Activities by loc Response: " + response);
                Gson gson = new Gson();
                GetActivityByLocationResponse jsonResponse = gson.fromJson(response, GetActivityByLocationResponse.class);
                if (jsonResponse.getStatusCode().equalsIgnoreCase("0")) {
                    allActivitiesList = jsonResponse.getData().getActivities();
                    if (mLocation != null && allActivitiesList != null) {
                        Utilities.sortActivitiesList(allActivitiesList, mLocation);
                    }
                    if (allActivitiesList != null && !allActivitiesList.isEmpty()) {
                        fillSevenDaysList();
                        dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
                        currentActivitiesList = sevenDaysList.get(dayOfTheWeek - 1);
                        //if activities list in singleton is empty, trigger getActivitiesByLocation request,
                        //else show all activities from singleton
                        updateActivitiesListWithTimeFilter(currentActivitiesList, currentHour, endTime);
                    }


                } else {
                    Log.e(TAG, jsonResponse.getStatusMsg());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "" + error.getLocalizedMessage());
            }
        });
    }


    private void fillSevenDaysList() {
        // Iterate through the entire activity list and check the schedule array for each week day
        // If schedule array is not empty add the activity to corresponding arrayList for the day of the week
        for (ActivityDetailJson activity : allActivitiesList) {
            for (int i = 0; i < 7; i++) {
                if (!activity.getSchedule().get(i).isEmpty()) {
                    sevenDaysList.get(i).add(activity);
                }
            }

        }
    }

    private void initializeSevenDaysList() {
        sevenDaysList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            ArrayList<ActivityDetailJson> json = new ArrayList<>();
            sevenDaysList.add(json);
        }
    }

    private void setActivitiesAdapter(ArrayList<ActivityDetailJson> activitiesList, Calendar c, int startTime, int endTime) {
        if (activitiesList != null && !activitiesList.isEmpty()) {
            ActivitiesAdapterNew adapter = new ActivitiesAdapterNew(mParentActivity, activitiesList, dayOfTheWeek, c, startTime, endTime);
            mActivityListView.setAdapter(adapter);
            noActLayout.setVisibility(View.GONE);
        } else {
            noActLayout.setVisibility(View.VISIBLE);
        }
    }

    private void triggerGetActivitiesRequest() {
        mProgressBar.setVisibility(View.VISIBLE);
        Log.e(TAG, "Get Activities URL: " + mUrl);


        WebServices.triggerVolleyGetRequest(mParentActivity, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Get Activities Response: " + response);
                mProgressBar.setVisibility(View.GONE);
                allActivitiesList = new ArrayList<ActivityDetailJson>();
                Gson gson = new Gson();
                if (from == FROM_NEARBY) {
                    GetActivityByGymResponse jsonGymResponse = gson.fromJson(response, GetActivityByGymResponse.class);
                    if (jsonGymResponse.getStatusCode().equalsIgnoreCase(WebServices.SUCCESS_CODE)) {
                        allActivitiesList = jsonGymResponse.getData().getActivities();
                        if (mLocation != null && allActivitiesList != null)
                            Utilities.sortActivitiesList(allActivitiesList, mLocation);
                    } else {
                        Log.e(TAG, jsonGymResponse.getStatusMsg());
                    }
                } else {
                    GetActivityByLocationResponse jsonResponse = gson.fromJson(response, GetActivityByLocationResponse.class);
                    if (jsonResponse.getStatusCode().equalsIgnoreCase(WebServices.SUCCESS_CODE)) {
                        allActivitiesList = jsonResponse.getData().getActivities();
                        if (mLocation != null && allActivitiesList != null)
                            Utilities.sortActivitiesList(allActivitiesList, mLocation);
                    } else {
                        Log.e(TAG, jsonResponse.getStatusMsg());
                    }
                }


                if (allActivitiesList != null && !allActivitiesList.isEmpty()) {
                    fillSevenDaysList();
                    Calendar c = Calendar.getInstance();
                    dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
                    currentActivitiesList = sevenDaysList.get(dayOfTheWeek - 1);
                    updateActivitiesListWithTimeFilter(currentActivitiesList, currentHour, endTime);
                } else {
                    noActLayout.setVisibility(View.VISIBLE);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_activities, menu);
        SearchManager searchManager = (SearchManager)
                mParentActivity.getSystemService(Context.SEARCH_SERVICE);

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(mParentActivity.getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Area\\Venue\\Activity");
        searchView.setOnQueryTextListener(queryListener);
        timeFilter = menu.findItem(R.id.action_Timer);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });


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

    public void refreshList(String newText) {
        ArrayList<ActivityDetailJson> filteredActivitiesList = new ArrayList<ActivityDetailJson>();
        if (!TextUtils.isEmpty(newText) && currentActivitiesList != null && currentActivitiesList.size() > 0) {
            for (ActivityDetailJson activityDetailJson : currentActivitiesList) {
                if (activityDetailJson.getActivityName().toLowerCase().contains(newText.toLowerCase()) ||
                        activityDetailJson.getGymName().toLowerCase().contains(newText.toLowerCase()) ||
                        activityDetailJson.getGymLocation().toLowerCase().contains(newText.toLowerCase()) ||
                        activityDetailJson.getActivityCategory().toLowerCase().contains(newText.toLowerCase())) {
                    filteredActivitiesList.add(activityDetailJson);
                }
            }
            updateActivitiesListWithTimeFilter(filteredActivitiesList, startTime, endTime);
        } else if (TextUtils.isEmpty(newText)) {
            updateActivitiesListWithTimeFilter(currentActivitiesList, startTime, endTime);
        }

    }

    public void addDayClickListeners(final AppCompatActivity activity, final LinearLayout daysLayout) {
        for (int i = 0; i < dayViewArray.length; i++) {
            final int j = i;
            daysLayout.findViewById(dayViewArray[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int startTime;
                    c.setTime(new Date());
                    c.add(Calendar.DATE, j);
                    dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
                    if (dayOfTheWeek == dayOfTheWeekToday) {
                        //if dayoftheweek is today, set current hour as start time
                        startTime = currentHour > numberSlider.getSelectedMinValue() ? currentHour : numberSlider.getSelectedMinValue();
                    } else {
                        startTime = ActivitiesFragmentNew.this.startTime;
                    }
                    Utilities.setSelectedDay(activity, j, daysLayout);
                    currentActivitiesList = sevenDaysList.get(dayOfTheWeek - 1);
                    if (searchString.equals("")) {
                        updateActivitiesListWithTimeFilter(currentActivitiesList, startTime, endTime);
                    } else {
                        refreshList(searchString);
                    }
                }
            });
        }
    }

}
