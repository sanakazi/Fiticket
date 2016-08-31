package com.fitticket.viewmodel.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.constants.Constants;
import com.fitticket.model.pojos.EditProfileResponse;
import com.fitticket.model.pojos.GetOperatingCitiesResponse;
import com.fitticket.model.pojos.GymDataJson;
import com.fitticket.model.pojos.ReferralDetailsResult;
import com.fitticket.model.services.FetchProfileDetailsService;
import com.fitticket.model.services.RegistrationIntentService;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.viewmodel.adapters.ActivitiesAdapter;
import com.fitticket.viewmodel.adapters.ActivitiesAdapterNew;
import com.fitticket.viewmodel.adapters.GymAdapter;
import com.fitticket.viewmodel.fragments.ActiveSubscriptionFragment;
import com.fitticket.viewmodel.fragments.ActivitiesFragmentNew;
import com.fitticket.viewmodel.fragments.CategoriesFragment;
import com.fitticket.viewmodel.fragments.FavoriteGymFragment;
import com.fitticket.viewmodel.fragments.FeedFragment;
import com.fitticket.viewmodel.fragments.NearByFragment;
import com.fitticket.viewmodel.fragments.ReferralDialogFragment;
import com.fitticket.viewmodel.fragments.UpcomingFragment;
import com.fitticket.viewmodel.fragments.WelcomeDialogFragment;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.Date;


