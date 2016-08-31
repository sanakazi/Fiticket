package com.fitticket.viewmodel.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.constants.Constants;
import com.fitticket.model.pojos.ActivityDetailJson;
import com.fitticket.model.pojos.GetActivityByLocationResponse;
import com.fitticket.model.services.FetchAddressIntentService;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.activities.MainActivity;
import com.fitticket.viewmodel.adapters.MapItemsAdapter;
import com.fitticket.viewmodel.custom.ProgressBarCircular;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.AddressComponent;
import com.seatgeek.placesautocomplete.model.AddressComponentType;
import com.seatgeek.placesautocomplete.model.Place;
import com.seatgeek.placesautocomplete.model.PlaceDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ActivityMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String CAT_ID = "CAT_ID";
    private static final String TAG = ActivityMapFragment.class.getSimpleName();
    Marker previousSelectedMarker;
    LinearLayout gymDetailsLayout;
    String latitude, longitude, catId;
    ProgressBarCircular progressBar;
    NetworkImageView gymLogoImgView;
    TextView gymNameTxtView, actNameTxtView;
//            filterTextView;
    AppCompatActivity parentActivity;
    Map<LatLng, Integer> markerMap;
    MapItemsAdapter mapItemsadapter;
    MyLocationReceiver mReceiver;
    com.fitticket.viewmodel.fragments.ActivityMapFragment.OnFragmentInteractionListener mListener;
    float zoomLevel = 10;
    private ArrayList<ActivityDetailJson> mActivitiesList;
    private GoogleMap mMap;
    private PlacesAutocompleteTextView placesAutocompleteTextView;
    private String mAddressOutput;
    private TextView myLocationButton;
    private AddressResultReceiver mResultReceiver;
    private Location myLocation;
//    private LinearLayout listHeader;
    private LinearLayout daysLayout;
    private LinearLayout timeSlotsLayout;
    private String myLong, myLat;
    private LinearLayout gymClickLayout;
    private int selected_Activity_position;
    private int radius = 25;

    public ActivityMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param latitude  Parameter 1.
     * @param longitude Parameter 2.
     * @param cateId    Parameter 3
     * @return A new instance of fragment ActivityHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityMapFragment newInstance(String latitude, String longitude, String cateId) {
        ActivityMapFragment fragment = new ActivityMapFragment();
        Bundle args = new Bundle();
        args.putString(LATITUDE, latitude);
        args.putString(LONGITUDE, longitude);
        args.putString(CAT_ID, cateId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, new IntentFilter(MainActivity.LOCATION_INTENT));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity_map, null, false);
        placesAutocompleteTextView = (PlacesAutocompleteTextView) view.findViewById(R.id.places_autocomplete);
        //TODO
        placesAutocompleteTextView.setCompletionEnabled(true);
        gymDetailsLayout = (LinearLayout) view.findViewById(R.id.gymDetailsLayout);
        gymLogoImgView = (NetworkImageView) view.findViewById(R.id.gymLogoImgView);
        gymNameTxtView = (TextView) view.findViewById(R.id.gymNameTxtView);
        actNameTxtView = (TextView) view.findViewById(R.id.activityNameTxtView);
//        listHeader = (LinearLayout) view.findViewById(R.id.listHeader);
//        filterTextView = (TextView) view.findViewById(R.id.filterView);
        timeSlotsLayout = (LinearLayout) view.findViewById(R.id.timeSlotLayout);
        daysLayout = (LinearLayout) view.findViewById(R.id.daysLayout);
        gymClickLayout = (LinearLayout) view.findViewById(R.id.gymClickLayout);
//        listHeader.setVisibility(View.GONE);
        gymDetailsLayout.setVisibility(View.GONE);
        gymDetailsLayout.setTranslationY(gymDetailsLayout.getHeight());
        myLocationButton = (TextView) view.findViewById(R.id.my_location_button);

        //When change-location button is pressed, hide textview and show autocomplete view
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latitude = myLat;
                longitude = myLong;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),
                        myLocation.getLongitude()), zoomLevel));
                triggerVolleyRequestForActivitiesList();
            }
        });

        gymClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onActivityClicked(mActivitiesList.get(selected_Activity_position).getId());
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            latitude = "19.13";
            myLat = latitude = getArguments().getString(LATITUDE);
