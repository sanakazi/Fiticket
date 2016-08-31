package com.fitticket.viewmodel.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.PostResponseJson;
import com.fitticket.model.pojos.UpdateContactJson;
import com.fitticket.model.utils.WebServices;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by Fiticket on 05/01/16.
 */
public class ContactNumberDialog extends DialogFragment {

    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private static final String NAME_NUMBER = "NAME_NUMBER";
    private Context context;
    private int customerId;
    private String phoneNumber;
    private Activity mParentActivity;
    private ProgressDialog progressDialog;
    private EditText phonenumberedt;
    private Button verifyButton;
    FragmentInteractionListener listener;
    private boolean nameNumber;
    private LinearLayout nameLayout;
    private EditText nameEdt;

    public static ContactNumberDialog newInstance(int customerId,boolean nameNumber){
        ContactNumberDialog fragment= new ContactNumberDialog();
        Bundle args = new Bundle();
        args.putInt(CUSTOMER_ID, customerId);
        args.putBoolean(NAME_NUMBER, nameNumber);
        fragment.setArguments(args);
        return  fragment;
    }

    public ContactNumberDialog(){
        //Empty constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_MinWidth);
        setCancelable(true);
        customerId=getArguments().getInt(CUSTOMER_ID);
        nameNumber=getArguments().getBoolean(NAME_NUMBER);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentActivity=getActivity();
        progressDialog = new ProgressDialog(mParentActivity);
        listener=(FragmentInteractionListener)mParentActivity;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.contact_number_dialog,null);
        nameLayout=(LinearLayout)view.findViewById(R.id.nameLayout);
        nameEdt=(EditText)view.findViewById(R.id.edtName);
        phonenumberedt=(EditText)view.findViewById(R.id.edtphone);
        verifyButton=(Button)view.findViewById(R.id.btnVerify);
        final String url;
        if(nameNumber){
            url= Apis.UPDATE_NAME_CONTACT_URL;
        }else{
            nameLayout.setVisibility(View.GONE);
            url= Apis.UPDATE_CONTACT_OTP_URL;
        }
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name= nameEdt.getText().toString();
                String phone=phonenumberedt.getText().toString();
                if(validate(name,phone)) {
                    UpdateContactJson json = new UpdateContactJson();
                    json.setContactNo(phone);
                    json.setCustomerId(customerId);
                    if (nameNumber) {
                        json.setCustomerName(name);
                    }
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
            }
        });
        return view;
    }

    private boolean validate(String name, String phone) {
        if(nameNumber){
            if(name.length()>0 && phone.length()==10) {
                return true;
            }else if(name.length()==0){
                Toast.makeText(mParentActivity,"Enter a valid name",Toast.LENGTH_SHORT).show();
            }else if(phone.length()!=10){
                Toast.makeText(mParentActivity,"Enter a valid 10 digit phone number",Toast.LENGTH_LONG).show();
            }
        }else{
            if(phone.length()==10) {
                return true;
            }else{
                Toast.makeText(mParentActivity,"Enter a valid 10 digit phone number",Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    public interface FragmentInteractionListener{
       public void onUpdatePhoneNumberSuccess(int customerId, String phonenumber);
   }

    public void handleUpdateContactResponse(PostResponseJson result) {
        switch (result.getId()) {
            case 0:
                //On success, dismiss this dialog and call listener
                ContactNumberDialog.this.dismiss();
                listener.onUpdatePhoneNumberSuccess(customerId, phonenumberedt.getText().toString());
                break;
            case 1:
                //TODO- Show 'update-number screen'
                break;
            default:
                break;
        }
        Toast.makeText(mParentActivity, result.getMessage(), Toast.LENGTH_SHORT).show();
    }
}


