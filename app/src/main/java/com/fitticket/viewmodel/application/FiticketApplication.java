package com.fitticket.viewmodel.application;

import android.app.Application;

import io.paperdb.Paper;

/**
 * Created by Fiticket on 11/02/16.
 */
public class FiticketApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(getApplicationContext());
    }

}
