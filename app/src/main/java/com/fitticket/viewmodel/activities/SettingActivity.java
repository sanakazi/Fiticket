
package com.fitticket.viewmodel.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.viewmodel.utils.Utilities;

public class SettingActivity extends AppCompatActivity {
    PreferencesManager sPref;
    private static final int MY_CALL_PHONE_REQUEST = 234;
    private LinearLayout llchangeCity, llFAQ, llTermsCondition, llFeedback, llLogout, llEditProfile, llContactUs, llLogin, mLlBuySub, llsubscription;
    Context context;
    int custId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //Utilities.setCustomActionBar(SettingActivity.this, "SETTINGS");
        //getActionBar().setDisplayHomeAsUpEnabled(true);
//        int upId = Resources.getSystem().getIdentifier("up", "id", "android");
//        if (upId > 0) {
//            ImageView up = (ImageView) findViewById(upId);
//            up.setPadding(10, 0, 0, 0);
//            up.setImageResource(R.drawable.ic_back);
//        }
        context = this;
        custId = PreferencesManager.getInstance(this).getUserId();
        llchangeCity = (LinearLayout) findViewById(R.id.changecity);
        llFAQ = (LinearLayout) findViewById(R.id.llFAQ);
        mLlBuySub = (LinearLayout) findViewById(R.id.llBuySub);
        llTermsCondition = (LinearLayout) findViewById(R.id.llTermsCondition);
        llFeedback = (LinearLayout) findViewById(R.id.llFeedback);
        llLogout = (LinearLayout) findViewById(R.id.llLogout);
        llLogin = (LinearLayout) findViewById(R.id.llLogin);
        llEditProfile = (LinearLayout) findViewById(R.id.lleditprofile);
        llContactUs = (LinearLayout) findViewById(R.id.llcontactus);
        llsubscription = (LinearLayout) findViewById(R.id.llsubscription);
        sPref = PreferencesManager.getInstance(this);
        if (custId != 0) {
            llLogin.setVisibility(View.GONE);
            llLogout.setVisibility(View.VISIBLE);
        } else {
            llLogin.setVisibility(View.VISIBLE);
            llLogout.setVisibility(View.GONE);
            llEditProfile.setVisibility(View.GONE);
        }

        llchangeCity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Utilities.promptForCitySelection(SettingActivity.this);
            }
        });
        llContactUs.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("Call Fiticket Customer service").setMessage("+917506959347").setCancelable(false)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })

                        .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (Utilities.checkPermission(true, SettingActivity.this, "android.permission.CALL_PHONE", MY_CALL_PHONE_REQUEST)) {
                                    dialog.cancel();
                                    launchCallIntent();
                                }
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        mLlBuySub.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingActivity.this, BuySubscriptionActivity.class);
                startActivity(i);
            }
        });

        llEditProfile.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (custId != 0) {
                    Intent i = new Intent(SettingActivity.this, EditUserActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(SettingActivity.this, "Please Login ", Toast.LENGTH_SHORT).show();
                }

            }
        });

        llLogout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("Logout Confirmation")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        PreferencesManager.getInstance(SettingActivity.this).clearUserPreferences();
                                        Intent i = new Intent(SettingActivity.this, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton(android.R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.cancel();
                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert).show();
            }

        });

        llLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch login activity
                Intent i = new Intent(SettingActivity.this,
                        LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
            }
        });

        llFAQ.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Uri uri = Uri.parse("http://fiticket.com/fit_faq");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        llTermsCondition.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Uri uri = Uri.parse("http://fiticket.com/terms");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        llFeedback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(SettingActivity.this, Feedback_Activity.class);
                startActivity(i);
            }
        });

        llsubscription.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String loadUrl = Apis.SHOW_ONLY_SUBDCRIPTION_URL + "" + sPref.getUserId();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(loadUrl));
                    startActivity(browserIntent);
                } else {
                    String loadUrl = Apis.SHOW_ONLY_SUBDCRIPTION_URL + "" + sPref.getUserId();
                    Uri uri = Uri.parse(loadUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

    }


    private void launchCallIntent() {
        try {
            String number = "tel:" + "+917506959347";
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
            startActivity(callIntent);
        } catch (SecurityException se) {
            se.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_CALL_PHONE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    launchCallIntent();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idmenu = item.getItemId();
        if (idmenu == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

    }
}
