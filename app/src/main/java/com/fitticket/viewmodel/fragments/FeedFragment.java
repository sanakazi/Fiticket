package com.fitticket.viewmodel.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.CampaignJson;
import com.fitticket.model.pojos.FeedsJson;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.adapters.MyPagingAdapter;
import com.fitticket.viewmodel.custom.CirclePageIndicator;
import com.fitticket.viewmodel.custom.ProgressBarCircular;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.Gson;
import com.paging.listview.PagingListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *  interface
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    private static final String TAG = FeedFragment.class.getSimpleName();
    private Button buttonticket, buttondumbbell, buttonIFeelLike,buttonFeeds;
    ViewPager mViewPager;
    View viewPagerLayout;
    CampaignPagerAdapter campaignPagerAdapter;
    ProgressBarCircular progressBar;
    PagingListView feedListView;
    MyPagingAdapter feedAdapter;
    private boolean campaignDone,feedDone;
    private ArrayList<FeedsJson.FeedDataJson.FeedData> feedsList;
    private ArrayList<CampaignJson.CampaignDataJson.CampaignFeed> campaignFeedsList;
    AppCompatActivity mParentActivity;
    String tweetCount="0";
    String blogCount="0";
    String systemCount="0";
    FragmentInteractionListener mListener;

    //For Quick Return view
    private LinearLayout mQuickReturnView;

    private static final int STATE_ONSCREEN = 0;


    private CirclePageIndicator circlePageIndicator;

    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        return fragment;
    }

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        viewPagerLayout=inflater.inflate(R.layout.feed_header, null, false);
        mViewPager=(ViewPager)viewPagerLayout.findViewById(R.id.campaignViewPager);
        //Set the height as screen width/3
        int widthInPixel= getActivity().getWindowManager().getDefaultDisplay().getWidth();
        mViewPager.getLayoutParams().height=widthInPixel/3;
        progressBar= (ProgressBarCircular)view.findViewById(R.id.progressBar);
        circlePageIndicator = (CirclePageIndicator)  viewPagerLayout.findViewById(R.id.indicator);
        feedListView = (PagingListView) view.findViewById(R.id.feedlistView);
        feedListView.addHeaderView(viewPagerLayout);
