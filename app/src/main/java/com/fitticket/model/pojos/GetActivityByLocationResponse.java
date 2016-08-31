package com.fitticket.model.pojos;

import com.fitticket.model.pojos.base.BaseResponseJson;

import java.util.ArrayList;

/**
 * Created by Fiticket on 26/11/15.
 */
public class GetActivityByLocationResponse extends BaseResponseJson{

    GymActivitiesJson data;

    public GymActivitiesJson getData() {
        return data;
    }

    public void setData(GymActivitiesJson data) {
        this.data = data;
    }

    public class GymActivitiesJson {
        ArrayList<ActivityDetailJson> activities;

        public ArrayList<ActivityDetailJson> getActivities() {
            return activities;
        }

        public void setActivities(ArrayList<ActivityDetailJson> activities) {
            this.activities = activities;
        }
    }

    public class GymActivityJson {
        String id;
        String gymLong;
        String gymLat;
        String gymLocation;
        String gymName;
        String activityName;
        String gymLogo;
        String categoryId;
        ArrayList<ArrayList<ActivityDetailJson.ScheduleDetail>> timeSlots;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public ArrayList<ArrayList<ActivityDetailJson.ScheduleDetail>> getTimeSlots() {
            return timeSlots;
        }

        public void setTimeSlots(ArrayList<ArrayList<ActivityDetailJson.ScheduleDetail>> timeSlots) {
            this.timeSlots = timeSlots;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGymLong() {
            return gymLong;
        }

        public void setGymLong(String gymLong) {
            this.gymLong = gymLong;
        }

        public String getGymLat() {
            return gymLat;
        }

        public void setGymLat(String gymLat) {
            this.gymLat = gymLat;
        }

        public String getGymLocation() {
            return gymLocation;
        }

        public void setGymLocation(String gymLocation) {
            this.gymLocation = gymLocation;
        }

        public String getGymName() {
            return gymName;
        }

        public void setGymName(String gymName) {
            this.gymName = gymName;
        }

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public String getGymLogo() {
            return gymLogo;
        }

        public void setGymLogo(String gymLogo) {
            this.gymLogo = gymLogo;
        }
    }

    public class TimeSlots {
        String startTime;
        String isFullDay;

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getIsFullDay() {
            return isFullDay;
        }

        public void setIsFullDay(String isFullDay) {
            this.isFullDay = isFullDay;
        }
    }
}
