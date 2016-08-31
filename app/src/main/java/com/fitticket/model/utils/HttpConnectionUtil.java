package com.fitticket.model.utils;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpConnectionUtil
{
	
    public static final Gson gson = new Gson();
    
  
    public static final String POST_REQUEST_METHOD = "POST";
	public static final String PUT_REQUEST_METHOD = "PUT";
	public static final String GET_REQUEST_METHOD = "GET";
	public static final int GET_OK = 200;
	public static final int PUT_OK = 200;
	public static final int POST_OK = 200;
   
    public static HttpURLConnection getPostConnection(String URL) throws IOException
    {
        URL url = new URL(URL);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        connection.setRequestMethod(POST_REQUEST_METHOD);
        connection.setRequestProperty("Content-Type",
                "application/json");
 
		connection.setConnectTimeout(15000);
        connection.setDoOutput(true);
        return connection;
    }
    
    
    public static HttpURLConnection getPutConnection(String URL) throws IOException
    {
    	URL url = new URL(URL);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        connection.setRequestMethod(PUT_REQUEST_METHOD);
     
        connection.setRequestProperty("Content-Type", "application/json");
		connection.setConnectTimeout(10000);

        return connection;
    }

  
    public static HttpURLConnection getGetConnection(String URL) throws IOException
    {
    	URL url = new URL(URL);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        connection.setRequestMethod(GET_REQUEST_METHOD);
		connection.setConnectTimeout(10000);
        return connection;
    }
}
