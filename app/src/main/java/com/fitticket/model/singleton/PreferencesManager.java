package com.fitticket.model.singleton;

import android.content.Context;
import android.content.SharedPreferences;

import com.fitticket.model.pojos.EditProfileResponse;
import com.fitticket.model.pojos.GetOperatingCitiesResponse;
import com.fitticket.model.pojos.ReferralDetailsResult;

/**
 * Created by Fiticket on 22/01/16.
 */
public class PreferencesManager {
    //Shared Preference Constants

    public static final String PREF_NAME = "User_Details";// This is the name used in older versions, dont change

    public static final String PREFKEY_USER_ID = "Id";
    public static final String PREFKEY_IS_SOCIAL_LOGIN = "IsSocialLogin";

    public static final String PROFILE_PHONE_NUMBER = "Profile.contactno";
    public static final String PROFILE_NAME = "Profile.customername";
    public static final String PROFILE_EMAIL = "Profile.emailid";
    public static final String PROFILE_PINCODE = "Profile.pincode";
    public static final String PROFILE_PACKAGE = "Profile.packages";
    public static final String PROFILE_PACKAGE_ENDDATE = "Profile.packageEndDate";
    public static final String PROFILE_SOCIAL_TOKEN = "Profile.Token";

    public static final String REFERRAL_TIMESTAMP = "REFERRAL_TIMESTAMP";
    public static final String REFERRAL_CODE = "REFERRAL_CODE";
    public static final String REFERRAL_URL = "REFERRAL_URL";
    public static final String REFERRAL_OFFER ="REFERRAL_OFFER" ;

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String SENT_TOKEN_ID_TO_SERVER = "sentTokenIdToServer";
    private static final String USER_SELECTED_CITY = "USER_SELECTED_CITY";
    private static final String FIRST_LAUNCH = "FIRST_LAUNCH6"; //Update this String during each release
    private static final String FIRST_RUN = "FIRST_RUN";
    private static final String PREFKEY_IS_FIRST_LOGIN = "PREFKEY_IS_FIRST_LOGIN";
    private static final String ACTIVITIES_TIMESTAMP = "ACTIVITIES_TIMESTAMP";
    private static final String GYMS_TIMESTAMP = "GYMS_TIMESTAMP";
    private static final String SELECTED_CITY_ID = "SELECTED_CITY_ID";
    private static final String SELECTED_CITY_NAME ="SELECTED_CITY_NAME" ;
    private static final String SELECTED_CITY_LAT = "SELECTED_CITY_LAT";
    private static final String SELECTED_CITY_LONG = "SELECTED_CITY_LONG";


    private SharedPreferences sPref;

    private static PreferencesManager preferencesManager;
    private long referralTimeStamp;


    private PreferencesManager(Context context){
        sPref= context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
    }

    public static PreferencesManager getInstance(Context context){
        if(preferencesManager ==null){
            preferencesManager= new PreferencesManager(context);
        }
        return  preferencesManager;
    }

    public Long getActivitiesTimeStamp() {
        return sPref.getLong(ACTIVITIES_TIMESTAMP,0);
    }

    public  Long getGymsTimeStamp() {
        return sPref.getLong(GYMS_TIMESTAMP,0);
    }


    public boolean isLoggedIn() {
        return (sPref.getInt(PREFKEY_USER_ID,0)!=0);
    }

    public int getUserId() {
        return (sPref.getInt(PREFKEY_USER_ID, 0));
    }

    public int isSocialLogin() {
        return (sPref.getInt(PREFKEY_IS_SOCIAL_LOGIN, 0));
    }

    public String getSocialToken() {
        return (sPref.getString(PROFILE_SOCIAL_TOKEN, ""));
    }


    public void saveProfileName(String profileName) {
        sPref.edit().putString(PROFILE_NAME, profileName).commit();
    }

