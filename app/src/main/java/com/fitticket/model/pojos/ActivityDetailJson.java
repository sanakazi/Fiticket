package com.fitticket.model.pojos;

import java.util.ArrayList;

/**
 * Created by Fiticket on 24/12/15.
 */
public class ActivityDetailJson implements Comparable<ActivityDetailJson> {
    String[] requirement;
    String activityTypeId;
    String gymLat;
    String gymId;
    String gymLocation;
    String gymWebSite;
    String gymName;
    String difficultyLevel;
    String gymLogo;
    String id;
    String actImages;
    String gymLong;
    String activityCategoryId;
    String externalActImages;
    String activityCategory;
    String gymAddress;
    String gymCity;
    String activityName;
    String activityDesc;
    String aboutGym;
    double distance;
    double nonMemberCost;
    boolean isForNonMember;
    boolean isForMember;
    ArrayList<ArrayList<ScheduleDetail>> schedule;

    //Empty constructor required for Paper DB
    public ActivityDetailJson() {
    }

    public boolean isForNonMember() {
        return isForNonMember;
    }

    public void setForNonMember(boolean forNonMember) {
        isForNonMember = forNonMember;
    }

    public boolean isForMember() {
        return isForMember;
    }

    public void setForMember(boolean forMember) {
        isForMember = forMember;
    }

    public double getNonMemberCost() {
        return nonMemberCost;
    }

    public void setNonMemberCost(double nonMemberCost) {
        this.nonMemberCost = nonMemberCost;
    }

    public String[] getRequirement() {
        return requirement;
    }

    public void setRequirement(String[] requirement) {
        this.requirement = requirement;
    }

    public String getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(String activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public String getGymLat() {
        return gymLat;
    }

    public void setGymLat(String gymLat) {
        this.gymLat = gymLat;
    }

    public String getGymId() {
        return gymId;
    }

    public void setGymId(String gymId) {
        this.gymId = gymId;
    }

    public String getGymLocation() {
        return gymLocation;
    }

    public void setGymLocation(String gymLocation) {
        this.gymLocation = gymLocation;
    }

    public String getGymWebSite() {
        return gymWebSite;
    }

    public void setGymWebSite(String gymWebSite) {
        this.gymWebSite = gymWebSite;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getGymLogo() {
        return gymLogo;
    }

    public void setGymLogo(String gymLogo) {
        this.gymLogo = gymLogo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActImages() {
        return actImages;
    }

    public void setActImages(String actImages) {
        this.actImages = actImages;
    }

    public String getGymLong() {
        return gymLong;
    }

    public void setGymLong(String gymLong) {
        this.gymLong = gymLong;
    }

    public String getActivityCategoryId() {
        return activityCategoryId;
    }

    public void setActivityCategoryId(String activityCategoryId) {
        this.activityCategoryId = activityCategoryId;
    }

    public String getExternalActImages() {
        return externalActImages;
    }

    public void setExternalActImages(String externalActImages) {
        this.externalActImages = externalActImages;
    }

    public String getActivityCategory() {
        return activityCategory;
    }

    public void setActivityCategory(String activityCategory) {
        this.activityCategory = activityCategory;
    }

    public String getGymAddress() {
        return gymAddress;
    }

    public void setGymAddress(String gymAddress) {
        this.gymAddress = gymAddress;
    }

    public String getGymCity() {
        return gymCity;
    }

    public void setGymCity(String gymCity) {
        this.gymCity = gymCity;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }

    public String getAboutGym() {
        return aboutGym;
    }

    public void setAboutGym(String aboutGym) {
        this.aboutGym = aboutGym;
    }

    public ArrayList<ArrayList<ScheduleDetail>> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<ArrayList<ScheduleDetail>> schedule) {
        this.schedule = schedule;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(ActivityDetailJson another) {
        if ((this.distance - another.distance) > 0)
            return 1;
        else if ((this.distance - another.distance) < 0)
            return -1;
        else
            return 0;
    }


    public static class ScheduleDetail {
        String startTime;
        String startDate;
        String bookingId;
        String isFullDay;
        String capacity;
        String endDate;
        String endTime;

        //Empty constructor required for Paper DB
        public ScheduleDetail() {
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getBookingId() {
            return bookingId;
        }

        public void setBookingId(String bookingId) {
            this.bookingId = bookingId;
        }

        public String getIsFullDay() {
            return isFullDay;
        }

        public void setIsFullDay(String isFullDay) {
            this.isFullDay = isFullDay;
        }

        public String getCapacity() {
            return capacity;
        }

        public void setCapacity(String capacity) {
            this.capacity = capacity;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

    }
}
