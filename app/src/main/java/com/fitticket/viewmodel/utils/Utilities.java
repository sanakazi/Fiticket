package com.fitticket.viewmodel.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.constants.Constants;
import com.fitticket.model.pojos.ActivityDetailJson;
import com.fitticket.model.pojos.BookRequestJson;
import com.fitticket.model.pojos.CategoryJson;
import com.fitticket.model.pojos.GetOperatingCitiesResponse;
import com.fitticket.model.pojos.GymDataJson;
import com.fitticket.model.pojos.PostResponseJson;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.activities.BookingActivity;
import com.fitticket.viewmodel.activities.CCAvenueWebview;
import com.fitticket.viewmodel.activities.LoginActivity;
import com.fitticket.viewmodel.activities.MainActivity;
import com.fitticket.viewmodel.activities.SplashActivity;
import com.fitticket.viewmodel.activities.TrainningSlidesActivity;
import com.fitticket.viewmodel.custom.CustomTimePickerDialog;
import com.fitticket.viewmodel.fragments.OTPDialogFragment;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import io.paperdb.Paper;

/**
 * Created by Fiticket on 14/10/15.
 */
public class Utilities {
    private static final String TAG = Utilities.class.getSimpleName();
    private static int[] dayViewArray = {
            R.id.day1,
            R.id.day2,
            R.id.day3,
            R.id.day4,
            R.id.day5,
            R.id.day6,
            R.id.day7,
    };
    private static int[] dayNameViewArray = {
            R.id.day1Name,
            R.id.day2Name,
            R.id.day3Name,
            R.id.day4Name,
            R.id.day5Name,
            R.id.day6Name,
            R.id.day7Name,
    };