    public void saveIsSocial(int isSocial) {
        sPref.edit().putInt(PREFKEY_IS_SOCIAL_LOGIN, isSocial).commit();
    }
    public void saveIsFirstLogin(boolean isFirstLogin) {
        sPref.edit().putBoolean(PREFKEY_IS_FIRST_LOGIN, isFirstLogin).commit();
    }
    public boolean isFirstLogin() {
        return (sPref.getBoolean(PREFKEY_IS_FIRST_LOGIN, false));
    }

    public boolean isFirstLaunch() {
        return (sPref.getBoolean(FIRST_LAUNCH, true));
    }
    public void saveIsFirstLaunch(boolean firstlaunch) {
        sPref.edit().putBoolean(FIRST_RUN, firstlaunch).commit();
    }


    public void saveProfileToken(String profileToken) {
        sPref.edit().putString(PROFILE_SOCIAL_TOKEN, profileToken).commit();
    }

    public void saveProfileEmail(String profileEmail) {
        sPref.edit().putString(PROFILE_EMAIL, profileEmail).commit();
    }

    public void saveProfileData(EditProfileResponse.ProfileData profile) {
        SharedPreferences.Editor edit= sPref.edit();
        edit.putString(PROFILE_NAME, profile.getFirstName() + " " + profile.getLastName());
        edit.putString(PROFILE_PHONE_NUMBER, profile.getContactNo());
        edit.putString(PROFILE_PACKAGE, profile.getPackageName());
        edit.putString(PROFILE_PACKAGE_ENDDATE, profile.getPackageEndDate());
        edit.putString(PROFILE_PINCODE, profile.getPincode());
        edit.putString(PROFILE_EMAIL, profile.getEmailId());
        edit.commit();
    }
    public EditProfileResponse.ProfileData getProfileData() {
        EditProfileResponse.ProfileData profileData= new EditProfileResponse.ProfileData();
        profileData.setName(sPref.getString(PROFILE_NAME, "-"));
        String[] nameArray=sPref.getString(PROFILE_NAME, "-").split(" ");
        if (nameArray.length>0)
        profileData.setFirstName(nameArray[0]);
        if(nameArray.length>1)
        profileData.setLastName(nameArray[1]);
        profileData.setContactNo(sPref.getString(PROFILE_PHONE_NUMBER, "-"));
        profileData.setPackageName(sPref.getString(PROFILE_PACKAGE, "-"));
        profileData.setPackageEndDate(sPref.getString(PROFILE_PACKAGE_ENDDATE, "-"));
        profileData.setPincode(sPref.getString(PROFILE_PINCODE, "-"));
        profileData.setEmailId(sPref.getString(PROFILE_EMAIL, "-"));
        return  profileData;
    }


    public void saveUserId(int id) {
        sPref.edit().putInt(PREFKEY_USER_ID, id).commit();
    }

    public void saveContactNo(String phoneNumber) {
        sPref.edit().putString(PROFILE_PHONE_NUMBER, phoneNumber).commit();
    }
    public void saveSelectedCityId(String selectedCityId){
        sPref.edit().putString(USER_SELECTED_CITY, selectedCityId).commit();
    }

    public String getSelectedCityId() {
        return (sPref.getString(USER_SELECTED_CITY, "0"));
    }

    public boolean isFirstRun() {
        return (sPref.getBoolean(FIRST_RUN, true));
    }

    public void saveIsFirstRun(boolean firstrun) {
        sPref.edit().putBoolean(FIRST_RUN, firstrun).commit();
    }

    public void saveActivitiesTimeStamp(long timeInMillis) {
        sPref.edit().putLong(ACTIVITIES_TIMESTAMP, timeInMillis).commit();

    }
    public void saveGymsTimeStamp(long timeInMillis) {
        sPref.edit().putLong(GYMS_TIMESTAMP, timeInMillis).commit();

    }