//        mQuickReturnView= (LinearLayout)view.findViewById(R.id.footerLayout);
        return  view;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener=(FragmentInteractionListener)getActivity();
        feedsList= new ArrayList<>();
        feedAdapter= new MyPagingAdapter(getActivity());
        feedListView.setAdapter(feedAdapter);
        addListviewListeners();
        feedListView.setHasMoreItems(true);
        feedListView.setPagingableListener(new PagingListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
//                progressBar.setVisibility(View.VISIBLE);
                triggerFeedVolleyRequest(Apis.FEED_URL + "?tweetCount=" + tweetCount + "&blogCount=" + blogCount
                        + "&systemCount=" + systemCount);
            }
        });

        triggerCampaignVolleyRequest(Apis.CAMPAIGN_URL);
        mParentActivity= (AppCompatActivity) getActivity();
    }

    /**
     *
     */

    private void addListviewListeners() {
        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    //Since listview has header, the position increased by 1, so we have to subtract 1 to get
                    // actual position
                    int actualPosition = position - 1;
                    if (feedsList.size() >= actualPosition && feedsList.get(actualPosition).getType().equalsIgnoreCase("post")) {
                        //Open the link in the browser if it is blogpost
                        String url = feedsList.get(actualPosition).getLink();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
            }
        });

        feedListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;
            private boolean mIsScrollingUp;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final ListView lw = feedListView;

                if (view.getId() == lw.getId()) {
                    final int currentFirstVisibleItem = lw.getFirstVisiblePosition();

                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        mIsScrollingUp = false;

                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        mIsScrollingUp = true;
                    }
                    //Do not hide footer until the first item has been scrolled up
                    //This is to avoid hiding when the list is really small
                    if(!(mIsScrollingUp==false&& currentFirstVisibleItem==0)) {
//                        mListener.onListScrolled(mIsScrollingUp);
                    }
                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    /*public void onListScrolled(boolean scrolledUp) {
        if (scrolledUp) {
//            mQuickReturnView.setVisibility(View.VISIBLE);
            mQuickReturnView.animate()
                    .translationY(0)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });


        } else {
            mQuickReturnView.animate()
                    .translationY(mQuickReturnView.getHeight())
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
//                            mQuickReturnView.setVisibility(View.GONE);
                        }
                    });
        }
    }
*/
    private void triggerCampaignVolleyRequest(String campaignUrl) {
        WebServices.triggerVolleyGetRequest(getActivity(), campaignUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        CampaignJson campaignJson = gson.fromJson(response, CampaignJson.class);
                        campaignFeedsList = refineList(campaignJson.getData().getSortedFeed());
                        campaignPagerAdapter = new CampaignPagerAdapter(getFragmentManager(), campaignFeedsList);
                        mViewPager.setAdapter(campaignPagerAdapter);
                        circlePageIndicator.setViewPager(mViewPager);
                        campaignDone = true;

                        hideProgressBar();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                Log.e(TAG,error.getMessage());
                        campaignDone = true;
                        hideProgressBar();
                    }
                });

    }

    //Method refines the campaign list by deleting unwanted campaigns
    private ArrayList<CampaignJson.CampaignDataJson.CampaignFeed> refineList(ArrayList<CampaignJson.CampaignDataJson.CampaignFeed> campaignFeeds) {
        ArrayList<CampaignJson.CampaignDataJson.CampaignFeed> campaignFeedsList= new ArrayList<>();
        Date date= new Date();
        for(CampaignJson.CampaignDataJson.CampaignFeed feed:campaignFeeds){
            if(feed.getStartDate()<date.getTime() && feed.getEndDate()>date.getTime() ){
                campaignFeedsList.add(feed);
            }
        }

        return campaignFeedsList;
    }

    private void triggerFeedVolleyRequest(String feedUrl) {
        WebServices.triggerVolleyGetRequest(getActivity(),feedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson= new Gson();
                        FeedsJson feeds= gson.fromJson(response,FeedsJson.class);
                        feedsList.addAll(feeds.getData().getSortedFeed());
                        //set new values for feedcounts
                        if(tweetCount.equalsIgnoreCase(feeds.getData().getTweetCount())&&
                                blogCount.equalsIgnoreCase(feeds.getData().getBlogCount())&&
                                systemCount.equalsIgnoreCase(feeds.getData().getSystemCount())) {
                            feedListView.onFinishLoading(false, null);

                        }else{
                            feedListView.onFinishLoading(true, feeds.getData().getSortedFeed());
                            tweetCount=feeds.getData().getTweetCount();
                            blogCount=feeds.getData().getBlogCount();
                            systemCount=feeds.getData().getSystemCount();
                        }
//                        feedAdapter.notifyDataSetChanged();
                        //Add the itemclick listener and scroll listeners
                        feedDone=true;
                        hideProgressBar();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                Log.e(TAG,error.getLocalizedMessage());
                        feedDone=true;
                        hideProgressBar();
                    }
                });
    }

    private void hideProgressBar() {
        // if both campaigns and feeds have been downloaded then hide progressbar
        if(campaignDone&& feedDone && progressBar.getVisibility()==View.VISIBLE){
            progressBar.setVisibility(View.GONE);
        }
    }


    public class CampaignPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<CampaignJson.CampaignDataJson.CampaignFeed> campaignList;

        public CampaignPagerAdapter(FragmentManager fm, ArrayList<CampaignJson.CampaignDataJson.CampaignFeed> campaignList) {
            super(fm);
            this.campaignList=campaignList;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            CampaignJson.CampaignDataJson.CampaignFeed campaign=campaignList.get(position);
            CampaignFragment campaignFragment= CampaignFragment.newInstance(campaign.getCampaignText(), campaign.getCampaignImage(),
                    campaign.getCampaignType(),campaign.getCampaignLink());
            return campaignFragment;
        }

        @Override
        public int getCount() {
            return campaignList.size();
        }
    }


    public interface FragmentInteractionListener{
        public void onListScrolled(boolean scrolledUp);
    }
}
