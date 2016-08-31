package com.fitticket.model.constants;

public class Apis {

    //New APIs
    public static final String BASE_URL = "http://fitdev.cloudapp.net:8080/B2BAPIPlatform/";// UAT (JAVA)
//    public static final String BASE_URL = "http://bheem.fiticket.com:8080/APIServices/";// production (JAVA)
    public static final String BASE_URL_FEED = "http://bheem.fiticket.com:8080/";

//public static  final String BASE_URL="http://fiticketsvc.cloudapp.net:8080/";// Production

    public static final String FEED_URL = BASE_URL_FEED + "FiticketFeeds/GetFiticketFeeds/v2";
    public static final String CAMPAIGN_URL = BASE_URL_FEED + "FiticketFeeds/GetFiticketCampaigns/v1";
    public static final String GET_CATEGORIES_URL = BASE_URL + "GetActivityCategoryList/v1?cityId=";
    public static final String GET_CATEGORIES_GROUP_URL = BASE_URL + "GetCategoryGroup/v1?cityId=";
    public static final String GET_ACTIVITIES_BY_LOCATION_URL = BASE_URL + "GetActivitiesByLocation/v1";
    public static final String GET_ACTIVITY_DATA_URL = BASE_URL + "GetActivityData/v1?activityId=";
    public static final String GET_GYMS_BY_LOCATION = BASE_URL + "GetGymsByLocation/v1";
    public static final String GET_GYM_DATA_URL = BASE_URL + "GetGymData/v1?gymId=";

    public static final String GET_GYMS_BY_CITY_URL = BASE_URL + "GetGymsByCity/v1?cityId=";
    public static final String GET_ACTVITIES_BY_CITY_URL = BASE_URL + "GetActivitiesByCity/v1?cityId=";
    public static final String GET_ACTVITIES_BY_GYM_URL = BASE_URL + "GetActivitiesByGym/v1?gymId=";
    public static final String GET_ACTVITIES_BY_CITY_BY_CATEGORY_URL = BASE_URL + "GetActivitiesByCityByCategory/v1";

    // Legacy APIs

//    public static final String DOMAIN_NAME = "fiticket.com"; //Production (.NET)
        public static final String DOMAIN_NAME = "fitsql.cloudapp.net"; //UAT_AZURE (.NET)
    public static final String URL = "http://" + DOMAIN_NAME + "/WebServices/v1/AppService.svc/";

    //User details Apis
    public static final String UPDATE_CUSTOMER_DATA_URL = URL + "UpdateCustomer";
    public static final String UPDATE_CUSTOMER_DETAILS = URL + "UpdateCustomerDetails_v2";
    public static final String SIGNUP_URL = URL + "UserSignUp";
    public static final String GET_PROFILE_DETAIL_URL = URL + "GetProfileDetails?customerId=";

    //Activities Apis
    public static final String GET_UPCOMING_ACTIVITIES_URL = URL + "GetUpcomingActivities_v2?customerId=";
    public static final String GET_ATTENDED_ACTIVITIES_URL = URL + "GetAttendedActivities_v2?customerId=";
    public static final String GET_ACTIVE_SUB_URL = URL + "GetValidPackages?customerId=";
    public static final String GET_FAVORITE_GYM_URL = URL + "GetFavouriteGymList?customerId=";
    public static final String BOOKING_URL_2 = URL + "BookActivity_v2";
    public static final String LOGIN_URL_2 = URL + "UserLogin";
    public static final String MARK_GYM_FAV_URL = URL + "MarkGymFavourite";
    public static final String CANCEL_ENROLLMENT_URL = URL + "cancelEnrollment?enrollmentId=";

    public static final String GET_REFERRAL_CODE = URL + "GetReferalDetails?customerId=";
    public static final String GENERATE_OTP_URL = URL + "GenerateOTP";
    public static final String VERIFY_OTP_URL = URL + "VerifyOTP";
    public static final String UPDATE_CONTACT_OTP_URL = URL + "UpdateContact";
    ;
    public static final String UPDATE_NAME_CONTACT_URL = URL + "UpdateNameContact";
    ;

    public static final String CCAVENUE_URL = "http://" + DOMAIN_NAME + "/CCAvenue/PackagePayment.aspx?cid=";
    public static final String FORGOT_PASSWORD_URL = "http://" + DOMAIN_NAME + "/Account/Fortgot.aspx";
    public static final String URL_DEVICE_TOKEN_2 = URL + "DevicePushToken";
    public static final String SHOW_ONLY_SUBDCRIPTION_URL = "http://" + DOMAIN_NAME + "/CCAvenue/DisplayPackageOnly.aspx?cid=";

    public static final String GET_OPERATING_CITIES = BASE_URL + "GetOperatingCities/v1";
}
