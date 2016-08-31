package com.fitticket.viewmodel.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fitticket.R;

/**
 * Created by Fiticket on 07/10/15.
 */
public class ReferralDialogFragment extends DialogFragment {
    private static final String REFERRAL_CODE ="REFERRAL_CODE" ;
    private static final String REFERRER_MESSAGE ="REFERRER_MESSAGE" ;
    private static final String REFERRED_MESSAGE = "REFERRED_MESSAGE";

    private String referralCode, referrerMessage, referredMessage;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static ReferralDialogFragment newInstance(String referralCode, String referrerMessage, String referredMessage) {
        ReferralDialogFragment f = new ReferralDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(REFERRAL_CODE,referralCode);
        args.putString(REFERRER_MESSAGE,referrerMessage);
        args.putString(REFERRED_MESSAGE,referredMessage);

        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Dialog_MinWidth);
        referralCode= getArguments().getString(REFERRAL_CODE);
        referrerMessage=getArguments().getString(REFERRER_MESSAGE);
        referredMessage=getArguments().getString(REFERRED_MESSAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_referral, container, false);
        TextView referralCodeTextView= (TextView)v.findViewById(R.id.referralCodeTextView);
        TextView referralBenefitTextView= (TextView)v.findViewById(R.id.referralBenefitTextView);
        Button referButton= (Button)v.findViewById(R.id.referralButton);
        referralCodeTextView.setText(referralCode);
        referralBenefitTextView.setText(referrerMessage);
        referButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, referredMessage);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,"Free FITICKET Classes!");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        return v;
    }

}
