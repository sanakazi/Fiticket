package com.fitticket.viewmodel.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.esotericsoftware.kryo.util.Util;
import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.pojos.PostResponseJson;
import com.fitticket.model.pojos.UpcomingActivitiesJson;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.adapters.ActivitiesAdapter;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class UpcomingFragment extends Fragment {

    private static final String FRAGMENT_TYPE = "FRAGMENT_TYPE";
    public static final int UPCOMING = 1;
    public static final int ATTENDED = 2;
    private static final String TAG = UpcomingFragment.class.getSimpleName();
    //Butterknife binding of views
    @Bind(R.id.upcomingList)
    RecyclerView mUpcomingListView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.noUppLayout)
    RelativeLayout noUpLayout;
    @Bind(R.id.noAttLayout)
    RelativeLayout noAttLayout;

    private FragmentActivity parentActivity;
    private LinearLayoutManager mLayoutManager;
    private int fragmentType;
    private String url;

    public static UpcomingFragment newInstance(int fragmentType) {
        UpcomingFragment fragment = new UpcomingFragment();
        Bundle args = new Bundle();
        args.putInt(FRAGMENT_TYPE,fragmentType);
        fragment.setArguments(args);
        return fragment;
    }

    public UpcomingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentType= getArguments().getInt(FRAGMENT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_upcoming, container, false);
        ButterKnife.bind(this, view);
        return  view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        parentActivity=getActivity();
        mLayoutManager = new LinearLayoutManager(parentActivity);
        mUpcomingListView.setLayoutManager(mLayoutManager);
        progressBar.setVisibility(View.VISIBLE);
        int customerId= PreferencesManager.getInstance(parentActivity).getUserId();
        //Set url based on fragment type
        if(fragmentType==UPCOMING) {
             url = Apis.GET_UPCOMING_ACTIVITIES_URL + customerId;
        }else if (fragmentType==ATTENDED){
             url = Apis.GET_ATTENDED_ACTIVITIES_URL + customerId;
        }
        triggerGetActivitiesRequest(url);
    }

    private void triggerGetActivitiesRequest(String url) {

        WebServices.triggerVolleyGetRequest(parentActivity, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Upcoming/Attended activities response:" + response);
                progressBar.setVisibility(View.GONE);
                UpcomingActivitiesJson[] upcomingActivitiesList = new Gson().fromJson(response, UpcomingActivitiesJson[].class);
                if (upcomingActivitiesList == null || upcomingActivitiesList.length == 0) {
                    mUpcomingListView.setVisibility(View.GONE);
                    if(fragmentType==UPCOMING) {
                        noUpLayout.setVisibility(View.VISIBLE);
                    }else if (fragmentType==ATTENDED){
                        noAttLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    UpcomingListAdapter adapter = new UpcomingListAdapter(parentActivity, upcomingActivitiesList);
                    mUpcomingListView.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Utilities.handleVolleyError(parentActivity, error);
            }
        });

    }

    public class UpcomingListAdapter extends RecyclerView.Adapter<UpcomingListAdapter.ViewHolder> {
        private UpcomingActivitiesJson[] activitiesList;
        private Context context;


        public UpcomingListAdapter(Context context,UpcomingActivitiesJson[] activitiesList) {
            this.activitiesList = activitiesList;
            this.context=context;
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            NetworkImageView activityimage;
            TextView activityname, gymname, branchname, time, date, status, statusLabel;
            LinearLayout images;
            TextView txtUnicode;
            ImageView cancleButton;
            public ViewHolder(View v) {
                super(v);
                activityimage = (NetworkImageView) v.findViewById(R.id.image);
                activityimage.setDefaultImageResId(R.drawable.fit);
                activityname = (TextView) v.findViewById(R.id.activity_name);
                branchname = (TextView) v.findViewById(R.id.baranchName);
                gymname = (TextView) v.findViewById(R.id.gymname);
                status = (TextView) v.findViewById(R.id.status);
                statusLabel = (TextView) v.findViewById(R.id.statusLabel);
                time = (TextView) v.findViewById(R.id.time);
                date = (TextView) v.findViewById(R.id.date);
                cancleButton = (ImageView) v.findViewById(R.id.cancle_button);
                txtUnicode = (TextView) v.findViewById(R.id.unique_pin);
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public UpcomingListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.upcoming_list_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            ImageLoader imageLoader= MySingleton.getInstance(context).getImageLoader();
            holder.activityimage.setImageUrl(activitiesList[position].getGymLogo(),imageLoader);
            holder.activityname.setText(activitiesList[position].getActivityName());
            holder.branchname.setText(activitiesList[position].getLocation());
            holder.date.setText(activitiesList[position].getEnrollmentDate());
            holder.gymname.setText(activitiesList[position].getGymName());
            holder.time.setText(activitiesList[position].getStartTime());
            holder.txtUnicode.setVisibility(View.VISIBLE);
            holder.txtUnicode.setText(activitiesList[position].getUniquePin());

            if(fragmentType==UPCOMING){
                holder.status.setVisibility(View.VISIBLE);
                if(activitiesList[position].getConfirmStatus()==0){
                    holder.status.setText("Processing Request");
                    holder.status.setBackgroundColor(getResources().getColor(R.color.SunYellow));
                }else if(activitiesList[position].getConfirmStatus()== 1){
                    holder.status.setText("Confirmed");
                    holder.status.setBackgroundColor(getResources().getColor(R.color.bbutton_success));
                }
                holder.cancleButton.setVisibility(View.VISIBLE);
            }else{
                holder.cancleButton.setVisibility(View.GONE);
                holder.status.setVisibility(View.GONE);
            }
            holder.cancleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(context)
                            .setTitle("Cancellation Confirmation")
                            .setMessage("Are you sure you don't want go for this activity ?")

                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    cancelActivity(parentActivity,activitiesList[position].getEnrollmentId());

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })

                            .show();
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return activitiesList.length;
        }
    }

    public void cancelActivity(final Activity parentActivity, String enrollmentId) {
        WebServices.triggerVolleyGetRequest(parentActivity, Apis.CANCEL_ENROLLMENT_URL + enrollmentId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                PostResponseJson result = new Gson().fromJson(response, PostResponseJson.class);
                handleCancelResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilities.handleVolleyError(parentActivity,error);
            }
        });
    }

    private void handleCancelResponse(PostResponseJson result) {
        switch (result.getId()) {
            case -1:
                showDialog(result.getMessage(), "Warning");
                break;
            case 1:
                showDialog(result.getMessage(), "Warning");
                break;
            case 0:
                triggerGetActivitiesRequest(url);
                break;

        }
    }

    public void showDialog(String msg, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(msg).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
