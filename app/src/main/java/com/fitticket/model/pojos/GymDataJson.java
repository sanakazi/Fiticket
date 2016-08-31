package com.fitticket.model.pojos;

/**
 * Created by Fiticket on 24/12/15.
 */
public class GymDataJson implements Comparable<GymDataJson> {
    String aboutUs;
    String branchLocation;
    String timeStamp;
    String branchLongitude;
    String website;
    String branchLatitude;
    String branchAddress;
    int [] activityCount;
    int gymId;
    int gymCity;
    String gymName;
    String [] gymBanner;
    String gymLogo;
    double distance;
    private boolean favorite;


    //Empty constructor required for Paper DB
    public GymDataJson() {
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public String getBranchLocation() {
        return branchLocation;
    }

    public void setBranchLocation(String branchLocation) {
        this.branchLocation = branchLocation;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getBranchLongitude() {
        return branchLongitude;
    }

    public void setBranchLongitude(String branchLongitude) {
        this.branchLongitude = branchLongitude;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBranchLatitude() {
        return branchLatitude;
    }

    public void setBranchLatitude(String branchLatitude) {
        this.branchLatitude = branchLatitude;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public int getGymId() {
        return gymId;
    }

    public void setGymId(int gymId) {
        this.gymId = gymId;
    }

    public int getGymCity() {
        return gymCity;
    }

    public void setGymCity(int gymCity) {
        this.gymCity = gymCity;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }


    public String getGymLogo() {
        return gymLogo;
    }

    public void setGymLogo(String gymLogo) {
        this.gymLogo = gymLogo;
    }

    public int[] getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(int[] activityCount) {
        this.activityCount = activityCount;
    }

    public String[] getGymBanner() {
        return gymBanner;
    }

    public void setGymBanner(String[] gymBanner) {
        this.gymBanner = gymBanner;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(GymDataJson another) {
        if ((this.distance-another.distance)>0)
            return 1;
        else if ((this.distance-another.distance)<0)
            return -1;
        else
            return 0;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isFavorite() {
        return favorite;
    }
}
