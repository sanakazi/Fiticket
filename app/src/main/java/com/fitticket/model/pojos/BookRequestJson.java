package com.fitticket.model.pojos;

/**
 * Created by Fiticket on 20/01/16.
 */
public class BookRequestJson {
    private long activityId; // booking Id
    private int customerId;
    private String enrollmentDate; //yyyy-MM-dd HH:mm

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}
