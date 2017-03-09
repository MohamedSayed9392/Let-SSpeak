package com.memoseed.letsspeak;

import android.support.multidex.MultiDexApplication;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Mohamed Sayed on 10/31/2016.
 */
public class LetSSApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("i64m8Ro7ezLQMPQZPbUG16TGb9D3Kwev1gr4cwCk")
                .server("https://parseapi.back4app.com")
                .clientKey("sVBgq1NaFarfFG6YYTbYTtKyj1gKNLgxIWtNnao8").build());
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }



}
