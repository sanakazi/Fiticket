package com.fitticket.viewmodel.adapters;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fitticket.R;
import com.fitticket.model.constants.Constants;
import com.fitticket.model.pojos.ActivityDetailJson;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.viewmodel.utils.Utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Fiticket on 11/02/16.
 */
public class ActivitiesAdapterNew extends RecyclerView.Adapter<ActivitiesAdapterNew.ViewHolder> {
    AppCompatActivity parentActivity;
    ArrayList<ActivityDetailJson> mActivitiesList;
    ActivitySelectedListener listener;
    int dayOfTheWeek;
    Calendar cal;
    int startTime, endtime;
    private int[] colors = new int[]{0x30bdc3c7, 0x30ecf0f1};

    public ActivitiesAdapterNew(AppCompatActivity activity, ArrayList<ActivityDetailJson> activitiesList, int dayOfTheWeek, Calendar c, int startTime, int endTime) {
        this.parentActivity = activity;
        listener = (ActivitySelectedListener) activity;
        this.mActivitiesList = activitiesList;
        this.dayOfTheWeek = dayOfTheWeek;
        this.cal = c;
        this.startTime = startTime;
        this.endtime = endTime;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activities_listitem_new, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.gymNameTxtView.setText(mActivitiesList.get(position).getGymName());
        holder.actNameTxtView.setText(mActivitiesList.get(position).getActivityName());
        holder.gymLocality.setText(mActivitiesList.get(position).getGymLocation());
        if (mActivitiesList.get(position).getDistance() != 0 && mActivitiesList.get(position).getDistance() != Constants.VERY_LONG_DISTANCE) {
            DecimalFormat df = new DecimalFormat("#.#");
            holder.distance.setText("" + df.format(mActivitiesList.get(position).getDistance()) + " KM");
        } else {
            holder.distance.setText("-");
        }

        if (mActivitiesList.get(position).isForNonMember()) {
            holder.priceNonMember.setText("Rs. " + mActivitiesList.get(position).getNonMemberCost());
        } else {
            holder.priceNonMember.setVisibility(View.GONE);
        }

        ImageLoader imageLoader = MySingleton.getInstance(parentActivity).getImageLoader();
        holder.gymLogoImgView.setImageUrl(mActivitiesList.get(position).getGymLogo(), imageLoader);
        holder.gymLogoImgView.setDefaultImageResId(R.drawable.ic_launcher);
        Utilities.addTimeSlots(parentActivity, dayOfTheWeek, mActivitiesList.get(position).getSchedule(), holder.timeSlotsLayout, cal,
                startTime, endtime, mActivitiesList.get(position));
        holder.gymClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onActivitySelected(mActivitiesList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mActivitiesList.size();
    }

    public interface ActivitySelectedListener {
        void onActivitySelected(String activityId);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout gymClickLayout;
        public final LinearLayout gymDetailsLayout;
        // each data item is just a string in this case
        public TextView gymNameTxtView, actNameTxtView, gymLocality, distance, priceNonMember;
        public LinearLayout daysLayout;
        public LinearLayout timeSlotsLayout;
        NetworkImageView gymLogoImgView;

        public ViewHolder(View view) {
            super(view);
            gymLogoImgView = (NetworkImageView) view.findViewById(R.id.gymLogoImgView);
            gymNameTxtView = (TextView) view.findViewById(R.id.gymNameTxtView);
            actNameTxtView = (TextView) view.findViewById(R.id.activityNameTxtView);
            gymLocality = (TextView) view.findViewById(R.id.gymLocality);
            distance = (TextView) view.findViewById(R.id.distance);
            timeSlotsLayout = (LinearLayout) view.findViewById(R.id.timeSlotLayout);
            gymClickLayout = (LinearLayout) view.findViewById(R.id.gymClickLayout);
            gymDetailsLayout = (LinearLayout) view.findViewById(R.id.gymDetailsLayout);
            priceNonMember = (TextView) view.findViewById(R.id.non_member_price);
        }
    }
}
