package com.fitticket.viewmodel.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.ActivityDetailJson;
import com.fitticket.model.pojos.GetActivityDataResponse;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActivityDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityDetailFragment extends Fragment implements OnMapReadyCallback {
    private static final String ACTIVITY_ID = "ACTIVITY_ID";
    private static final String TAG = ActivityDetailFragment.class.getSimpleName();
    ImageLoader imageLoader;
    //Views
    View fragmentView;
    GoogleMap mMap;
    //Butterknife binding of views
    @Bind(R.id.gymName)
    TextView mGymNameTxtVw;
    @Bind(R.id.distanceTxtView)
    TextView mDistanceTxtVw;
    @Bind(R.id.about_desc)
    TextView mAboutTxtVw;
    @Bind(R.id.towel_icon)
    TextView mTowelIconTxtVw;
    @Bind(R.id.mat_icon)
    TextView mMatIcontxtVw;
    @Bind(R.id.thingsToCarry)
    TextView mThingsToCarryTV;
    @Bind(R.id.id_proof_icon)
    TextView mIdIconTxtVw;
    @Bind(R.id.addressTxtView)
    TextView mAddressTxtVw;
    @Bind(R.id.daysLayout)
    LinearLayout mDaysLayout;
    @Bind(R.id.timeSlotLayout)
    LinearLayout mTimeSlotLayout;
    @Bind(R.id.activityBanner)
    NetworkImageView mGymBannerImgVw;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    //model
    ActivityDetailJson mActivityDetailJson;
    private AppCompatActivity parentActivity;
    private String mActivityId;

    private OnFragmentInteractionListener mListener;

    public ActivityDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ActivityDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityDetailFragment newInstance(String activityId) {
        ActivityDetailFragment fragment = new ActivityDetailFragment();
        Bundle args = new Bundle();
        args.putString(ACTIVITY_ID, activityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mActivityId = getArguments().getString(ACTIVITY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_activity_detail, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        String data = intent.getDataString();

        Uri datauri = intent.getData();


        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            String productId = data.substring(data.lastIndexOf("/") + 1);
            Log.e("productId", "productId" + productId);
//            Uri contentUri = PetstoreContentProvider.CONTENT_URI.buildUpon()
//                    .appendPath(productId).build();

        }


        parentActivity = (AppCompatActivity) getActivity();
        imageLoader = MySingleton.getInstance(parentActivity).getImageLoader();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setToolbar("");
        setUpNavigation();
        ArrayList<ActivityDetailJson> activitiesByCityList = MySingleton.getInstance(parentActivity).getmAllAcivitiesList();
        for (ActivityDetailJson activity : activitiesByCityList) {
            if (activity.getId().equals(mActivityId)) {
                mActivityDetailJson = activity;
                filldataInViews(activity);
            }
        }
        if (mActivityDetailJson == null) {
            progressBar.setVisibility(View.VISIBLE);
            WebServices.triggerVolleyGetRequest(parentActivity, Apis.GET_ACTIVITY_DATA_URL + mActivityId,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, response);
                            GetActivityDataResponse json = new Gson().fromJson(response, GetActivityDataResponse.class);
                            mActivityDetailJson = json.getData().getActivityData();
                            if (mActivityDetailJson != null)
                                filldataInViews(mActivityDetailJson);
                            progressBar.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            Utilities.handleVolleyError(parentActivity, error);
                        }
                    });
        }
    }

    //populate the views with data
    private void filldataInViews(ActivityDetailJson activityDetailJson) {
        setToolbar(activityDetailJson.getActivityName());
        mAboutTxtVw.setText(activityDetailJson.getActivityDesc());
        mAddressTxtVw.setText(activityDetailJson.getGymAddress());
        Linkify.addLinks(mAddressTxtVw, Linkify.PHONE_NUMBERS);
        mGymNameTxtVw.setText(activityDetailJson.getGymName());

        mGymBannerImgVw.setDefaultImageResId(R.drawable.ic_default);
        if (!TextUtils.isEmpty(activityDetailJson.getActImages()))
            mGymBannerImgVw.setImageUrl(activityDetailJson.getActImages(), imageLoader);
        String[] requirementsArray = activityDetailJson.getRequirement();
        if (requirementsArray != null) {
            String requirement = "";
            for (String req : requirementsArray) {
                requirement += " " + req + ",";
            }
            mThingsToCarryTV.setText(requirement.substring(0, requirement.length() - 1));
        }

        setMarker();
        //set the 7 dates
        Utilities.setDayViews(mDaysLayout);
        //set the todays date as selected date
        Utilities.setSelectedDay(parentActivity, 0, mDaysLayout);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dayOFWeek = c.get(Calendar.DAY_OF_WEEK);
        //Add time slots for today
        Utilities.addTimeSlots(parentActivity, dayOFWeek, activityDetailJson.getSchedule(), mTimeSlotLayout, c, 0, 24,activityDetailJson);
        //Add click listeners for other days
        Utilities.addDayClickListeners(parentActivity, mDaysLayout, activityDetailJson.getSchedule(), mTimeSlotLayout, 0, 24, activityDetailJson);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        mMap = googleMap;
        setMarker();
    }

    private void setMarker() {
        if (mMap != null && mActivityDetailJson != null && mActivityDetailJson.getGymLat() != null && mActivityDetailJson.getGymLong() != null) {
            Double lat = Double.parseDouble(mActivityDetailJson.getGymLat());
            Double longi = Double.parseDouble(mActivityDetailJson.getGymLong());
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longi), 12));
        }
    }

    /**
     * Set back arrow navigation.
     */
    protected void setUpNavigation() {
        ActionBar actionBar = parentActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setToolbar(String title) {
        if (toolbar != null) {
            parentActivity.setSupportActionBar(toolbar);
        }
        ActionBar actionBar = parentActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
