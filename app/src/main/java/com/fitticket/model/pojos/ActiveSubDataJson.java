package com.fitticket.model.pojos;

/**
 * Created by Fiticket on 24/12/15.
 */
public class ActiveSubDataJson {
    String endDate;
    String packageName;
    String purchaseDate;
    String purchasePrice;
    String startDate;

    //Empty constructor required for Paper DB
    public ActiveSubDataJson() {
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /*public String getBranchLatitude() {
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
    }*/
}