public class MainActivity extends AppCompatActivity implements FeedFragment.FragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, CategoriesFragment.FragmentInteractionListener
        , ActivitiesAdapter.ActivitySelectedListener, ActivitiesAdapterNew.ActivitySelectedListener,
        GymAdapter.GymSelectedListener, Utilities.BookingSuccessfulListener, com.google.android.gms.location.LocationListener {
    public static final String LOCATION_INTENT = "LOCATION_RECEIVED";
    public static final String SHOW_UPCOMING = "SHOW_UPCOMING";
    private static final long ONE_DAY = 24 * 60 * 60 * 1000; //Milliseconds in a day
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String ACT_FRAGMENT = "ACT_FRAGMENT";
    private static final String FEED_FRAGMENT = "FEED_FRAGMENT";
    private static final String NEARBY_FRAGMENT = "NEARBY_FRAGMENT";
    private static final String BUY_FRAGMENT = "BUY_FRAGMENT";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ACT_DETAIL_FRAGMENT = "ACT_DETAIL_FRAGMENT";
    private static final String CAT_FRAGMENT = "CAT_FRAGMENT";
    private static final String UPCOMING_FRAG = "UPCOMING_FRAG";
    private static final String ATTENDED_FRAG = "ATTENDED_FRAG";
    private static final int PROFILE = 0;
    private static final int UPCOMING = 1;
    private static final int HISTORY = 2;
    private static final int REFERRAL = 3;
    private static final int FAVORITE = 4;
    private static final int HELP = 5;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 123;
    private static final String FAV_FRAG = "FAV_FRAG";
    private static final String ACTIVE_FRAG = "ACTIVE_FRAG";
    private static final int REQUEST_CHECK_SETTINGS = 3456;
    Toolbar toolbar;
    String[] navigationItems;
    //    ListView mNavigationListview;
    CharSequence mTitle;
    CategoriesFragment categoriesFragment;
    TextView myLocationButton;
    PreferencesManager sPref;
    View navHeaderLayout;
    private Button feedsButton, activitiesButton, nearByButton, ifeelLikeButton;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout footerView;
    private String[] navigationIconsArray;
    private String[] navigationStringsArray;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    //Geocoder Stuff
    private String mAddressOutput = "";
    private NavigationView navigationView;
    private EditProfileResponse.ProfileData mProfileData;
    private ReferralDetailsResult.ReferralDetails mReferralDetails;
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(FetchProfileDetailsService.PROFILE_DATA_DOWNLOADED)) {
                fillProfileDetails();
            }
        }
    };
    private MenuItem searchMenuItem;
    private android.support.v7.widget.SearchView searchView;
    private Fragment activeFragment;
    private LocationRequest mLocationRequest;

    public static void showToast(String title, Context context) {
        Toast.makeText(context, title, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean showUpcoming = getIntent().getBooleanExtra(SHOW_UPCOMING, false);
        sPref = PreferencesManager.getInstance(this);
        buildGoogleApiClient();
        checkLocationSettings();
        Log.i(TAG, "MainActivity");
//        Intent intent = getIntent();
//        String action = intent.getAction();
//        String data = intent.getDataString();

//        if (){
//            if (Intent.ACTION_VIEW.equals(action) && data != null) {
//                String productId = data.substring(data.lastIndexOf("/") + 1);
//                Log.e("productId", "productId" + productId);
////            Uri contentUri = PetstoreContentProvider.CONTENT_URI.buildUpon()
////                    .appendPath(productId).build();
//
//            }
//
//        }


//        navigationIconsArray= new String[]{getResources().getString(R.string.ic_profile), getResources().getString(R.string.ic_upcoming), getResources().getString(R.string.ic_history), getResources().getString(R.string.ic_referral), getResources().getString(R.string.ic_favorite), getResources().getString(R.string.ic_help)};
//        navigationStringsArray= getResources().getStringArray(R.array.navigation_items);
        findFooterViews();
        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mNavigationListview=(ListView)findViewById(R.id.left_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolBarTitle(getString(R.string.app_name));

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navHeaderLayout = navigationView.getHeaderView(0);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                toolbar.setTitle(getString(R.string.app_name));
                footerView.setVisibility(View.VISIBLE);
                //Closing drawer on item click
                mDrawerLayout.closeDrawers();
                Fragment fragment;
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        footerView.setVisibility(View.VISIBLE);
                        toolbar.setTitle(getString(R.string.app_name));
                        return true;
                    case R.id.upcoming:
                        fragment = UpcomingFragment.newInstance(UpcomingFragment.UPCOMING);
                        replaceFragment(fragment, UPCOMING_FRAG, true);
                        toolbar.setTitle(getString(R.string.title_upcoming_events));
                        footerView.setVisibility(View.GONE);
                        return true;

                    case R.id.history:
                        fragment = UpcomingFragment.newInstance(UpcomingFragment.ATTENDED);
                        replaceFragment(fragment, ATTENDED_FRAG, true);
                        toolbar.setTitle(getString(R.string.title_history));
                        footerView.setVisibility(View.GONE);
                        return true;
                    case R.id.favorites:
                        fragment = FavoriteGymFragment.newInstance();
                        replaceFragment(fragment, FAV_FRAG, true);
                        toolbar.setTitle(getString(R.string.title_favorites));
                        footerView.setVisibility(View.GONE);
                        return true;
                    case R.id.referral:
                        if (!mReferralDetails.getCode().equals(""))
                            showReferralPopUp();
                        return true;
                    case R.id.buySubscription:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            String loadUrl = Apis.CCAVENUE_URL + "" + sPref.getUserId();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(loadUrl));
                            startActivity(browserIntent);
                        } else {
                            Intent i = new Intent(MainActivity.this, CCAvenueWebview.class);
                            startActivity(i);
                        }
                        return true;

                    case R.id.activeSubscription:
                        fragment = ActiveSubscriptionFragment.newInstance();
                        replaceFragment(fragment, ACTIVE_FRAG, true);
                        toolbar.setTitle(getString(R.string.title_active_subscription));
                        footerView.setVisibility(View.GONE);
                        return true;
                    case R.id.rateUs:
                        launchMarket();
                        return true;
                    case R.id.help:
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
                        return true;
                    default:
                        //Should never come here
                        return true;

                }
            }
        });

