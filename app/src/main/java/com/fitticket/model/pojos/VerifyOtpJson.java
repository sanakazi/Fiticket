package com.fitticket.model.pojos;

/**
 * Created by Fiticket on 30/12/15.
 */
public class VerifyOtpJson {
    private int customerId;
    private String otp;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