//            longitude = "72.83";
            myLong = longitude = getArguments().getString(LONGITUDE);
            catId = getArguments().getString(CAT_ID);
        }

        mResultReceiver = new AddressResultReceiver(new Handler());
        markerMap = new HashMap<LatLng, Integer>();
        triggerVolleyRequestForActivitiesList();
        mReceiver = new MyLocationReceiver();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        parentActivity = (AppCompatActivity) getActivity();
        Typeface font = Typeface.createFromAsset(parentActivity.getAssets(), "fontawesome-webfont.ttf");
//        filterTextView.setTypeface(font);
        myLocationButton.setTypeface(font);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (myLocation != null) {
            setAutocompleteListeners();
//            startIntentService(myLocation);
        }
//        mapFragment.getMapAsync(this);

    }

    private void triggerVolleyRequestForActivitiesList() {
        String url = Apis.GET_ACTIVITIES_BY_LOCATION_URL + "?latitude=" + latitude + "&longitude=" + longitude +
                "&category=" + catId + "&distance=" + radius;
        Log.d(TAG, "Get activities by location URL: " + url);
        WebServices.triggerVolleyGetRequest(parentActivity, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Activities by loc Response: " + response);
                Gson gson = new Gson();
                GetActivityByLocationResponse jsonResponse = gson.fromJson(response, GetActivityByLocationResponse.class);
                if (jsonResponse.getStatusCode().equalsIgnoreCase("0")) {
                    mActivitiesList = jsonResponse.getData().getActivities();
                    setMarkers(mActivitiesList);
                    gymDetailsLayout.setVisibility(View.GONE);
//                    listHeader.setVisibility(View.VISIBLE);
//                    listHeader.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            MySingleton.getInstance(parentActivity).setmMapAcivitiesList(mActivitiesList);
//                            mListener.onResultsClicked();
//                        }
//                    });
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

//    private void showMapItemsDialog() {
//        mapItemsadapter = new MapItemsAdapter(parentActivity, mActivitiesList);
//
//        DialogPlus dialog = DialogPlus.newDialog(parentActivity)
//                .setAdapter(mapItemsadapter)
//                .setOnItemClickListener(new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
//                    }
//                })b
//                .setExpanded(false)// This will enable the expand feature, (similar to android L share dialog)
//                .setHeader(R.layout.activity_list_header)
//                .create();
//        dialog.show();
//        TextView filterView = (TextView) dialog.getHeaderView().findViewById(R.id.filterView);
//        Typeface font = Typeface.createFromAsset(parentActivity.getAssets(), "fontawesome-webfont.ttf");
//        filterView.setTypeface(font);
//        filterView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.showToast("Coming Soon", parentActivity);
//            }
//        });
//    }

    private void setMarkers(ArrayList<ActivityDetailJson> mActivitiesList) {
        if (mMap != null) {
            for (int i = 0; i < mActivitiesList.size(); i++) {

                ActivityDetailJson activity = mActivitiesList.get(i);
                Double lat = Double.parseDouble(activity.getGymLat());
                Double lon = Double.parseDouble(activity.getGymLong());
                LatLng latlng = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(latlng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red)));
                markerMap.put(latlng, i);
            }


        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnFragmentInteractionListener) activity;
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setTrafficEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude),
                Double.parseDouble(longitude)), zoomLevel));

        if (mActivitiesList != null && mActivitiesList.size() > 0) {
            setMarkers(mActivitiesList);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {


            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_yellow));
                if (previousSelectedMarker != null)
                    previousSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red));
                selected_Activity_position = markerMap.get(marker.getPosition());
                setSelectedGymDetails(selected_Activity_position);
                previousSelectedMarker = marker;
                return false;
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                latitude = "" + latLng.latitude;
                longitude = "" + latLng.longitude;
                mMap.clear();
                radius = 25;
                zoomLevel = 10;
                triggerVolleyRequestForActivitiesList();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                Location location = new Location("");
                location.setLatitude(latLng.latitude);//your coords of course
                location.setLongitude(latLng.longitude);
                startIntentService(location);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                if (listHeader.getVisibility() != View.VISIBLE) {
//                    listHeader.setVisibility(View.VISIBLE);
                gymDetailsLayout.setVisibility(View.GONE);
//                }
            }
        });
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (cameraPosition.zoom < zoomLevel) {
                    zoomLevel = cameraPosition.zoom;
                    if (radius < 25) {
                        radius += 10;
                        triggerVolleyRequestForActivitiesList();
                    }
                }
            }
        });
    }

    private void setSelectedGymDetails(int position) {
//        listHeader.setVisibility(View.GONE);
        gymDetailsLayout.setVisibility(View.VISIBLE);
        if (mActivitiesList.get(position) != null) {
            gymNameTxtView.setText(mActivitiesList.get(position).getGymName());
            actNameTxtView.setText(mActivitiesList.get(position).getActivityName());
            ImageLoader imageLoader = MySingleton.getInstance(parentActivity).getImageLoader();
            gymLogoImgView.setImageUrl(mActivitiesList.get(position).getGymLogo(), imageLoader);
            gymLogoImgView.setDefaultImageResId(R.drawable.ic_launcher);
            Utilities.setDayViews(daysLayout);
            Utilities.setSelectedDay(parentActivity, 0, daysLayout);
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            int dayOFWeek = c.get(Calendar.DAY_OF_WEEK);
            if (mActivitiesList.get(position).getSchedule() != null) {
                Utilities.addTimeSlots(parentActivity, dayOFWeek, mActivitiesList.get(position).getSchedule(), timeSlotsLayout, c, 0, 24, mActivitiesList.get(position));
                Utilities.addDayClickListeners(parentActivity, daysLayout, mActivitiesList.get(position).getSchedule(), timeSlotsLayout, 0, 24, mActivitiesList.get(position));
            }
        }
    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }

    private void setAutocompleteListeners() {
        placesAutocompleteTextView.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                placesAutocompleteTextView.getDetailsFor(place, new DetailsCallback() {
                    @Override
                    public void onSuccess(final PlaceDetails details) {
                        Log.d(TAG, "place details: " + details.toString());
                        String addressString = "";
                        for (AddressComponent component : details.address_components) {
                            for (AddressComponentType type : component.types) {
                                switch (type) {
                                    case STREET_NUMBER:
                                        break;
                                    case ROUTE:
                                        break;
                                    case NEIGHBORHOOD:
                                        break;
                                    case SUBLOCALITY_LEVEL_1:
//                                        addressString = addressString + " " + component.short_name;
                                        break;
                                    case SUBLOCALITY:
                                        addressString = addressString + " " + component.long_name;
                                        break;
                                    case LOCALITY:
                                        addressString = addressString + " " + component.short_name;
                                        break;
                                    case ADMINISTRATIVE_AREA_LEVEL_1:
                                        break;
                                    case ADMINISTRATIVE_AREA_LEVEL_2:
                                        break;
                                    case COUNTRY:
                                        break;
                                    case POSTAL_CODE:
                                        break;
                                    case POLITICAL:
                                        break;
                                }
                            }
                        }
                        placesAutocompleteTextView.setCompletionEnabled(false);
                        placesAutocompleteTextView.setText(addressString);
                        placesAutocompleteTextView.setCursorVisible(false);
                        //Todo- location to be geocoded
                        Geocoder gc = new Geocoder(parentActivity);

                        if (gc.isPresent()) {
                            List<Address> list = null;
                            try {
                                list = gc.getFromLocationName(addressString, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Address address = list.get(0);
                            latitude = "" + address.getLatitude();
                            longitude = "" + address.getLongitude();
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(address.getLatitude(), address.getLongitude())));
                            triggerVolleyRequestForActivitiesList();
                        }
                    }

                    @Override
                    public void onFailure(final Throwable failure) {
                        Log.d("test", "failure " + failure);
                        placesAutocompleteTextView.setText(place.description);
                    }
                });
            }
        });
        placesAutocompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placesAutocompleteTextView.setCompletionEnabled(true);
                placesAutocompleteTextView.setText("");
                placesAutocompleteTextView.setCursorVisible(true);
            }
        });
    }

    protected void startIntentService(Location location) {
        Intent intent = new Intent(parentActivity, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        parentActivity.startService(intent);
    }

    public interface OnFragmentInteractionListener {
        public void onActivityClicked(String activityId);

        public void onResultsClicked();
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            placesAutocompleteTextView.setText(mAddressOutput);
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
//                showToast(getString(R.string.address_found));
            }

        }
    }

    public class MyLocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            myLocation = MySingleton.getInstance(getContext()).getMyLocation();
//            startIntentService(myLocation);
            setAutocompleteListeners();
        }
    }


}
