package com.memoseed.letsspeak.UTils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mohamed Sayed on 10/31/2016.
 */
public class UTils {

    public static boolean isOnline(Context context) {
        boolean connected = false;
//		boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    connected = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    connected = true;
        }
        return connected;
    }


    public static final void recreateActivityCompat(final Activity a) {

            final Intent intent = a.getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            a.finish();
            a.overridePendingTransition(0, 0);
            a.startActivity(intent);
            a.overridePendingTransition(0, 0);

    }

    public static void shareText(Activity activity, String text, String title) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
        activity.startActivity(Intent.createChooser(sharingIntent, title));
    }

    public static void showProgressDialog(String title, String message, ProgressDialog progressDialog) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressDialog(ProgressDialog progressDialog) {
        progressDialog.hide();
    }

    public static void sendPush(String titlee,String messagee,String deviceIdd){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://gcm-http.googleapis.com/gcm/send";

                    HttpClient client = HttpClientBuilder.create().build();
                    HttpPost post = new HttpPost(url);

                    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

                    urlParameters.add(new BasicNameValuePair("registration_id", deviceIdd));
                    urlParameters.add(new BasicNameValuePair("data.title", titlee));
                    urlParameters.add(new BasicNameValuePair("data.message", messagee));
                    post.setHeader("Authorization", "key=AIzaSyAJ3xW8KhQtJTvuQlab-IG9c85id9ua4_s");
                    //post.setHeader("Content-Type", "application/json");
                    post.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));

                    HttpResponse response = client.execute(post);
                    System.out.println("GCM Response Code : "
                            + response.getStatusLine().getStatusCode());

                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer result = new StringBuffer();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("result : ",response.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
    }

    public static int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }

    public static void show2OptionsDialoge(Activity activity, String msg, DialogInterface.OnClickListener listenerPos, DialogInterface.OnClickListener listenerNeg, String txtbtnP, String txtbtnN) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(txtbtnP, listenerPos)
                .setNegativeButton(txtbtnN, listenerNeg);
        AlertDialog alert = builder.create();
        alert.show();
    }

}
