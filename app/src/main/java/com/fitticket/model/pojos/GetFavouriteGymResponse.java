package com.fitticket.model.pojos;

import java.util.ArrayList;

/**
 * Created by Fiticket on 07/03/16.
 */
public class GetFavouriteGymResponse {
    ArrayList<FavGymIdJson> GetFavouriteGymListResult;

    public ArrayList<FavGymIdJson> getGetFavouriteGymListResult() {
        return GetFavouriteGymListResult;
    }

    public void setGetFavouriteGymListResult(ArrayList<FavGymIdJson> getFavouriteGymListResult) {
        GetFavouriteGymListResult = getFavouriteGymListResult;
    }

    public class FavGymIdJson {
        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
