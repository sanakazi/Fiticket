package com.fitticket.viewmodel.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.R;

import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.GenerateOTPJson;
import com.fitticket.model.pojos.PostResponseJson;
import com.fitticket.model.pojos.UpdateContactJson;
import com.fitticket.model.pojos.VerifyOtpJson;
import com.fitticket.model.utils.WebServices;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by Fiticket on 30/12/15.
 */
public class OTPDialogFragment extends DialogFragment {
    private static final String TAG = OTPDialogFragment.class.getSimpleName();
    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    EditText otpEt, phonenumberedt;
    Button resendBtn,submitBtn,editNumberButton;
    private int customerId;
    private String phoneNumber;
    private ProgressDialog progressDialog;
    Activity mParentActivity;
    private Button cancelButton;
    OnFragmentInteractionListener listener;

    public static OTPDialogFragment newInstance(int customerId,String phoneNumber){
        OTPDialogFragment fragment= new OTPDialogFragment();
        Bundle args = new Bundle();
        args.putInt(CUSTOMER_ID, customerId);
        args.putString(PHONE_NUMBER, phoneNumber);
        fragment.setArguments(args);
        return  fragment;
    }

    public OTPDialogFragment(){
        //Empty constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_MinWidth);
        setCancelable(true);
        customerId=getArguments().getInt(CUSTOMER_ID);
        phoneNumber=getArguments().getString(PHONE_NUMBER);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentActivity=getActivity();
        progressDialog = new ProgressDialog(mParentActivity);
        listener=(OnFragmentInteractionListener)mParentActivity;
        setClickListeners();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_otp,null);
        otpEt=(EditText)view.findViewById(R.id.edtOtp);
        phonenumberedt=(EditText)view.findViewById(R.id.edt_phone_number);
        resendBtn=(Button)view.findViewById(R.id.resend_btn);
        submitBtn=(Button)view.findViewById(R.id.submit_btn);
        editNumberButton=(Button)view.findViewById(R.id.btn_edit_number);
        cancelButton=(Button)view.findViewById(R.id.cancel_btn);

        phonenumberedt.setText(phoneNumber);
        phonenumberedt.setTag(phonenumberedt.getKeyListener());
        phonenumberedt.setKeyListener(null);
        return view;
    }

    private void setClickListeners() {
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (phoneNumber.equals(phonenumberedt.getText().toString())) {
                    GenerateOTPJson json = new GenerateOTPJson();
                    json.setCustomerId(customerId);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    WebServices.triggerVolleyPostRequest(mParentActivity, json, Apis.GENERATE_OTP_URL, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            PostResponseJson result = new Gson().fromJson(response.toString(), PostResponseJson.class);
                            handleGenerateOTPResponse(result);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            error.printStackTrace();
                            Toast.makeText(mParentActivity, " OTP Generation Failed. Please try again", Toast.LENGTH_LONG).show();
                        }
                    }, GenerateOTPJson.class);

                } else {
                    UpdateContactJson json= new UpdateContactJson();
                    json.setContactNo(phonenumberedt.getText().toString());
                    json.setCustomerId(customerId);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    WebServices.triggerVolleyPutRequest(mParentActivity, json, Apis.UPDATE_CONTACT_OTP_URL,
                            new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            PostResponseJson result = new Gson().fromJson(response.toString(), PostResponseJson.class);
                            handleUpdateContactResponse(result);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            error.printStackTrace();
                            Toast.makeText(mParentActivity, "Failed to update Contact. Please try again", Toast.LENGTH_LONG).show();
                        }
                    }, UpdateContactJson.class);
                }
//                triggerGenerateOtpRequest();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpEt.getText().toString();
                if (TextUtils.isEmpty(otp)) {
                    otpEt.setError(getResources().getString(R.string.otp_empty));
                } else {
                    VerifyOtpJson json = new VerifyOtpJson();
                    json.setOtp(otp);
                    json.setCustomerId(customerId);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    WebServices.triggerVolleyPostRequest(mParentActivity, json, Apis.VERIFY_OTP_URL, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            PostResponseJson result = new Gson().fromJson(response.toString(), PostResponseJson.class);
                            handleVerifyResponse(result);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            error.printStackTrace();
                            Toast.makeText(mParentActivity, "OTP Verification Failed. Please try again", Toast.LENGTH_LONG).show();
                        }
                    }, VerifyOtpJson.class);
//                    triggerVerifyOtpRequest(otp);
                }
            }
        });
        editNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonenumberedt.setKeyListener((KeyListener)phonenumberedt.getTag());
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onOTPCancelled();
            }
        });
    }

    private void handleVerifyResponse(PostResponseJson result) {
        Toast.makeText(getActivity(),result.getMessage(),Toast.LENGTH_SHORT).show();
        switch(result.getId()){
            case 0:
                dismiss();
                listener.onVerificationSuccessful(customerId,phoneNumber);
                break;
            case 1:
            case 2:
            case 3:
                //do nothing. Just show a toast
                break;
        }
    }


    public void handleGenerateOTPResponse(PostResponseJson result) {

        switch (result.getId()) {
            case 0:

                break;
            case 1:
                //TODO- Show 'update-number screen'
                break;
            default:
                break;
        }
        Toast.makeText(mParentActivity, result.getMessage(), Toast.LENGTH_SHORT).show();

    }

    public void handleUpdateContactResponse(PostResponseJson result) {
        switch (result.getId()) {
            case 0:
                phoneNumber=phonenumberedt.getText().toString();
                break;
            case 1:
                //TODO- Show 'update-number screen'
                break;
            default:
                break;
        }
        Toast.makeText(mParentActivity, result.getMessage(), Toast.LENGTH_SHORT).show();
    }



    public interface OnFragmentInteractionListener{
        public void onVerificationSuccessful(int customerId, String phoneNumber);
        public void onOTPCancelled();
    }
}
