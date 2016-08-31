package com.fitticket.model.utils;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.fitticket.model.singleton.MySingleton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by Fiticket on 20/01/16.
 */
public class WebServices {

    public static final String SUCCESS_CODE = "0";

    public static void triggerVolleyGetRequest(Context context,String url,Response.Listener responseListener,Response.ErrorListener errorListener ){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,responseListener ,errorListener);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(context).getRequestQueue().add(stringRequest);
    }

    public static void triggerVolleyPostRequest(Context context, Object postRequestJson, String url, Response.Listener<JSONObject> responseListener,
                                                Response.ErrorListener errorListener, Type requestClassType){
        Gson gson= new Gson();
        JSONObject json= null;
        try {
            json = new JSONObject(gson.toJson(postRequestJson,requestClassType ));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,json,responseListener,errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void triggerVolleyPutRequest(Context context, Object postRequestJson, String url, Response.Listener<JSONObject> responseListener,
                                                Response.ErrorListener errorListener, Type requestClassType){
        Gson gson= new Gson();
        JSONObject json= null;
        try {
            json = new JSONObject(gson.toJson(postRequestJson,requestClassType ));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url,json,responseListener,errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

}
