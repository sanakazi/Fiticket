package com.fitticket.model.pojos;

import android.widget.ArrayAdapter;

import com.fitticket.model.pojos.base.BaseResponseJson;

import java.util.ArrayList;

/**
 * Created by Fiticket on 11/02/16.
 */
public class GetGymsByLocationResponse extends BaseResponseJson{
    GymListJson data;

    public GymListJson getData() {
        return data;
    }

    public void setData(GymListJson data) {
        this.data = data;
    }

    public class GymListJson {
        ArrayList<GymDataJson> gyms;

        public ArrayList<GymDataJson> getGyms() {
            return gyms;
        }

        public void setGyms(ArrayList<GymDataJson> gyms) {
            this.gyms = gyms;
        }
    }
}
