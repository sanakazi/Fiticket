package com.fitticket.viewmodel.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fitticket.BuildConfig;
import com.fitticket.R;
import com.fitticket.model.services.FetchFavGymsService;
import com.fitticket.model.services.FetchProfileDetailsService;
import com.fitticket.model.services.RegistrationIntentService;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.quantumgraph.sdk.QG;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final long SPLASH_DISPLAY_LENGTH = 2000;
    private static final String FIRSTRUN = "FIRSTRUN";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    boolean firstrun;
    PreferencesManager sPref;
    private QG qg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set fullscreen
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_splash);
        sPref = PreferencesManager.getInstance(this);
        TextView versionText = (TextView) findViewById(R.id.versionTextView);
        versionText.setText("Version: " + BuildConfig.VERSION_NAME);
        Log.i(TAG, "Splash Screen");
        //Get if it is the first launch of the app
        if (sPref.isFirstLaunch()) {
            MySingleton.getInstance(this).clearApplicationData();
            sPref.clearTimeStamps();
            //If this is first launch of new App and user has logged in in the previous version of the app
            // get user profile details here
            if (sPref.isLoggedIn()) {
                Intent intent = new Intent(this, FetchProfileDetailsService.class);
                startService(intent);
            }
        }
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            //GCM Registration
            if (!sPref.isSentTokenToServer() && isConnected) {
                if (checkPlayServices()) {
                    // Start IntentService to register this application with GCM.

                    Intent intent = new Intent(this, RegistrationIntentService.class);
                    startService(intent);
                }

            }
            showKeyHash();


            //move to next activity after 2 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sPref.getSelectedCityId().equals("0")) {
                        Utilities.promptForCitySelection(SplashActivity.this);
                    } else {
                        //if it is first launch, show training slides
                        Intent intent;
                        if (sPref.isFirstRun()) {
                            intent = new Intent(SplashActivity.this, TrainningSlidesActivity.class);
                            PreferencesManager.getInstance(SplashActivity.this).saveIsFirstRun(false);
                        } else {
                            intent = new Intent(SplashActivity.this, FetchFavGymsService.class);
                            startService(intent);
                            intent = new Intent(SplashActivity.this, MainActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                }
            }, SPLASH_DISPLAY_LENGTH);

        } else {
            showDialog("Fiticket requires an active internet connection.Please check your data connection and try again.", "Warning");
        }
        //QGraph init
        QG.initializeSdk(getApplication(), "2acc4f4c7c0198663845", "75899056408");


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

    //Method to generate the Keyhash programatically. This can be used for Facebook SDK integration
    private void showKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.fitticket",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        }
    }

    public void showDialog(String msg, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this);
        builder.setTitle(title).setMessage(msg).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        qg = QG.getInstance(getApplicationContext());
        qg.onStart();

    }
}
