package com.fitticket.model.pojos;

/**
 * Created by Fiticket on 28/01/16.
 */
public class LoginRequestJson {
    private String userEmail;

    private String password;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
