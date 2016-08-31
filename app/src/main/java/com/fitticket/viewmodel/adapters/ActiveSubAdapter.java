package com.fitticket.viewmodel.adapters;

import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fitticket.R;
import com.fitticket.model.constants.Constants;
import com.fitticket.model.pojos.ActiveSubDataJson;
import com.fitticket.model.pojos.GymDataJson;
import com.fitticket.model.singleton.MySingleton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Fiticket on 16/02/16.
 */
public class ActiveSubAdapter extends RecyclerView.Adapter<ActiveSubAdapter.ViewHolder> {
    private final Location mLocation;
    AppCompatActivity parentActivity;
    ArrayList<ActiveSubDataJson> mGymsList;
    ActiveSubDataJson[] activeSubDataList;
    //GymSelectedListener listener;
    private int[] colors = new int[]{0x30bdc3c7, 0x30ecf0f1};

    public ActiveSubAdapter(AppCompatActivity activity, ActiveSubDataJson[] activeSubDataList) {
        this.parentActivity = activity;
        //listener = (GymSelectedListener) activity;
        this.activeSubDataList = activeSubDataList;
        mLocation = MySingleton.getInstance(parentActivity).getMyLocation();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_sub_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //final ActiveSubDataJson activeData = mGymsList.get(position);
        int colorPos = position % colors.length;
        //holder.gymLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        /*ImageLoader imageLoader = MySingleton.getInstance(parentActivity).getImageLoader();
        if (gymData.getGymLogo() != null) {
            holder.logoImageview.setImageUrl(gymData.getGymLogo(), imageLoader);
        }*/
        holder.mPackageName.setText(activeSubDataList[position].getPackageName());
        holder.mPackageDate.setText(activeSubDataList[position].getPurchaseDate());
        holder.mPurchasePrice.setText(activeSubDataList[position].getPurchasePrice());
        holder.mStartDate.setText(activeSubDataList[position].getStartDate());
        holder.mEndDate.setText(activeSubDataList[position].getEndDate());
        /*if (gymData.getDistance() != 0 && gymData.getDistance() != Constants.VERY_LONG_DISTANCE) {
            DecimalFormat df = new DecimalFormat("#.#");
            holder.tvDistance.setText("" + df.format(gymData.getDistance()) + " KM");
        } else {
            holder.tvDistance.setText("-");
        }

        holder.tvaddress.setText(gymData.getBranchLocation());
        int[] activityCountArray = gymData.getActivityCount();
        Calendar c = Calendar.getInstance();
        int dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
        if (activityCountArray != null && activityCountArray.length == 7) {
            if (activityCountArray[dayOfTheWeek - 1] == 0) {
                holder.tvActivity.setText("No Activities today");
            } else {
                String activityString = activityCountArray[dayOfTheWeek - 1] == 1 ? "activity" : "activities";
                holder.tvActivity.setText("" + activityCountArray[dayOfTheWeek - 1] + " " + activityString + " today");
            }
        }
        holder.gymLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onGymSelected(gymData);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return activeSubDataList.length;
    }

    public interface GymSelectedListener {
        void onGymSelected(GymDataJson gymData);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mPackageName;
        private final TextView mPackageDate;
        private final TextView mPurchasePrice;
        private final TextView mStartDate;
        private final TextView mEndDate;


        public ViewHolder(View v) {
            super(v);

            mPackageName = (TextView) v.findViewById(R.id.packageName);
            mPackageDate = (TextView) v.findViewById(R.id.packageDateValue);
            mPurchasePrice = (TextView) v.findViewById(R.id.purchasePriceValue);
            mStartDate = (TextView) v.findViewById(R.id.startDateValue);
            mEndDate = (TextView) v.findViewById(R.id.endDateValue);

        }
    }

}

