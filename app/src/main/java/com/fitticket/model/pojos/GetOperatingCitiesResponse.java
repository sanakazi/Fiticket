package com.fitticket.model.pojos;

import com.fitticket.model.pojos.base.BaseResponseJson;

import java.util.ArrayList;

/**
 * Created by Fiticket on 11/02/16.
 */
public class GetOperatingCitiesResponse extends BaseResponseJson{
    public GetOperatingCitiesResponse() {
    }

    CityListJson data;

    public CityListJson getData() {
        return data;
    }

    public void setData(CityListJson data) {
        this.data = data;
    }

    public class CityListJson {
        ArrayList<CitiesJson> cities;

        public ArrayList<CitiesJson> getCities() {
            return cities;
        }

        public void setCities(ArrayList<CitiesJson> cities) {
            this.cities = cities;
        }
    }
    public static class CitiesJson {
        String cityId;
        String cityName;
        double cityLong;
        double cityLat;

        public double getCityLong() {
            return cityLong;
        }

        public void setCityLong(double cityLong) {
            this.cityLong = cityLong;
        }

        public double getCityLat() {
            return cityLat;
        }

        public void setCityLat(double cityLat) {
            this.cityLat = cityLat;
        }

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }
    }
}
