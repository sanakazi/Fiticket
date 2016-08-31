package com.fitticket.model.pojos;

import com.fitticket.model.pojos.base.BaseResponseJson;

import java.util.ArrayList;

/**
 * Created by Fiticket on 24/12/15.
 */
public class GetActivityDataResponse extends BaseResponseJson{

    ActivityData data;

    public ActivityData getData() {
        return data;
    }

    public void setData(ActivityData data) {
        this.data = data;
    }

    public class ActivityData {
       ActivityDetailJson activityData;

        public ActivityDetailJson getActivityData() {
            return activityData;
        }

        public void setActivityData(ActivityDetailJson activityData) {
            this.activityData = activityData;
        }
    }
}
