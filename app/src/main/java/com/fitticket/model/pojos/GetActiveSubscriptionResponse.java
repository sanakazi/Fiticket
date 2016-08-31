package com.fitticket.model.pojos;

import com.fitticket.model.pojos.base.BaseResponseJson;

import java.util.ArrayList;

/**
 * Created by Fiticket on 11/02/16.
 */
public class GetActiveSubscriptionResponse{
    /*ActiveListJson data;

    public ActiveListJson getData() {
        return data;
    }

    public void setData(ActiveListJson data) {
        this.data = data;
    }

    public class ActiveListJson {
        ArrayList<ActiveSubDataJson> active;

        public ArrayList<ActiveSubDataJson> getActive() {
            return active;
        }

        public void setActive(ArrayList<ActiveSubDataJson> active) {
            this.active = active;
        }
    }*/

    private ArrayList<ActiveSubDataJson> active = new ArrayList<ActiveSubDataJson>();

    public ArrayList<ActiveSubDataJson> getActive() {
        return active;
    }

    public void setActive(ArrayList<ActiveSubDataJson> postList) {
        this.active = (ArrayList<ActiveSubDataJson>)active;
    }
}
