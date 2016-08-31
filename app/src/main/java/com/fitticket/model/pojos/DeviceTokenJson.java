package com.fitticket.model.pojos;

/**
 * Created by Fiticket on 30/09/15.
 */
public class DeviceTokenJson {

    private int customerId;
    private String deviceId;
    private String deviceToken;
    private String deviceType;
    private String deviceModel;
    private String osVersion;
    private String appVersion;
    private String deviceName;
    private String latitude;
    private String longitude;

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

}
