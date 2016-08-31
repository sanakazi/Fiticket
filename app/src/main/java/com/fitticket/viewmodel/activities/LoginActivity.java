
package com.fitticket.viewmodel.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequest.GraphJSONObjectCallback;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.EditProfileResponse;
import com.fitticket.model.pojos.GetProfileDetailsJson;
import com.fitticket.model.pojos.LoginRequestJson;
import com.fitticket.model.pojos.PostResponseJson;
import com.fitticket.model.pojos.ReferralDetailsResult;
import com.fitticket.model.pojos.SignUpRequestJson;
import com.fitticket.model.services.FetchFavGymsService;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.HttpConnectionUtil;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.Validator.Field;
import com.fitticket.viewmodel.Validator.Form;
import com.fitticket.viewmodel.Validator.validations.IsEmail;
import com.fitticket.viewmodel.Validator.validations.NotEmpty;
import com.fitticket.viewmodel.fragments.ContactNumberDialog;
import com.fitticket.viewmodel.fragments.OTPDialogFragment;
import com.fitticket.viewmodel.utils.ConnectionDetector;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        OnConnectionFailedListener, ResultCallback<LoadPeopleResult>,ContactNumberDialog.FragmentInteractionListener,
        OTPDialogFragment.OnFragmentInteractionListener{

    private static final String READ_CONTACTS_PERMISSION = "android.permission.READ_CONTACTS";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACT =127 ;
    private Button login, btnForgetPassword;
    private EditText email, password;
    private Form mForm;
    private String promoCode;
    private Boolean isInternetPresent;
    private EditText input;
    private ImageButton btnCreateAccount;
    private ProgressDialog progressDialog;
    private Context context;
    private SharedPreferences sPref;
    private CallbackManager callbackManager;
    private String mEmail, token;
    private ImageButton googleLoginButton, fbLoginButton;
    String socialName = null;

    //private LoginButton fbLoginButton;
    final String TAG = LoginActivity.class.getSimpleName();
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /* Is there a result resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve results when possible? */
    private boolean mShouldResolve = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.PLUS_ME))
                .addScope(new Scope((Scopes.EMAIL)))
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .build();

        //        mGoogleApiClient = new GoogleApiClient.Builder(this)
        //        .addConnectionCallbacks(this)
        //        .addOnConnectionFailedListener(this).addApi(Plus.API, null)
        //        .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        setContentView(R.layout.activity_login);
        context = getApplicationContext();
        login = (Button) findViewById(R.id.login);
        btnForgetPassword = (Button) findViewById(R.id.btnForgetPassword);
        email = (EditText) findViewById(R.id.etDemail);
        password = (EditText) findViewById(R.id.etDpassword);
        btnCreateAccount = (ImageButton) findViewById(R.id.btnCreateAccount);
        fbLoginButton = (ImageButton) findViewById(R.id.login_button);
        googleLoginButton = (ImageButton) findViewById(R.id.sign_in_button);
        progressDialog = new ProgressDialog(this);
        initValidationForm();
        isInternetPresent = ConnectionDetector.isConnectingToInternet(this);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult result) {
                // TODO Auto-generated method stub
                token = result.getAccessToken().getUserId();
                Log.i("nishant-token", token);
                GraphRequest request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // TODO Auto-generated method stub


                        try {
                            socialName = object.getString("name");
                            PreferencesManager.getInstance(LoginActivity.this).saveProfileName(socialName);
                            Log.i("name-facebook", socialName);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        mEmail = object.optString("email");

                        PreferencesManager.getInstance(LoginActivity.this).saveIsSocial(1);
                        PreferencesManager.getInstance(LoginActivity.this).saveProfileToken(token);
                        PreferencesManager.getInstance(LoginActivity.this).saveProfileEmail(mEmail);

                        //Once Token is received call RegisterAsyncTAsk
                        SignUpRequestJson signup = new SignUpRequestJson();
                        signup.setName(socialName);
                        signup.setEmailId(mEmail);
                        signup.setSocialToken(token);
                        new RegisterAsyncTask().execute(signup);

                    }
                });

                Bundle fbParameter = new Bundle();
                fbParameter.putString("fields", " email, name");
                request.setParameters(fbParameter);
                request.executeAsync();

            }

            @Override
            public void onError(FacebookException error) {
            Log.e(TAG,error.toString());
            }

            @Override
            public void onCancel() {
                Log.e(TAG,"FB Login Cancelled");

            }
        });

        fbLoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));

            }
        });

        googleLoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Utilities.checkPermission(true,LoginActivity.this,Manifest.permission.GET_ACCOUNTS,MY_PERMISSIONS_REQUEST_READ_CONTACT)) {

                    mShouldResolve = true;
                    mGoogleApiClient.connect();
                }
            }
        });


        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!(email.getText().toString().trim().equals(""))
                    && !(password.getText().toString().equals(""))) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                    if (isInternetPresent) {
                        LoginRequestJson login = new LoginRequestJson();
                        login.setUserEmail(email.getText().toString());
                        login.setPassword(password.getText().toString());
                        new LoginAsynctask().execute(login);
                    }
                    else {
                        Toast.makeText(LoginActivity.this,
                                "No Internet Connection", Toast.LENGTH_LONG)
                                .show();
                    }
                }
                else {
                    setError();
                }
            }
        });

        btnForgetPassword.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,
                        ForgotPasswordWebview.class);
                startActivity(i);
            }
        });

        btnCreateAccount.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                launchRegistrationActivity();
            }
        });
    }

    private void launchRegistrationActivity() {
        Intent i = new Intent(LoginActivity.this,
                SignupActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mShouldResolve = true;
                    mGoogleApiClient.connect();

                } else {

                    Toast.makeText(LoginActivity.this,"Please provide permission to access contacts if you want to Sign in through your Google account",Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();

        }
    }

    @Override
    public void onUpdatePhoneNumberSuccess(int customerId, String phonenumber) {
        Utilities.showOTPDialog(this, customerId, phonenumber);
    }

    @Override
    public void onVerificationSuccessful(int customerId, String phoneNumber) {
        allowToHomeScreen(customerId);
        PreferencesManager.getInstance(this).saveIsFirstLogin(true);
    }

    @Override
    public void onOTPCancelled() {
        Toast.makeText(this,"Please verify phone number to login",Toast.LENGTH_LONG).show();
    }

    class LoginAsynctask extends AsyncTask<LoginRequestJson, Object, PostResponseJson> {

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected PostResponseJson doInBackground(LoginRequestJson... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            PostResponseJson login = null;
            int status = 0;
            try {

                connection = HttpConnectionUtil.getPostConnection(Apis.LOGIN_URL_2);
                String requestData = HttpConnectionUtil.gson.toJson(params[0]);

                PrintWriter out = new PrintWriter(connection.getOutputStream());
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
                    Log.i("Login", "response (LoginUser) : " + responseData);

                    login = HttpConnectionUtil.gson.fromJson(responseData,
                            new TypeToken<PostResponseJson>() {
                            }.getType());
                }
                else {
                    reader = new BufferedReader(new InputStreamReader(
                            connection.getErrorStream()));
                    String line = "";
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    Log.e("Login", "response (LoginUser) : " + sb.toString());
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return login;

        }

        @Override
        protected void onPostExecute(PostResponseJson login) {

            if (login != null) {
                updateData(login);
            }
        }

    }

    private void setError() {
        if(!password.getText().toString().isEmpty())
        email.setError("Invalid Email");
        password.setError("Invalid Password");
    }

    private void initValidationForm() {
        mForm = new Form(LoginActivity.this);
        mForm.addField(Field.using(email)
                .validate(NotEmpty.build(LoginActivity.this))
                .validate(IsEmail.build(LoginActivity.this)));
        mForm.addField(Field.using(password).validate(
                NotEmpty.build(LoginActivity.this)));
    }

    public void updateData(PostResponseJson login) {
        if (login.getId() != 0)
        {
            try {
                if ((progressDialog != null) && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }
            catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            }
            catch (final Exception e) {
                // Handle or log or ignore
            }
        }
        switch (login.getId()) {
            case -1:
            case 1:
            case 3:
                showDialog(login.getMessage(), "Warning");
                break;
            case 0://0: Successful login,
                allowToHomeScreen(Integer.parseInt(login.getCustomerId()));
                break;
            case 4:
                promptForNameNumber(Integer.parseInt(login.getCustomerId()),false);
                break;
            case 5:
                Utilities.showOTPDialog(this, Integer.parseInt(login.getCustomerId()), login.getCustomerContact());
                break;
            default:
                showDialog(login.getMessage(),"Warning");

        }

    }

    public void showDialog(String msg, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                LoginActivity.this);
        builder.setTitle(title).setMessage(msg).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Method prompt for name and number. If boolean is false, prompt only for number
    private void promptForNameNumber(int id, boolean nameNumber) {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("Phonedialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        ContactNumberDialog newFragment =  ContactNumberDialog.newInstance(id, nameNumber);
        newFragment.show(ft, "Phonedialog");

    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();

        }
        LoginManager.getInstance().logOut();

    }




    public void allowToHomeScreen(int id) {

        try {
            PreferencesManager.getInstance(this).saveUserId(id);
            //Fetch Profile details and save it in preferences
//            Intent intent = new Intent(this, FetchProfileDetailsService.class);
//            startService(intent);
            fetchProfileData();
//            new GetAllMasterDataAsyncTask().execute();

        }
        catch (Exception e) {
            Toast.makeText(this, getString(R.string.please_try_again),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchProfileData() {
        final PreferencesManager prefMan=PreferencesManager.getInstance(this);
        String url= Apis.GET_PROFILE_DETAIL_URL+prefMan.getUserId();
        showProgressDialog();
        WebServices.triggerVolleyGetRequest(this, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Profile Details response: " + response);
                GetProfileDetailsJson json = new Gson().fromJson(response, GetProfileDetailsJson.class);
                GetProfileDetailsJson.GetProfileDetailsResultClass profileDataResponse = json.getGetProfileDetailsResult();
                if (profileDataResponse != null) {
                    EditProfileResponse.ProfileData profile = new EditProfileResponse.ProfileData();
                    profile.setContactNo(profileDataResponse.getContactNo());
                    profile.setFirstName(profileDataResponse.getFirstName());
                    profile.setLastName(profileDataResponse.getLastName());
                    profile.setEmailId(profileDataResponse.getEmailId());
                    profile.setPackageEndDate(profileDataResponse.getPackageEndDate());
                    profile.setPackageName(profileDataResponse.getPackageName());
                    profile.setPincode(profileDataResponse.getPincode());
                    prefMan.saveProfileData(profile);
                    String url = Apis.GET_REFERRAL_CODE + prefMan.getUserId();
                    WebServices.triggerVolleyGetRequest(LoginActivity.this, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "Referral Details response: " + response);
                            dismissProgressDialog();
                            ReferralDetailsResult json = new Gson().fromJson(response, ReferralDetailsResult.class);
                            ReferralDetailsResult.ReferralDetails referralDetails = json.getGetReferalDetailsResult();
                            if (referralDetails != null) {
                                prefMan.saveReferralDetails(referralDetails);
                            }
                            launchActivity(MainActivity.class);
                            Intent intent = new Intent(LoginActivity.this, FetchFavGymsService.class);
                            startService(intent);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utilities.handleVolleyError(LoginActivity.this, error);
                            dismissProgressDialog();
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilities.handleVolleyError(LoginActivity.this, error);
                dismissProgressDialog();
            }
        });
    }

    public static boolean compareDatesByDateMethods(String compareDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        boolean isDateBefore = false;
        Date todayDate = new Date();
        Date newDate = null;
        try {
            newDate = df.parse(compareDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        if (todayDate.equals(newDate)) {
            isDateBefore = false;
        }
        if (todayDate.before(newDate)) {
            isDateBefore = false;
        }
        if (todayDate.after(newDate)) {
            isDateBefore = true;
        }
        return isDateBefore;
    }


    private void launchActivity(Class activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                }
                catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }
            else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                Log.d(TAG, "Connection not resolved");
            }
        }
        else {
            // Show the signed-out UI
            Log.d(TAG, "Signed out UI");
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                .setResultCallback(this);

        mEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
        Person p = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        if (p != null) {
            String name = p.getDisplayName();
            socialName = name;
            Log.d(TAG, name);
        }

        Log.d(TAG, mEmail);

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String token = null;
                final String SCOPES = "https://www.googleapis.com/auth/plus.login ";

                try {
                    /* token = GoogleAuthUtil.getToken(
                             getApplicationContext(),
                             Plus.AccountApi.getAccountName(mGoogleApiClient),
                             "oauth2:" + SCOPES); */
                    token = GoogleAuthUtil.getAccountId(getApplicationContext(), mEmail);
                    //                    Person p = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                    //                    p.getDisplayName();
                    //                   Log.i("Gogle login User name ", p.getDisplayName());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (GoogleAuthException e) {
                    e.printStackTrace();
                }

                return token;

            }

            @Override
            protected void onPostExecute(String token) {
                Log.i(TAG, "Access token retrieved:" + token);
                PreferencesManager.getInstance(LoginActivity.this).saveIsSocial(1);
                PreferencesManager.getInstance(LoginActivity.this).saveProfileToken(token);
                PreferencesManager.getInstance(LoginActivity.this).saveProfileEmail(mEmail);
                PreferencesManager.getInstance(LoginActivity.this).saveProfileName(socialName);
                //Once Token is received call RegisterAsyncTAsk
                SignUpRequestJson signup = new SignUpRequestJson();
                signup.setName(socialName);
                signup.setEmailId(mEmail);
                signup.setSocialToken(token);
                new RegisterAsyncTask().execute(signup);
//                RegisterAsyncTask reg = new RegisterAsyncTask();
//                reg.execute(param);
            }

        };
        task.execute();

    }

    @Override
    public void onConnectionSuspended(int cause) {
        // TODO Auto-generated method stub
        mGoogleApiClient.connect();

    }

    @Override
    public void onResult(LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    Log.i("Google name ", "Display name: " + personBuffer.get(i).getDisplayName());

                }
            }
            finally {
                personBuffer.release();
            }
        }
        else {
            Log.e(TAG, "Error requesting people data: " + peopleData.getStatus());
        }

    }



    class RegisterAsyncTask extends AsyncTask<SignUpRequestJson, Object, PostResponseJson> {



        @Override
        protected void onPreExecute() {
            showProgressDialog();

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
                }
                else {
                    reader = new BufferedReader(new InputStreamReader(
                            connection.getErrorStream()));
                    String line = "";
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    Log.e("Registration", "response (RegisterUser) : " + sb.toString());
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return response;

        }

        @Override
        protected void onPostExecute(PostResponseJson result) {
            try {
               dismissProgressDialog();

            }
            catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            }
            catch (final Exception e) {
                // Handle or log or ignore
            }

            if(result!=null){
                caseRegistrationResponse(result);
            }else{
                Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void dismissProgressDialog() {
        if ((progressDialog != null) && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }


    public void caseRegistrationResponse(PostResponseJson response) {

        switch (response.getId()) {
            case 0:
                allowToHomeScreen(Integer.parseInt(response.getCustomerId()));
                break;
            case 4:
                Toast.makeText(context,response.getMessage(),Toast.LENGTH_LONG).show();
                promptForNameNumber(Integer.parseInt(response.getCustomerId()), true);
                break;
            case 5:
                Toast.makeText(context,response.getMessage(),Toast.LENGTH_LONG).show();
                Utilities.showOTPDialog(this, Integer.parseInt(response.getCustomerId()), response.getCustomerContact());
                break;
            default:
                showDialog(response.getMessage(),"Warning");
                break;
        }
    }

}
