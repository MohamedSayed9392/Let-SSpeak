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
        Parse.initialize(this, "M9hBizYlpFuOdPUGn9m3nksuUWNia2M7g7K37aBe", "kMw5iD0WzvSx25cdmd2VNa5zgsKrGKrGddGeeeMW");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }



}
