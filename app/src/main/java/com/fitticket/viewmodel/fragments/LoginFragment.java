package com.fitticket.viewmodel.fragments;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fitticket.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    ImageButton signUpButton;
    Button loginButton;
    Activity parentActivity;
    Context context;
    FragmentManager fragmentManager;
    FragmentInteractionListener listener;
    private LoginButton fbLoginBtn;
    private ImageButton googleLoginBtn;
    CallbackManager callbackManager;

    public interface FragmentInteractionListener{
        void onSignUpClicked();
        void onLoginSuccess();
    }
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager= CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_login, container, false);
        FacebookSdk.sdkInitialize(context);
        signUpButton= (ImageButton)view.findViewById(R.id.btnSignup);
        loginButton= (Button)view.findViewById(R.id.btn_login);
        fbLoginBtn= (LoginButton)view.findViewById(R.id.btn_fb_login);
        googleLoginBtn= (ImageButton)view.findViewById(R.id.btn_google_signin);
        fbLoginBtn.setFragment(this);
        fbLoginBtn.setReadPermissions("user_friends");
        fbLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
        setOnClickListeners();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        parentActivity=getActivity();
        listener=(FragmentInteractionListener)parentActivity;
    }

    private void setOnClickListeners() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSignUpClicked();

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLoginSuccess();
            }
        });
    }


}
