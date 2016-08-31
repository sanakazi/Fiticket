
package com.fitticket.viewmodel.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.EditProfileResponse;
import com.fitticket.model.pojos.EditUserProfile;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.HttpConnectionUtil;
import com.fitticket.viewmodel.fragments.OTPDialogFragment;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.reflect.TypeToken;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

public class EditUserActivity extends AppCompatActivity implements OTPDialogFragment.OnFragmentInteractionListener{

    private static final String TAG = EditUserActivity.class.getSimpleName();
    private SharedPreferences sPrefs;
    int customerId, isSocialLogin;
    EditText userName, mobileNo, oldPassword, newPassword;
    ImageButton saveButton;
    ProgressDialog progress;
    PreferencesManager pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        progress = new ProgressDialog(EditUserActivity.this);
        pref=PreferencesManager.getInstance(this);
        customerId = pref.getUserId();
        Log.i("cusId",customerId+"");
        
        isSocialLogin = pref.isSocialLogin();
        
        Log.i(TAG, isSocialLogin+"");
        
//        Utilities.setCustomActionBar(EditUserActivity.this, "EDIT PROFILE");
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        int upId = Resources.getSystem().getIdentifier("up", "id", "android");
//        if (upId > 0) {
//            ImageView up = (ImageView) findViewById(upId);
//            up.setPadding(10, 0, 0, 0);
//            up.setImageResource(R.drawable.ic_back);
//        }

        userName = (EditText) findViewById(R.id.usernameTextView);
        mobileNo = (EditText) findViewById(R.id.mobileNoText);
        oldPassword = (EditText) findViewById(R.id.oldpasswordText);
        newPassword = (EditText) findViewById(R.id.newPasswordText);
        saveButton = (ImageButton) findViewById(R.id.saveButton);

        newPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int keyCode,
                                          KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(newPassword.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        if (isSocialLogin == 1)
        {
            oldPassword.setVisibility(View.GONE);
            newPassword.setVisibility(View.GONE);
        }

        userName.setText(pref.getProfileData().getName());
        mobileNo.setText(pref.getProfileData().getContactNo());


        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isSocialLogin == 1) {
                    EditUserProfile edit = new EditUserProfile();
                    edit.setCustomerName(pref.getProfileData().getName());
                    edit.setContactNo(mobileNo.getText().toString());
                    edit.setIsSocial(isSocialLogin);
                    edit.setOldPassword("");
                    edit.setPassword("");

                    if (mobileNo.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "ENTER MOBILE NUMBER", Toast.LENGTH_LONG).show();

                    } else if (mobileNo.getText().toString().length() < 10) {
                        Toast.makeText(getApplicationContext(), "ENTER VALID MOBILE NUMBER", Toast.LENGTH_LONG).show();
                    } else {
                        new UpdateUserProfile().execute(edit);
                    }
                } else if (oldPassword.getText().toString().isEmpty() && newPassword.getText().toString().isEmpty()) {
                    EditUserProfile edit = new EditUserProfile();
                    edit.setCustomerName(pref.getProfileData().getName());
                    edit.setContactNo(mobileNo.getText().toString());
                    edit.setOldPassword("");
                    edit.setPassword("");
                    edit.setIsSocial(1);

                    if (mobileNo.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "ENTER MOBILE NUMBER", Toast.LENGTH_LONG).show();

                    } else if (mobileNo.getText().toString().length() < 10) {
                        Toast.makeText(getApplicationContext(), "ENTER VALID MOBILE NUMBER", Toast.LENGTH_LONG).show();
                    } else {
                        new UpdateUserProfile().execute(edit);
                    }
                } else {
                    EditUserProfile edit = new EditUserProfile();
                    edit.setCustomerName(pref.getProfileData().getName());
                    edit.setContactNo(mobileNo.getText().toString());
                    edit.setIsSocial(isSocialLogin);
                    edit.setOldPassword(oldPassword.getText().toString());
                    edit.setPassword(newPassword.getText().toString());

                    if (mobileNo.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "ENTER MOBILE NUMBER", Toast.LENGTH_LONG).show();

                    } else if (mobileNo.getText().toString().length() < 10) {
                        Toast.makeText(getApplicationContext(), "ENTER VALID MOBILE NUMBER", Toast.LENGTH_LONG).show();
                    } else if (oldPassword.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "ENTER OLD PASSWORD", Toast.LENGTH_LONG).show();
                    } else if (newPassword.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "ENTER NEW PASSWORD", Toast.LENGTH_LONG).show();
                    } else if (oldPassword.getText().toString().equals(newPassword.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "ENTER DIFFERENT PASSWORD", Toast.LENGTH_LONG).show();
                    } else {
                        new UpdateUserProfile().execute(edit);
                    }
                }
            }
        });

    }

    private void showOTPDialog() {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        OTPDialogFragment newFragment =OTPDialogFragment.newInstance(customerId, pref.getProfileData().getContactNo());
        newFragment.show(ft, "dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        MenuItem profile = menu.findItem(R.id.blank);
        profile.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idmenu = item.getItemId();
        if (idmenu == android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void handleEditResult(EditProfileResponse result) {
        EditProfileResponse.ProfileData profile;

        switch (result.getId()){
            case 0:
                profile= result.getProfile();
                pref.saveProfileData(profile);
                finish();
                break;
            case 1:
                profile= result.getProfile();
                pref.saveProfileData(profile);
                Utilities.showOTPDialog(this, customerId, profile.getContactNo());
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
        Toast.makeText(this,result.getMessage(),Toast.LENGTH_LONG).show();



    }


    public void showDialog(String msg, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
        builder.setTitle(title).setMessage(msg).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onVerificationSuccessful(int customerId, String phoneNumber) {
        pref.saveContactNo(phoneNumber);
        finish();
    }

    @Override
    public void onOTPCancelled() {
        Toast.makeText(this,"Phone number not verified",Toast.LENGTH_SHORT).show();
    }

    class UpdateUserProfile extends AsyncTask<EditUserProfile, Object, EditProfileResponse> {

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(EditUserActivity.this);
            progress.setMessage("Please wait...");
            progress.setCancelable(true);
            progress.show();
        }

        @Override
        protected EditProfileResponse doInBackground(EditUserProfile... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            EditProfileResponse edit = null;
            int status = 0;
            Log.i("customer url ", Apis.UPDATE_CUSTOMER_DETAILS + "?customerId=" + customerId);
            try {

                connection = HttpConnectionUtil.getPutConnection(Apis.UPDATE_CUSTOMER_DETAILS + "?customerId=" + customerId);

                
                String requestData = HttpConnectionUtil.gson.toJson(params[0]);
                Log.i("data send", requestData);
                
                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.print(requestData);
                out.flush();
                status = connection.getResponseCode();
                if (status == HttpConnectionUtil.PUT_OK) {

                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    String responseData = sb.toString();
                    Log.i("EditUserProfile", "response (EditUserProfile) : " + responseData);

                    edit = HttpConnectionUtil.gson.fromJson(responseData, new TypeToken<EditProfileResponse>() {
                    }.getType());
                }
                else {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    String line = "";
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    Log.e("EditUserProfile", "response (EditUserProfile) : " + sb.toString());
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return edit;

        }

        @Override
        protected void onPostExecute(EditProfileResponse result) {
                        try {
                            if ((progress != null) && progress.isShowing()) {
                                progress.dismiss();
                            }
            
                        }
                        catch (final IllegalArgumentException e) {
                            // Handle or log or ignore
                        }
                        catch (final Exception e) {
                            // Handle or log or ignore
                        }
                        finally {
                            progress = null;
                        }

            if (result != null) {
                Log.i(TAG, result.toString());
                handleEditResult(result);
            }
        }

    }





}
