package com.fitticket.model.pojos;

/**
 * Created by Fiticket on 06/10/15.
 */
public class ReferralDetailsResult {
    private ReferralDetails GetReferalDetailsResult;

    public ReferralDetails getGetReferalDetailsResult() {
        return GetReferalDetailsResult;
    }

    public void setGetReferalDetailsResult(ReferralDetails getReferalDetailsResult) {
        GetReferalDetailsResult = getReferalDetailsResult;
    }

    public static class ReferralDetails {
        private String code;
        private String codeURL;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCodeURL() {
            return codeURL;
        }

        public void setCodeURL(String codeURL) {
            this.codeURL = codeURL;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
