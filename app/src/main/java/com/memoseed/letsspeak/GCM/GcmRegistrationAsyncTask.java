package com.memoseed.letsspeak.GCM;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.memoseed.letsspeak.UTils.TempValues;

import java.io.IOException;

/**
 * Created by Mohamed Sayed on 10/31/2016.
 */

public class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
    String regId;
    GoogleCloudMessaging gcm;
    Context context;

    public GcmRegistrationAsyncTask(Context context){
        this.context=context;
    }

    protected String doInBackground(Void... params) {
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            regId = gcm.register("956215793872");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        if(regId!=null && !regId.isEmpty()) {
            return regId;
        }else{
            return "";
        }
    }

    @Override
    protected void onPostExecute(String msg) {
        if(regId!=null) {
            Log.d("Registration ID :", regId);
            TempValues.regId = regId;
        }
    }
}
