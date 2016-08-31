package com.fitticket.model.pojos.base;

/**
 * Created by Fiticket on 11/02/16.
 */
public class BaseResponseJson {
    protected String statusCode;
    protected String statusMsg;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }
}