    public void clearUserPreferences() {

            SharedPreferences.Editor edit= sPref.edit();
            edit.putInt(PREFKEY_USER_ID,0);
            edit.putInt(PREFKEY_IS_SOCIAL_LOGIN, 0);
            edit.putString(PROFILE_NAME, "");
            edit.putString(PROFILE_PHONE_NUMBER, "");
            edit.putString(PROFILE_PACKAGE, "");
            edit.putString(PROFILE_PACKAGE_ENDDATE, "");
            edit.putString(PROFILE_PINCODE, "");
            edit.putString(PROFILE_SOCIAL_TOKEN, "");
            edit.putLong(REFERRAL_TIMESTAMP, 0);
            edit.putString(REFERRAL_CODE, "");
            edit.putString(REFERRAL_OFFER, "");
            edit.putString(REFERRAL_URL, "");
            edit.putBoolean(SENT_TOKEN_ID_TO_SERVER, false);
            edit.commit();
    }

    public void clearCityPreferences() {

        SharedPreferences.Editor edit= sPref.edit();
        edit.putLong(GYMS_TIMESTAMP, 0);
        edit.putLong(ACTIVITIES_TIMESTAMP, 0);
        edit.commit();
    }

    public void saveSentTokenToServer(boolean b) {
        sPref.edit().putBoolean(SENT_TOKEN_TO_SERVER, b).commit();
    }
    public void saveSentTokenIdToServer(boolean b) {
        sPref.edit().putBoolean(SENT_TOKEN_ID_TO_SERVER, b).commit();
    }

    public boolean isSentTokenToServer() {
        return sPref.getBoolean(SENT_TOKEN_TO_SERVER,false);
    }
    public boolean isSentTokenIdToServer() {
        return sPref.getBoolean(SENT_TOKEN_ID_TO_SERVER,false);    }

    public void saveReferralDetails(ReferralDetailsResult.ReferralDetails referralDetails) {
        SharedPreferences.Editor edit= sPref.edit();
        edit.putString(REFERRAL_CODE, referralDetails.getCode());
        edit.putString(REFERRAL_URL, referralDetails.getCodeURL());
        edit.putString(REFERRAL_OFFER,referralDetails.getMessage());
        edit.commit();
    }

    public ReferralDetailsResult.ReferralDetails getReferralDetails(){
        ReferralDetailsResult.ReferralDetails referralDetails= new ReferralDetailsResult.ReferralDetails();
        referralDetails.setCode(sPref.getString(REFERRAL_CODE, ""));
        referralDetails.setCodeURL(sPref.getString(REFERRAL_URL, ""));
        referralDetails.setMessage(sPref.getString(REFERRAL_OFFER, ""));
        return  referralDetails;
    }

    public void saveReferralTimeStamp(long time) {
        sPref.edit().putLong(REFERRAL_TIMESTAMP, time).commit();
    }

    public long getReferralTimeStamp() {
        return sPref.getLong(REFERRAL_TIMESTAMP,0);
    }

    public void saveSelectedCityJson(GetOperatingCitiesResponse.CitiesJson citiesJson) {
        SharedPreferences.Editor edit= sPref.edit();
        edit.putString(SELECTED_CITY_ID, citiesJson.getCityId());
        edit.putString(SELECTED_CITY_NAME, citiesJson.getCityName());
        edit.putFloat(SELECTED_CITY_LAT, (float)citiesJson.getCityLat());
        edit.putFloat(SELECTED_CITY_LONG, (float)citiesJson.getCityLong());
        edit.commit();
    }

    public GetOperatingCitiesResponse.CitiesJson getSelectedCityJson()
    {
        GetOperatingCitiesResponse.CitiesJson citiesJson=new GetOperatingCitiesResponse.CitiesJson();
        citiesJson.setCityId(sPref.getString(SELECTED_CITY_ID, ""));
        citiesJson.setCityName(sPref.getString(SELECTED_CITY_NAME, ""));
        citiesJson.setCityLat(sPref.getFloat(SELECTED_CITY_LAT, 0.0f));
        citiesJson.setCityLong(sPref.getFloat(SELECTED_CITY_LONG, 0.0f));
        return citiesJson;
    }

    public void clearTimeStamps() {
        sPref.edit().putLong(ACTIVITIES_TIMESTAMP, 0).commit();
        sPref.edit().putLong(GYMS_TIMESTAMP, 0).commit();
    }
}


