package com.fitticket.viewmodel.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.EditProfileResponse;
import com.fitticket.model.pojos.PostResponseJson;
import com.fitticket.model.pojos.SignUpRequestJson;
import com.fitticket.model.services.FetchFavGymsService;
import com.fitticket.model.services.FetchProfileDetailsService;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.HttpConnectionUtil;
import com.fitticket.viewmodel.Validator.Field;
import com.fitticket.viewmodel.Validator.Form;
import com.fitticket.viewmodel.Validator.validations.IsEmail;
import com.fitticket.viewmodel.Validator.validations.IsPositiveInteger;
import com.fitticket.viewmodel.Validator.validations.NotEmpty;
import com.fitticket.viewmodel.fragments.OTPDialogFragment;
import com.fitticket.viewmodel.utils.ConnectionDetector;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

public class SignupActivity extends AppCompatActivity implements OnClickListener,
        OTPDialogFragment.OnFragmentInteractionListener {

    public static final int WEBVIEW_REQUEST_CODE = 10;
    //private static final String APP_ID = "425646524260622";
    private static final String APP_ID = "594426374026601";
    SharedPreferences sPref;
    LinearLayout passwordLayout, confPasswdLayout;
    private Form mForm;
    private String mCustomerName, mEmail, mPhoneNumber;
    private EditText etEmail, etPasssword, etName, etConfirmPass, etPhoneNumber;
    private CheckBox cbTermsService;
    private TextView tvTermsService;
    private Boolean isInternetPresent;
    private ProgressDialog progressDialog;
    private ImageButton ibCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                /* Setting activity layout file */
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        /* Enabling strict mode */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        EditProfileResponse.ProfileData profileData = PreferencesManager.getInstance(this).getProfileData();
        mCustomerName = profileData.getName();
        mEmail = profileData.getEmailId();
        mPhoneNumber = profileData.getContactNo();


        setContentView(R.layout.activity_register);

        etEmail = (EditText) findViewById(R.id.edtemail);
        etPasssword = (EditText) findViewById(R.id.edtpwd);
        etName = (EditText) findViewById(R.id.edtName);
        etConfirmPass = (EditText) findViewById(R.id.edtCfmPwd);
        etPhoneNumber = (EditText) findViewById(R.id.edtphone);
        passwordLayout = (LinearLayout) findViewById(R.id.passwdLayout);
        confPasswdLayout = (LinearLayout) findViewById(R.id.confPasswdLayout);


        tvTermsService = (TextView) findViewById(R.id.tvTermsService);
        cbTermsService = (CheckBox) findViewById(R.id.cbTermsService);
        ibCancel = (ImageButton) findViewById(R.id.ibCancel);

        progressDialog = new ProgressDialog(this);
        findViewById(R.id.btngetstart).setOnClickListener(this);
        ibCancel.setOnClickListener(this);


        isInternetPresent = ConnectionDetector.isConnectingToInternet(this);

        tvTermsService.setText(Html.fromHtml("<a href=\"http://fiticket.com/terms\">Terms and conditions</a> "));
        tvTermsService.setMovementMethod(LinkMovementMethod.getInstance());
        initValidationForm();

        //fillDetails();

    }

    private void fillDetails() {
        etName.setText(mCustomerName);
        etEmail.setText(mEmail);
        etPhoneNumber.setText(mPhoneNumber);
        if (PreferencesManager.getInstance(this).isSocialLogin() == 1) {
            etEmail.setKeyListener(null);
            passwordLayout.setVisibility(View.GONE);
            confPasswdLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btngetstart:
                // start next activity
                onGetStarted();
                break;
            case R.id.ibCancel:
                this.finish();
                overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit);
                break;
            default:
                break;
        }
    }

    private void onGetStarted() {

        if (mForm.isValid() && cbTermsService.isChecked() && doPasswordsMatch()) {
            String email = etEmail.getText().toString();
            String password;
            if (PreferencesManager.getInstance(this).isSocialLogin() == 1) {
                password = PreferencesManager.getInstance(this).getSocialToken();
            } else {
                password = etPasssword.getText().toString();
            }
            String name = etName.getText().toString();
            String phone = etPhoneNumber.getText().toString();
            if (phone.length() == 10) {
                updateUserData(email, password, name, phone);
            } else {
                Toast.makeText(SignupActivity.this, "Enter valid 10 digit phone number", Toast.LENGTH_LONG).show();
            }

        } else {
            if (!cbTermsService.isChecked()) {
                Toast.makeText(SignupActivity.this, "Please accept Terms and Conditions", Toast.LENGTH_LONG).show();
            } else if (!doPasswordsMatch()) {
                Toast.makeText(SignupActivity.this, "Passwords don't match", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean doPasswordsMatch() {
        return etPasssword.getText().toString().equals(etConfirmPass.getText().toString());
    }

    /*
     * @param password Password
     * @param pincode user pincode
     */
    private void updateUserData(String email, String passwd, String name, String phone) {

        //seting up single object to handle across multiple network call 
        SignUpRequestJson signup = new SignUpRequestJson();

        signup.setName(name);
        signup.setEmailId(email);
        if (PreferencesManager.getInstance(this).isSocialLogin() == 1) {
            signup.setSocialToken(passwd);
        } else {
            signup.setPassword(passwd);
        }
        signup.setContactNo(phone);

        if (isInternetPresent) {
            new RegisterAsyncTask().execute(signup);
        } else {
            Toast.makeText(SignupActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onVerificationSuccessful(int customerId, String phoneNumber) {
        PreferencesManager.getInstance(this).saveUserId(customerId);
        PreferencesManager.getInstance(this).saveContactNo(phoneNumber);
        allowToHomeScreen();
        PreferencesManager.getInstance(this).saveIsFirstLogin(true);
    }

    @Override
    public void onOTPCancelled() {
        finish();
    }

    public void caseRegistrationResponse(PostResponseJson response) {

        switch (response.getId()) {
            case 0:
//                allowToHomeScreen(Integer.parseInt(response.getMessage()));
                PreferencesManager.getInstance(this).saveProfileEmail(etEmail.getText().toString());
                Utilities.showOTPDialog(this, Integer.parseInt(response.getCustomerId()), etPhoneNumber.getText().toString());
                break;
            default:
                showDialog(response.getMessage(), "Warning");
                break;
        }
    }

    public void allowToHomeScreen() {
        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
        //Fetch profile details
        Intent intent = new Intent(this, FetchProfileDetailsService.class);
        startService(intent);

        intent = new Intent(SignupActivity.this, FetchFavGymsService.class);
        startService(intent);

        startNextActivity();
        finish();
    }

    public void showDialog(String msg, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setTitle(title).
                setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startNextActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void initValidationForm() {
        mForm = new Form(this);
        mForm.addField(Field.using(etEmail).validate(NotEmpty.build(this)).validate(IsEmail.build(this)));
        if (!(PreferencesManager.getInstance(this).isSocialLogin() == 1)) {
            mForm.addField(Field.using(etPasssword).validate(NotEmpty.build(this)));
            mForm.addField(Field.using(etConfirmPass).validate(NotEmpty.build(this)));
        }
        mForm.addField(Field.using(etName).validate(NotEmpty.build(this)));
        mForm.addField(Field.using(etPhoneNumber).validate(NotEmpty.build(this)).validate(IsPositiveInteger.build(this)));

    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void clearEditText() {
        etEmail.setText("");
        etPasssword.setText("");

    }

    class RegisterAsyncTask extends AsyncTask<SignUpRequestJson, Object, PostResponseJson> {


        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected PostResponseJson doInBackground(SignUpRequestJson... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            PostResponseJson response = null;
            int status = 0;

            try {

                connection = HttpConnectionUtil.getPostConnection(Apis.SIGNUP_URL);
                String requestData = HttpConnectionUtil.gson.toJson(params[0]);

                PrintWriter out = new PrintWriter(
                        connection.getOutputStream());
                out.print(requestData);
                out.flush();
                status = connection.getResponseCode();
                if (status == HttpConnectionUtil.POST_OK) {

                    reader = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                    String line = "";
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    String responseData = sb.toString();
                    Log.i("Registration", "response (RegisterUser) : " + responseData);

                    response = HttpConnectionUtil.gson.fromJson(responseData, new TypeToken<PostResponseJson>() {
                    }.getType());
                } else {
                    reader = new BufferedReader(new InputStreamReader(
                            connection.getErrorStream()));
                    String line = "";
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    Log.e("Registration", "response (RegisterUser) : " + sb.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;

        }

        @Override
        protected void onPostExecute(PostResponseJson result) {
            try {
                if ((progressDialog != null) && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            } catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            } catch (final Exception e) {
                // Handle or log or ignore
            }

            if (result != null)
                caseRegistrationResponse(result);
        }

    }

}
