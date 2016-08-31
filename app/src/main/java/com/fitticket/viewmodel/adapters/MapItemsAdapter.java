package com.fitticket.viewmodel.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fitticket.R;
import com.fitticket.model.pojos.ActivityDetailJson;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.viewmodel.utils.Utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Fiticket on 01/12/15.
 */
public class MapItemsAdapter extends BaseAdapter{
    AppCompatActivity parentActivity;
    ArrayList<ActivityDetailJson> mActivitiesList;
    ActivitiesAdapter.ActivitySelectedListener listener;
    private int[] colors = new int[] { 0x30bdc3c7, 0x30ecf0f1 };
    public MapItemsAdapter(AppCompatActivity activity, ArrayList<ActivityDetailJson> activitiesList) {
        this.parentActivity=activity;
        listener=(ActivitiesAdapter.ActivitySelectedListener)activity;
        this.mActivitiesList=activitiesList;
    }

    @Override
    public int getCount() {
        return mActivitiesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mActivitiesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (View) inflater.inflate(R.layout.activities_listitem, null);
            holder = new ViewHolder();
            holder.gymLogoImgView = (NetworkImageView) view.findViewById(R.id.gymLogoImgView);
            holder.gymNameTxtView = (TextView) view.findViewById(R.id.gymNameTxtView);
            holder.actNameTxtView = (TextView) view.findViewById(R.id.activityNameTxtView);
            holder.timeSlotsLayout = (LinearLayout) view.findViewById(R.id.timeSlotLayout);
            holder.daysLayout = (LinearLayout) view.findViewById(R.id.daysLayout);
            holder.gymClickLayout = (LinearLayout) view.findViewById(R.id.gymClickLayout);
            holder.gymDetailsLayout = (LinearLayout) view.findViewById(R.id.gymDetailsLayout);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.gymNameTxtView.setText(mActivitiesList.get(position).getGymName());
        holder.actNameTxtView.setText(mActivitiesList.get(position).getActivityName());
        ImageLoader imageLoader = MySingleton.getInstance(parentActivity).getImageLoader();
        holder.gymLogoImgView.setImageUrl(mActivitiesList.get(position).getGymLogo(), imageLoader);
        holder.gymLogoImgView.setDefaultImageResId(R.drawable.ic_launcher);
        Utilities.setDayViews(holder.daysLayout);
        Utilities.setSelectedDay(parentActivity, 0, holder.daysLayout);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dayOFWeek = c.get(Calendar.DAY_OF_WEEK);
        Utilities.addTimeSlots(parentActivity, dayOFWeek, mActivitiesList.get(position).getSchedule(), holder.timeSlotsLayout, c, 0, 24,mActivitiesList.get(position));
        Utilities.addDayClickListeners(parentActivity, holder.daysLayout, mActivitiesList.get(position).getSchedule(), holder.timeSlotsLayout,0,24, mActivitiesList.get(position));
        holder.gymClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onActivitySelected(mActivitiesList.get(position).getId());
            }
        });
    return view;
    }

    private void addTimeSlots(ArrayList<ArrayList<ActivityDetailJson.ScheduleDetail>> timeSlotsArray, TextView timeSlotView) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dayOFWeek = c.get(Calendar.DAY_OF_WEEK);
        timeSlotView.setText("");
        if (!timeSlotsArray.get(dayOFWeek).isEmpty()) {
            for (ActivityDetailJson.ScheduleDetail timeSlot : timeSlotsArray.get(dayOFWeek)) {
                if (timeSlot.getIsFullDay().equalsIgnoreCase("false")) {
                    timeSlotView.append(timeSlot.getStartTime().substring(0, 5) + "  ");
                }else{
                    timeSlotView.setText("All Day");
                    break;
                }
            }
        }

    }

    static class ViewHolder{
         LinearLayout gymClickLayout;
        LinearLayout gymDetailsLayout;
        // each data item is just a string in this case
        TextView gymNameTxtView,actNameTxtView;
        LinearLayout daysLayout;
        LinearLayout timeSlotsLayout;
        NetworkImageView gymLogoImgView;
    }
}