//        navigationItems=getResources().getStringArray(R.array.navigation_items);
//        NavigationDrawerAdapter navigationAdapter=new NavigationDrawerAdapter(this,navigationIconsArray,navigationStringsArray);
//        mNavigationListview.setAdapter(navigationAdapter);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };

        if (!sPref.isLoggedIn()) {
            navHeaderLayout.setVisibility(View.GONE);
//            navigationView.setVisibility(View.GONE);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchLoginActivity();
                }
            });
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        }
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the drawer toggle as the DrawerListener

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        addDefaultFragment();


        //GCM Registration
        if (!sPref.isSentTokenIdToServer()) {
            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }
        if (showUpcoming) {
            showUpcomingFragment();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.search:
                return true;
            case android.R.id.home:
                if (!sPref.isLoggedIn())
                    launchLoginActivity();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkLocationSettings() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates settingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    /*@Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void showUpcomingFragment() {
        UpcomingFragment fragment = UpcomingFragment.newInstance(UpcomingFragment.UPCOMING);
        replaceFragment(fragment, UPCOMING_FRAG, true);
        toolbar.setTitle("Upcoming Activities");
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.couldnt_launch_market, Toast.LENGTH_LONG).show();
        }
    }

    private void showReferralPopUp() {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        ReferralDialogFragment newFragment = ReferralDialogFragment.
                newInstance(mReferralDetails.getCode(),
                        mReferralDetails.getMessage(),
                        mReferralDetails.getCodeURL());
        newFragment.show(ft, "dialog");
    }

    private long timeDifference(long time1, long time2) {
        return time2 - time1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(FetchProfileDetailsService.PROFILE_DATA_DOWNLOADED));
        toggleDrawerLockState();
        fillProfileDetails();
        //Check if user is logged in, if yes
        //Calculate the time difference between last Referral pop-up and now. If it is greater than 2 days, display pop-up
        if (sPref.isLoggedIn() && sPref.isFirstLogin()) {
            showWelcomePopUp();
            sPref.saveIsFirstLogin(false);
        } else if (sPref.getUserId() != 0 && timeDifference(sPref.getReferralTimeStamp(), (new Date()).getTime()) > (2 * ONE_DAY)) {
            if (!mReferralDetails.getCode().equals("")) {
                showReferralPopUp();
                sPref.saveReferralTimeStamp(new Date().getTime());
            }
        }
    }

    private void showWelcomePopUp() {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        WelcomeDialogFragment newFragment = WelcomeDialogFragment.
                newInstance();
        newFragment.show(ft, "dialog");
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
//        if(mGoogleApiClient!=null)
//        stopLocationUpdates();
    }

    private void toggleDrawerLockState() {
        if (sPref.getUserId() == 0) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    private void addDefaultFragment() {

        categoriesFragment = (CategoriesFragment) getSupportFragmentManager().findFragmentByTag(CAT_FRAGMENT);

        if (categoriesFragment == null) {
            categoriesFragment = new CategoriesFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, categoriesFragment, CAT_FRAGMENT).commit();
        } else {
            replaceFragment(categoriesFragment, CAT_FRAGMENT, false);
        }
        changePressedState(ifeelLikeButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
//        QG qg = QG.getInstance(getApplicationContext());
//        qg.onStart("2acc4f4c7c0198663845", getResources().getString(R.string.gcm_sender_id));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
//        QG qg = QG.getInstance(getApplicationContext());
//        qg.onStop();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void findFooterViews() {
        footerView = (LinearLayout) findViewById(R.id.footerview);
        feedsButton = (Button) findViewById(R.id.buttonFeed);
        activitiesButton = (Button) findViewById(R.id.buttonActivities);
        nearByButton = (Button) findViewById(R.id.buttonNearby);
        ifeelLikeButton = (Button) findViewById(R.id.buttonIFeelLike);

        feedsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                FeedFragment feedFragment = (FeedFragment) getSupportFragmentManager().findFragmentByTag(FEED_FRAGMENT);
                if (feedFragment == null) {
                    feedFragment = new FeedFragment();
                }
                replaceFragment(feedFragment, FEED_FRAGMENT, false);
                changePressedState(feedsButton);
                activeFragment = feedFragment;

            }
        });

        activitiesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                ActivitiesFragment activitiesFragment = (ActivitiesFragment)getSupportFragmentManager().findFragmentByTag(ACT_FRAGMENT);
                ActivitiesFragmentNew activitiesFragment = (ActivitiesFragmentNew) getSupportFragmentManager().findFragmentByTag(ACT_FRAGMENT);
                if (activitiesFragment == null) {
                    //Until all gym data loads, show list for Gym, Yoga, Zumba and crossfit
                    String cats = "10,11,12,13";
//                    for (CategoryJson cat : MySingleton.getInstance(MainActivity.this).getCategoryList()) {
//                        cats += Utilities.getCommaSeperatedCatIds(cat.getCategoryList());
//                        cats+=",";
//                    }
//                    if (cats.charAt(cats.length()-1) == ',') {
//                        cats = cats.substring(0,cats.length()-1);
//                    }
                    Location location = new Location("");
                    if (mLastLocation == null) {
                        GetOperatingCitiesResponse.CitiesJson selectedCity = sPref.getSelectedCityJson();
                        location.setLatitude(selectedCity.getCityLat());
                        location.setLongitude(selectedCity.getCityLong());
                    } else {
                        location = mLastLocation;
                    }

                    String url = Apis.GET_ACTIVITIES_BY_LOCATION_URL + "?latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude() +
                            "&category=" + cats + "&distance=" + Constants.NEARBY_DISTANCE; //TODO- category id is hardcoded, should be removed
                    //TODO TO BE REMOVED
//                    String url= Apis.GET_ACTIVITIES_BY_LOCATION_URL+"?latitude="+19.13+"&longitude="+72.83+"&category=12&radius="+ Constants.NEARBY_DISTANCE; //Mumbai lat long For testing
                    activitiesFragment = ActivitiesFragmentNew.newInstance(ActivitiesFragmentNew.FROM_MAIN_ACTIVITY, url);
                }
                replaceFragment(activitiesFragment, ACT_FRAGMENT, false);
                changePressedState(activitiesButton);
                activeFragment = activitiesFragment;

            }
        });
        ifeelLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CategoriesFragment categoriesFragment = (CategoriesFragment) getSupportFragmentManager().findFragmentByTag(CAT_FRAGMENT);
                if (categoriesFragment == null) {
                    categoriesFragment = new CategoriesFragment();
                }
                replaceFragment(categoriesFragment, CAT_FRAGMENT, false);
                changePressedState(ifeelLikeButton);
                activeFragment = categoriesFragment;
            }
        });
        nearByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NearByFragment nearByFragment = (NearByFragment) getSupportFragmentManager().findFragmentByTag(NEARBY_FRAGMENT);
                if (nearByFragment == null) {
                    nearByFragment = NearByFragment.newInstance();
                }
                replaceFragment(nearByFragment, NEARBY_FRAGMENT, false);
                changePressedState(nearByButton);
                activeFragment = nearByFragment;
            }
        });


    }

    //Method changes the state of the footer buttons

    private void replaceFragment(Fragment fragment, String tag, boolean addtoBackStack) {
        if (addtoBackStack) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, tag).addToBackStack(null).commit();
//            toolbarNAvigationListener();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, tag).commit();
        }
    }

    public void changePressedState(Button v) {
        Drawable[] drawables;
        drawables = feedsButton.getCompoundDrawables();
        ContextCompat.getColor(MainActivity.this, R.color.White);
        drawables[1].setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.White),
                PorterDuff.Mode.SRC_ATOP);
        feedsButton.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.White));

        drawables = activitiesButton.getCompoundDrawables();
        drawables[1].setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.White),
                PorterDuff.Mode.SRC_ATOP);
        activitiesButton.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.White));

        drawables = nearByButton.getCompoundDrawables();
        drawables[1].setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.White),
                PorterDuff.Mode.SRC_ATOP);
        nearByButton.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.White));

        drawables = ifeelLikeButton.getCompoundDrawables();
        drawables[1].setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.White),
                PorterDuff.Mode.SRC_ATOP);
        ifeelLikeButton.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.White));

        drawables = v.getCompoundDrawables();
        drawables[1].setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.calendar),
                PorterDuff.Mode.SRC_ATOP);
        v.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.calendar));
    }


    /*final private android.support.v7.widget.SearchView.OnQueryTextListener queryListener=new android.support.v7.widget.SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (activeFragment instanceof ActivitiesFragment){
                 ((ActivitiesFragment) activeFragment).refreshList(newText);
            }else if (activeFragment instanceof NearByFragment){
                ((NearByFragment) activeFragment).refreshList(newText);
            }

            return false;
        }
    };*/

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
//            getActionBar().setTitle(title);
    }

    public void setToolBarTitle(String toolBarTitle) {
        if (toolbar != null) {
//        toolbar.setTitle(toolBarTitle);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(toolBarTitle);
            Log.i(TAG, "Toolbar was set");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onListScrolled(boolean scrolledUp) {
        if (scrolledUp) {
//            mQuickReturnView.setVisibility(View.VISIBLE);
            footerView.animate()
                    .translationY(0)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });


        } else {
            footerView.animate()
                    .translationY(footerView.getHeight())
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
//                            mQuickReturnView.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        saveLocationAndBroadcast();
        if (Utilities.checkPermission(true, this, Manifest.permission.ACCESS_COARSE_LOCATION, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION)) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private void saveLocationAndBroadcast() {

        if (mLastLocation != null) {
            MySingleton.getInstance(this).setMyLocation(mLastLocation);
            Intent intent = new Intent(LOCATION_INTENT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            if (!(MySingleton.getInstance(this).getmAllGymsList().isEmpty())) {
                Utilities.sortGymList(MySingleton.getInstance(this).getmAllGymsList(), mLastLocation);
            }
            if (!(MySingleton.getInstance(this).getmAllAcivitiesList().isEmpty())) {
                Utilities.sortActivitiesList(MySingleton.getInstance(this).getmAllAcivitiesList(), mLastLocation);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onCategoryClicked(String catId) {
        Location location;
        if (Utilities.checkPermission(true, this, Manifest.permission.ACCESS_COARSE_LOCATION, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION) && mLastLocation != null) {
            location = mLastLocation;
        } else {
            GetOperatingCitiesResponse.CitiesJson selectedCity = sPref.getSelectedCityJson();
            location = new Location("");
            location.setLatitude(selectedCity.getCityLat());
            location.setLongitude(selectedCity.getCityLong());
        }
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(MapActivity.LOCATION, location);
        intent.putExtra(MapActivity.CAT_ID, catId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        toolbar.setTitle(getString(R.string.app_name));
        super.onBackPressed();
        footerView.setVisibility(View.VISIBLE);
    }

    private void launchActivitiesDetailActivity(String activityId) {
        Intent i = new Intent(this, ActivityDetailsActivity.class);
        i.putExtra(ActivityDetailsActivity.ACTIVITY_ID, activityId);
        startActivity(i);
        overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
    }

    @Override
    public void onActivitySelected(String activityId) {
        launchActivitiesDetailActivity(activityId);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void launchLoginActivity() {
        //launch login activity
        Intent i = new Intent(this,
                LoginActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
    }

    public void fillProfileDetails() {

        TextView userName, email, currentPlan, expiringDate, referralCodeTextView, profileImage;
        userName = (TextView) navHeaderLayout.findViewById(R.id.usernameTextView);
        email = (TextView) navHeaderLayout.findViewById(R.id.emailTextView);
        currentPlan = (TextView) navHeaderLayout.findViewById(R.id.currentPlanValueTextView);
        expiringDate = (TextView) navHeaderLayout.findViewById(R.id.expiringValueTextView);
        profileImage = (TextView) navHeaderLayout.findViewById(R.id.profileImage);
        if (sPref.getUserId() != 0) {
            mProfileData = sPref.getProfileData();
            mReferralDetails = sPref.getReferralDetails();
            if (!TextUtils.isEmpty(mProfileData.getName()))
                profileImage.setText("" + mProfileData.getName().substring(0, 1));
            userName.setText(mProfileData.getName());
            email.setText(mProfileData.getEmailId());
            String currentPlanText = TextUtils.isEmpty(mProfileData.getPackageName()) ? "No Plan" : mProfileData.getPackageName();
            currentPlan.setText(currentPlanText);
            String expiresOn = TextUtils.isEmpty(mProfileData.getPackageEndDate()) ? "-" : mProfileData.getPackageEndDate();
            expiringDate.setText(expiresOn);
        } else {
            userName.setText("");
            email.setText("");
            currentPlan.setText("");
            expiringDate.setText("");
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onGymSelected(GymDataJson gymDataJson) {
        MySingleton.getInstance(this).setSelectedGym(gymDataJson);
        Intent intent = new Intent(this, GymDetailsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBookingSucessful() {
        showUpcomingFragment();

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        saveLocationAndBroadcast();
    }
}
