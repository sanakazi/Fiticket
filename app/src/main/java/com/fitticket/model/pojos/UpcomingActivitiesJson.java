package com.fitticket.model.pojos;

/**
 * Created by Fiticket on 12/02/16.
 */
public class UpcomingActivitiesJson {
    private String activityName;
    private int confirmStatus;
    private String  endTime;
    private String  enrollmentDate;
    private String  enrollmentId;
    private String  gymLogo;
    private String  gymName;
    private int id;
    private boolean isFullDay;
    private String  location;
    private String  startTime;
    private String  uniquePin;

    public UpcomingActivitiesJson() {
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(int confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getGymLogo() {
        return gymLogo;
    }

    public void setGymLogo(String gymLogo) {
        this.gymLogo = gymLogo;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFullDay() {
        return isFullDay;
    }

    public void setIsFullDay(boolean isFullDay) {
        this.isFullDay = isFullDay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getUniquePin() {
        return uniquePin;
    }

    public void setUniquePin(String uniquePin) {
        this.uniquePin = uniquePin;
    }
}
