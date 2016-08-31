package com.fitticket.model.pojos;

import com.fitticket.model.pojos.base.BaseResponseJson;

/**
 * Created by Fiticket on 24/12/15.
 */
public class GetGymDataResponseJson extends BaseResponseJson{

    GymData data;

    public GymData getData() {
        return data;
    }

    public void setData(GymData data) {
        this.data = data;
    }

    private class GymData {
        GymDataJson gymData;

        public GymDataJson getGymData() {
            return gymData;
        }

        public void setGymData(GymDataJson gymData) {
            this.gymData = gymData;
        }
    }
}
