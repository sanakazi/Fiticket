package com.fitticket.model.pojos;

/**
 * Created by Fiticket on 08/01/16.
 */
public class EditProfileResponse {

    private int id;
    private  String message;
    private ProfileData profile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProfileData getProfile() {
        return profile;
    }

    public void setProfile(ProfileData profile) {
        this.profile = profile;
    }

    public static class ProfileData {
        String contactNo;
        String emailId;
        String name;
        String firstName;
        String lastName;
        String packageEndDate;
        String packageName;
        String pincode;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContactNo() {
            return contactNo;
        }

        public void setContactNo(String contactNo) {
            this.contactNo = contactNo;
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPackageEndDate() {
            return packageEndDate;
        }

        public void setPackageEndDate(String packageEndDate) {
            this.packageEndDate = packageEndDate;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }
    }
}
