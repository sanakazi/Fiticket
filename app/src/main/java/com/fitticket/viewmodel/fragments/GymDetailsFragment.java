package com.fitticket.viewmodel.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.fitticket.R;
import com.fitticket.model.pojos.GymDataJson;
import com.fitticket.model.singleton.MySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
public class GymDetailsFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = GymDetailsFragment.class.getSimpleName();
    private static final String GYM_ARRAY_INDEX = "GYM_ARRAY_INDEX";
    private AppCompatActivity parentActivity;
    ImageLoader imageLoader;

    //Views
    ProgressBar progressBar;
    View fragmentView;
    GoogleMap mMap;

    //Butterknife binding of views

    @Bind(R.id.about_desc)
    TextView mAboutTxtVw;
    @Bind(R.id.addressTxtView)
    TextView mAddressTxtVw;
    @Bind(R.id.mapLayout)
    FrameLayout mapLayout;

    //model
    GymDataJson mGymDetailJson;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ActivityDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GymDetailsFragment newInstance() {
        GymDetailsFragment fragment = new GymDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public GymDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView= inflater.inflate(R.layout.fragment_gym_details, container, false);
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
        parentActivity= (AppCompatActivity) getActivity();
//        Utilities.setActionBarIconVisibility(parentActivity,true);
        progressBar= new ProgressBar(parentActivity);
        progressBar.setVisibility(View.VISIBLE);
        imageLoader= MySingleton.getInstance(parentActivity).getImageLoader();
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGymDetailJson=MySingleton.getInstance(parentActivity).getSelectedGym();
        filldataInViews(mGymDetailJson);
    }

    //populate the views with data
    private void filldataInViews(GymDataJson gymDetailJson) {
        mAboutTxtVw.setText(gymDetailJson.getAboutUs());
        mAddressTxtVw.setText(gymDetailJson.getBranchAddress());
        Linkify.addLinks(mAddressTxtVw, Linkify.PHONE_NUMBERS);
        setMarker();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        mMap=googleMap;
        setMarker();
    }

    private void setMarker() {
        if(mMap!=null && mGymDetailJson!=null &&  !TextUtils.isEmpty(mGymDetailJson.getBranchLatitude())&&
                !TextUtils.isEmpty(mGymDetailJson.getBranchLongitude())){
            Double lat= Double.parseDouble(mGymDetailJson.getBranchLatitude());
            Double longi= Double.parseDouble(mGymDetailJson.getBranchLongitude());
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longi), 12));
        }
        else {
            mapLayout.setVisibility(View.GONE);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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



}
