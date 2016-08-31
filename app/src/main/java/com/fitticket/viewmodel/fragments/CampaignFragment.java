package com.fitticket.viewmodel.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fitticket.R;
import com.fitticket.model.singleton.MySingleton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CampaignFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CampaignFragment extends Fragment {

    private static final String TITLE = "TITLE";
    private static final String IMAGE_URL ="IMAGE_URL" ;
    private static final String CAMPAIGN_TYPE ="CAMPAIGN_TYPE" ;
    private static final String CAMPAIGN_LINK ="CAMPAIGN_LINK" ;
    ImageLoader mImageLoader;
    NetworkImageView campaignImage;
    ImageView playButton;
    TextView campaignText;
    private String mTitle,mImageUrl,mCampaignType,mLink;
    private FrameLayout campaignLayout;

    public static CampaignFragment newInstance(String title, String imageUrl, String campaignType, String link) {
        CampaignFragment fragment = new CampaignFragment();
        Bundle args = new Bundle();
        args.putString(TITLE,title);
        args.putString(IMAGE_URL,imageUrl);
        args.putString(CAMPAIGN_TYPE,campaignType);
        args.putString(CAMPAIGN_LINK,link);
        fragment.setArguments(args);
        return fragment;
    }

    public CampaignFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        mTitle=getArguments().getString(TITLE);
        mCampaignType=getArguments().getString(CAMPAIGN_TYPE);
        mImageUrl=getArguments().getString(IMAGE_URL);
            mLink=getArguments().getString(CAMPAIGN_LINK);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.campaign_layout, container, false);
        campaignLayout=(FrameLayout)view.findViewById(R.id.campaignLayout);
        campaignImage=(NetworkImageView)view.findViewById(R.id.campaign_image);
        campaignImage.setDefaultImageResId(R.drawable.ic_default);
        campaignText =(TextView)view.findViewById(R.id.campaign_text);
        playButton=(ImageView)view.findViewById(R.id.playButtonImageView);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mImageLoader= MySingleton.getInstance(getActivity()).getImageLoader();
        populateViews();
        campaignLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Open the link in the browser
                if(!TextUtils.isEmpty(mLink)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(mLink));
                    startActivity(i);
                }
            }
        });
    }

    private void populateViews() {
        switch (mCampaignType){
            case "imageOnly":
                campaignText.setVisibility(View.GONE);
                break;
            case "videoText":
                playButton.setVisibility(View.VISIBLE);
                break;
            case "imageText":
                break;
            case "text":
                break;
            case "webview":
                break;
        }
        campaignText.setText(mTitle);
        campaignImage.setImageUrl(mImageUrl,mImageLoader);

    }


}