    public static boolean checkPermission(boolean showDialog, Activity activity, String permissionString, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, permissionString) != PackageManager.PERMISSION_GRANTED) {

            if (showDialog) {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{permissionString},
                        requestCode);
            }
            return false;
        } else {
            return true;
        }
    }

    public static String getFormattedTime(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        Date date = new Date(timeStamp);
        return date.toString();
    }

    public static void sortGymList(ArrayList<GymDataJson> gymList, Location mLastLocation) {

        if (!(gymList.isEmpty())) {
            for (GymDataJson gym : gymList) {
                if (!TextUtils.isEmpty(gym.getBranchLatitude()) && !TextUtils.isEmpty(gym.getBranchLongitude())) {
                    double gymLat = Double.parseDouble(gym.getBranchLatitude());
                    double gymLong = Double.parseDouble(gym.getBranchLongitude());
                    double distance = Utilities.distFrom(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                            gymLat, gymLong);
                    gym.setDistance(distance);
                } else {
                    gym.setDistance(Constants.VERY_LONG_DISTANCE);
                }
            }
            Collections.sort(gymList);
        }
    }

    public static void sortActivitiesList(ArrayList<ActivityDetailJson> activityList, Location mLastLocation) {

        if (activityList != null && !(activityList.isEmpty())) {
            for (ActivityDetailJson activity : activityList) {
                if (!TextUtils.isEmpty(activity.getGymLat()) && !TextUtils.isEmpty(activity.getGymLong())) {
                    double gymLat = Double.parseDouble(activity.getGymLat());
                    double gymLong = Double.parseDouble(activity.getGymLong());
                    double distance = Utilities.distFrom(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                            gymLat, gymLong);
                    activity.setDistance(distance);
                } else {
                    activity.setDistance(Constants.VERY_LONG_DISTANCE);
                }
            }
            Collections.sort(activityList);
        }
    }


    public static void addDayClickListeners(final AppCompatActivity activity, final LinearLayout daysLayout,
                                            final ArrayList<ArrayList<ActivityDetailJson.ScheduleDetail>> timeSlots,
                                            final LinearLayout timeSlotsLayout, final int startTime, final int endtime, final ActivityDetailJson activityDetailJson) {
        final Calendar c = Calendar.getInstance();
        for (int i = 0; i < dayViewArray.length; i++) {
            final int j = i;
            daysLayout.findViewById(dayViewArray[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c.setTime(new Date());
                    c.add(Calendar.DATE, j);
                    int dayOFWeek = c.get(Calendar.DAY_OF_WEEK);
                    addTimeSlots(activity, dayOFWeek, timeSlots, timeSlotsLayout, c, startTime, endtime, activityDetailJson);
                    setSelectedDay(activity, j, daysLayout);
                }
            });
        }
    }

    public static void setSelectedDay(Activity activity, int index, LinearLayout daysLayout) {
        for (int i = 0; i < dayViewArray.length; i++) {
            TextView tV = (TextView) daysLayout.findViewById(dayViewArray[i]);
            TextView dayName = (TextView) daysLayout.findViewById(dayNameViewArray[i]);
            if (i == index) {
                tV.setBackgroundResource(R.drawable.rounded_button_fiticket);
                tV.setTextColor(activity.getResources().getColor(R.color.White));
                dayName.setTextColor(activity.getResources().getColor(R.color.explore_yellow));

            } else {
                tV.setBackgroundResource(R.drawable.explore_button_yellow);
                tV.setTextColor(activity.getResources().getColor(R.color.explore_yellow));
                dayName.setTextColor(activity.getResources().getColor(R.color.Gray));

            }
        }
    }

    public static void addTimeSlots(final AppCompatActivity parentActivity, int dayOfWeek,
                                    final ArrayList<ArrayList<ActivityDetailJson.ScheduleDetail>> timeSlotsArray,
                                    LinearLayout containerView, final Calendar date, int startTime, int endtime, final ActivityDetailJson activityDetailJson) {
        containerView.removeAllViews();
        if (timeSlotsArray != null && !timeSlotsArray.get(dayOfWeek - 1).isEmpty()) {
            for (final ActivityDetailJson.ScheduleDetail timeSlot : timeSlotsArray.get(dayOfWeek - 1)) {
                if (timeSlot.getIsFullDay().equalsIgnoreCase("false")) {
                    int hour = Integer.parseInt(timeSlot.getStartTime().substring(0, 2));
                    //Show only those timeslots within the time filter
                    if (hour >= startTime && hour < endtime) {
                        TextView textView = (TextView) parentActivity.getLayoutInflater().inflate(R.layout.timeslot_textview, null);
                        textView.setText(timeSlot.getStartTime().substring(0, 5));
                        containerView.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (PreferencesManager.getInstance(parentActivity).isLoggedIn()) {


                                    if (activityDetailJson.isForNonMember()) {


                                        final String enrollmentDate = convertDateFormat(date, timeSlot.getStartTime());

                                        LayoutInflater inflater = parentActivity.getLayoutInflater();
                                        View alertLayout = inflater.inflate(R.layout.book_for_non_member, null);

                                        final TextView activityName = (TextView) alertLayout.findViewById(R.id.activity_name);
                                        final TextView activityDate = (TextView) alertLayout.findViewById(R.id.activity_date);
                                        final TextView activityTime = (TextView) alertLayout.findViewById(R.id.activity_time);
                                        final TextView activityPrice = (TextView) alertLayout.findViewById(R.id.activity_price);
                                        final TextView activityCancelDeadline = (TextView) alertLayout.findViewById(R.id.activity_cancel_deadline);
                                        final TextView activityPlace = (TextView) alertLayout.findViewById(R.id.activity_place);
                                        final TextView gymName = (TextView) alertLayout.findViewById(R.id.gym_name);

                                        String startTime = timeSlot.getStartTime().substring(0, 5);
                                        String endTime = timeSlot.getEndTime().substring(0, 5);

                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");

//                                    activityName.setText(activityDetailJson.getActivityName());
//                                    activityDate.setText("" + simpleDateFormat.format(date.getTime()));
//                                    activityTime.setText(startTime + " to " + endTime);
//                                    activityPlace.setText(activityDetailJson.getGymAddress());
//                                    gymName.setText(activityDetailJson.getGymName());
//                                    activityPrice.setText("Rs. " + activityDetailJson.getNonMemberCost());
//                                    activityCancelDeadline.setText("");

//                                        String payTmUrl = "http://fitsql.cloudapp.net/CCAvenue/PayPerActivityBooking.aspx?cid=" + PreferencesManager.getInstance(parentActivity).getUserId() + "&actid=" + timeSlot.getBookingId() + "&dt=" + enrollmentDate;
                                        String payTmUrl = "http://fiticket.com/CCAvenue/PayPerActivityBooking.aspx?cid=" + PreferencesManager.getInstance(parentActivity).getUserId() + "&actid=" + timeSlot.getBookingId() + "&dt=" + enrollmentDate;

                                        Intent bookpageIntent = new Intent(parentActivity, BookingActivity.class);
                                        bookpageIntent.putExtra("activityName", activityDetailJson.getActivityName());
                                        bookpageIntent.putExtra("activityDate", "" + simpleDateFormat.format(date.getTime()));
                                        bookpageIntent.putExtra("activityTime", "" + startTime + " to " + endTime);
                                        bookpageIntent.putExtra("activityPlace", activityDetailJson.getGymAddress());
                                        bookpageIntent.putExtra("activityPrice", "" + "Rs. " + activityDetailJson.getNonMemberCost());
                                        bookpageIntent.putExtra("gymName", activityDetailJson.getGymName());
                                        bookpageIntent.putExtra("payTmUrl", payTmUrl);
                                        parentActivity.startActivity(bookpageIntent);


//                                    AlertDialog.Builder alert = new AlertDialog.Builder(parentActivity);
//
//                                    Rect displayRectangle = new Rect();
//                                    Window window = parentActivity.getWindow();
//                                    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//
//                                    alertLayout.setMinimumWidth((int) (displayRectangle.width()));
//                                    alertLayout.setMinimumHeight((int) (displayRectangle.height()));
//
//                                    alert.setTitle("Booking confirmation");
//                                    alert.setView(alertLayout);
//                                    alert.setCancelable(true);
//                                    alert.setNegativeButton("Cancel", null);
//                                    alert.setPositiveButton("Book", null);


//                                    final AlertDialog alertDialog = alert.create();
//
//                                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                                        @Override
//                                        public void onShow(DialogInterface dialog) {
//                                            Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                                            Button negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//
//                                            positive.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
//
//
//                                                    Intent payTmIntent = new Intent(parentActivity, PayTmWebViewActivity.class);
//                                                    payTmIntent.putExtra("URL TO LOAD", payTmUrl);
//                                                    parentActivity.startActivity(payTmIntent);
//
//                                                    alertDialog.dismiss();
//                                                }
//                                            });
//
//                                            negative.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    alertDialog.dismiss();
//                                                }
//                                            });
//                                        }
//                                    });
//
//                                    alertDialog.show();
                                    } else {


                                        final BookRequestJson json = new BookRequestJson();
                                        json.setActivityId(Long.parseLong(timeSlot.getBookingId()));
                                        json.setCustomerId(PreferencesManager.getInstance(parentActivity).getUserId());
                                        final String enrollmentDate = convertDateFormat(date, timeSlot.getStartTime());
                                        json.setEnrollmentDate(enrollmentDate);
                                        new AlertDialog.Builder(
                                                parentActivity)
                                                .setTitle("Booking confirmation")
                                                .setMessage("Are you sure you want to Book this activity for " + enrollmentDate + "?")

                                                .setPositiveButton("Book", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        bookActivity(parentActivity, json);

                                                    }
                                                })
                                                .setNegativeButton(
                                                        android.R.string.no,
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // do nothing
                                                            }
                                                        })
                                                .setIcon(
                                                        android.R.drawable.ic_dialog_alert)
                                                .show();

                                    }
                                } else {
                                    showLoginAlert(parentActivity);

                                }
                            }
                        });
                    }
                } else {
                    TextView textView = (TextView) parentActivity.getLayoutInflater().inflate(R.layout.timeslot_textview, null);
                    textView.setText(parentActivity.getResources().getString(R.string.allday_text));
                    containerView.addView(textView);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (PreferencesManager.getInstance(parentActivity).isLoggedIn()) {
                                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(parentActivity, new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                        final BookRequestJson json = new BookRequestJson();
                                        json.setActivityId(Long.parseLong(timeSlot.getBookingId()));
                                        json.setCustomerId(PreferencesManager.getInstance(parentActivity).getUserId());
                                        //Add 0 padding to the left to make hour and minute 2 digits
                                        String startTime = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                                        final String enrollmentDate = convertDateFormat(date, startTime);
                                        json.setEnrollmentDate(enrollmentDate);

                                        new AlertDialog.Builder(
                                                parentActivity)
                                                .setTitle("Booking confirmation")
                                                .setMessage("Are you sure you want to Book this activity for " + enrollmentDate + "?")

                                                .setPositiveButton("Book", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        bookActivity(parentActivity, json);

                                                    }
                                                })
                                                .setNegativeButton(
                                                        android.R.string.no,
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // do nothing
                                                            }
                                                        })
                                                .setIcon(
                                                        android.R.drawable.ic_dialog_alert)
                                                .show();

                                    }
                                }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), false);
                                timePickerDialog.show();
                            } else {
                                showLoginAlert(parentActivity);
                            }
                        }
                    });
                    break;
                }
            }
        } else {
            TextView textView = (TextView) parentActivity.getLayoutInflater().inflate(R.layout.timeslot_textview, null);
            textView.setText(parentActivity.getResources().getString(R.string.no_Slots_text));
            containerView.addView(textView);
        }

    }

    private static String convertDateFormat(Calendar date, String startTime) {
        String outputDate;
        startTime = startTime.substring(0, 5);
        outputDate = "" + date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH) +
                " " + startTime;

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = date.getTime();

        String opDate = fmt.format(date1);

        return opDate + " " + startTime;
    }


    public static void bookActivity(final AppCompatActivity parentActivity, BookRequestJson json) {
        Log.d(TAG, "Booking request json: " + json.toString());
        WebServices.triggerVolleyPostRequest(parentActivity, json, Apis.BOOKING_URL_2, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "Booking response success: " + response.toString());
                PostResponseJson result = new Gson().fromJson(response.toString(), PostResponseJson.class);
                handleBookActivityResponse(parentActivity, result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Booking response error: " + error.toString());
                handleVolleyError(parentActivity, error);
            }
        }, BookRequestJson.class);
    }

    private static void handleBookActivityResponse(final AppCompatActivity parentActivity, PostResponseJson result) {
        int customerId = PreferencesManager.getInstance(parentActivity).getUserId();
        if (result.getId() == 5) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    parentActivity);
            builder.setTitle("Warning").setMessage(result.getMessage()).setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                String loadUrl = Apis.CCAVENUE_URL + "" + PreferencesManager.getInstance(parentActivity).getUserId();
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(loadUrl));
                                parentActivity.startActivity(browserIntent);
                            } else {
                                Intent i = new Intent(parentActivity, CCAvenueWebview.class);
                                parentActivity.startActivity(i);
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (result.getId() == 15) {
            showOTPDialog(parentActivity, customerId, result.getCustomerContact());

        } else if (result.getId() == 14) {
            Toast.makeText(parentActivity, result.getMessage(), Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(parentActivity, EditUserActivity.class);
//            parentActivity.startActivity(intent);
        } else if (result.getId() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    parentActivity);
            builder.setTitle("Request Received").setMessage(result.getMessage()).setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            BookingSuccessfulListener listener = (BookingSuccessfulListener) parentActivity;
                            listener.onBookingSucessful();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            showDialog(parentActivity, result.getMessage(), "Warning");
        }
    }


    public static void showDialog(Activity activity, String msg, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                activity);
        builder.setTitle(title).setMessage(msg).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void setDayViews(LinearLayout daysLayout) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        for (int i = 0; i < 7; i++) {
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            String dayOfWeek = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);

            TextView datetextView = (TextView) daysLayout.findViewById(dayViewArray[i]);
            datetextView.setText("" + (dayOfMonth));
            TextView dayTextView = (TextView) daysLayout.findViewById(dayNameViewArray[i]);
            dayTextView.setText(dayOfWeek);
            c.add(Calendar.DATE, 1);
        }
    }

    public static void showOTPDialog(AppCompatActivity activity, int id, String phoneNumber) {
        android.support.v4.app.FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        Fragment prev = activity.getSupportFragmentManager().findFragmentByTag("OTPdialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        OTPDialogFragment newFragment = OTPDialogFragment.newInstance(id, phoneNumber);
        newFragment.show(ft, "OTPdialog");
    }

    public static void showLoginAlert(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.loginRequired))
                .setMessage(activity.getResources().getString(R.string.login_warning_text))

                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //launch login activity
                        Intent i = new Intent(activity,
                                LoginActivity.class);
                        activity.startActivity(i);
                        activity.overridePendingTransition(R.anim.anim_up, R.anim.anim_down);

                    }
                })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })

                .show();
    }


    /*public static void setCustomActionBar(final Activity activity, String title) {
        activity.getActionBar().setDisplayShowCustomEnabled(true);
        activity.getActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(activity);
        View v = inflator.inflate(R.layout.custom_actionbar, null);
        ((TextView) v.findViewById(R.id.title)).setText(title);
        activity.getActionBar().setCustomView(v);
        v.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                activity.finish();
            }
        });
    }*/
    public static void setCustomActionBar(Activity activity, String title) {
        activity.getActionBar().setDisplayShowHomeEnabled(false);
        activity.getActionBar().setDisplayShowCustomEnabled(false);
        activity.getActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(activity);

        View v = inflator.inflate(R.layout.custom_actionbar, null);
        ((TextView) v.findViewById(R.id.title)).setText(title);
        ActionBar.LayoutParams params = new
                ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);

        activity.getActionBar().setCustomView(v, params);
        activity.getActionBar().setDisplayShowCustomEnabled(true);
        activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
    }


    public static void showToast(Context context, String statusMsg) {
        Toast.makeText(context, statusMsg, Toast.LENGTH_SHORT).show();
    }


    public static void handleVolleyError(Context context, VolleyError error) {
        error.printStackTrace();
        Toast.makeText(context, "Network Error, Please try again later", Toast.LENGTH_SHORT).show();
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; //KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public static void setActionBarIconVisibility(AppCompatActivity appCompatActivity, boolean visibility) {
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(visibility);
        appCompatActivity.getSupportActionBar().setHomeButtonEnabled(visibility);
    }

    public static void promptForCitySelection(final AppCompatActivity activity) {
        ArrayList<String> cityList = new ArrayList<>();
        if (MySingleton.getInstance(activity).getOperatingCities().isEmpty()) {
            String url = Apis.GET_OPERATING_CITIES;
            WebServices.triggerVolleyGetRequest(activity, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Operating cities get response:" + response);
                    GetOperatingCitiesResponse json = new Gson().fromJson(response, GetOperatingCitiesResponse.class);
                    ArrayList<GetOperatingCitiesResponse.CitiesJson> operatingCities = json.getData().getCities();
                    MySingleton.getInstance(activity).setOperatingCities(operatingCities);
                    ArrayList<String> cityList = new ArrayList<String>();
                    String currentCityId = PreferencesManager.getInstance(activity).getSelectedCityId();
                    int position = 0;
                    for (int i = 0; i < operatingCities.size(); i++) {
                        cityList.add(operatingCities.get(i).getCityName());
                        if (currentCityId.equals(operatingCities.get(i).getCityId()))
                            position = i;
                    }
                    showCitySelectionDialog(cityList.toArray(new String[cityList.size()]), activity, position);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    handleVolleyError(activity, error);
                    activity.finish();
                }
            });
        } else {
            ArrayList<GetOperatingCitiesResponse.CitiesJson> cityJsonList = MySingleton.getInstance(activity).getOperatingCities();
            String currentCityId = PreferencesManager.getInstance(activity).getSelectedCityId();
            int position = 0;
            for (int i = 0; i < cityJsonList.size(); i++) {
                cityList.add(cityJsonList.get(i).getCityName());
                if (currentCityId.equals(cityJsonList.get(i).getCityId()))
                    position = i;
            }
            showCitySelectionDialog(cityList.toArray(new String[cityList.size()]), activity, position);

        }


    }

    public static void showCitySelectionDialog(String[] cityList, final AppCompatActivity activity, int position) {

        new android.support.v7.app.AlertDialog.Builder(activity)
                .setTitle("Select City")
                .setSingleChoiceItems(cityList, position, null)
                .setPositiveButton(R.string.ok_button_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((android.support.v7.app.AlertDialog) dialog).getListView().getCheckedItemPosition();
                        PreferencesManager.getInstance(activity).saveSelectedCityId(MySingleton.getInstance(activity).getOperatingCities().get(selectedPosition).getCityId());
                        PreferencesManager.getInstance(activity).saveSelectedCityJson(MySingleton.getInstance(activity).getOperatingCities().get(selectedPosition));
                        PreferencesManager.getInstance(activity).clearCityPreferences();
                        //Clear DB & Singleton data
                        Paper.book().delete(Constants.ALL_ACTIVITY_LIST);
                        Paper.book().delete(Constants.ALL_GYM_LIST);
                        MySingleton.getInstance(activity).setmAllAcivitiesList(new ArrayList<ActivityDetailJson>());
                        MySingleton.getInstance(activity).setmAllGymsList(new ArrayList<GymDataJson>());
                        MySingleton.getInstance(activity).setCategoryList(new ArrayList<CategoryJson>());
                        Intent intent;
                        if (PreferencesManager.getInstance(activity).isFirstRun()) {
                            intent = new Intent(activity, TrainningSlidesActivity.class);
                            PreferencesManager.getInstance(activity).saveIsFirstRun(false);
                        } else {
                            //clear stack and Launch main activity
                            intent = new Intent(activity, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        }
                        activity.startActivity(intent);
                        activity.overridePendingTransition(0, 0);
                    }
                })
                .show();
    }

    private static void restartApp(AppCompatActivity activity) {
        Intent mStartActivity = new Intent(activity, SplashActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(activity, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public static String getCommaSeperatedCatIds(ArrayList<Integer> ids) {
        StringBuffer catIds = new StringBuffer();
        for (int i : ids) {
            catIds.append(i);
            catIds.append(",");
        }
        catIds.deleteCharAt(catIds.length() - 1);
        return catIds.toString();
    }

    public interface BookingSuccessfulListener {
        void onBookingSucessful();
    }

}
