package com.fitticket.model.pojos;

import com.fitticket.model.pojos.base.BaseResponseJson;

import java.util.ArrayList;

/**
 * Created by Fiticket on 24/12/15.
 */
public class GetActivityByCityResponseJson extends BaseResponseJson {


    ActivitiesList data;

    public ActivitiesList getData() {
        return data;
    }

    public void setData(ActivitiesList data) {
        this.data = data;
    }

    public class ActivitiesList {
        ArrayList<ActivityDetailJson> activities;

        public ArrayList<ActivityDetailJson> getActivities() {
            return activities;
        }

        public void setActivities(ArrayList<ActivityDetailJson> activities) {
            this.activities = activities;
        }
    }
}
