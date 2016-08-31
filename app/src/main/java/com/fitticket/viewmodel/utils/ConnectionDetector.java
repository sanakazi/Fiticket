package com.fitticket.viewmodel.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ConnectionDetector {


    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo netInfo = connectivity.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    public static String md5(String input) {

        String md5 = null;

        if (null == input)
            return null;

        try {

            byte[] bytes = input.getBytes();
            // Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            // Update input string in message digest
            // digest.update(str.getBytes(), 0, str.length());
            byte[] bytdigest = digest.digest(bytes);

            // Converts message digest value in base 16 (hex)
            BigInteger bi = new BigInteger(1, bytdigest);
            // md5 = new BigInteger(1, digest.digest()).toString(16);
            md5 = String.format("%0" + (bytdigest.length << 1) + "X", bi);

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
        return md5;
    }
}
