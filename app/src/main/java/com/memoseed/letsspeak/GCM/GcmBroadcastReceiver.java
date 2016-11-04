package com.memoseed.letsspeak.GCM;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.memoseed.letsspeak.Activities.MainActivity;
import com.memoseed.letsspeak.Activities.MainActivity_;
import com.memoseed.letsspeak.Activities.MessagesActiviry;
import com.memoseed.letsspeak.Activities.MessagesActiviry_;
import com.memoseed.letsspeak.R;
import com.memoseed.letsspeak.UTils.UTils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed Sayed on 10/31/2016.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {


    String title, message;
    JSONObject messageJSON;

    String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.d(TAG, extras.toString());
                title = extras.getString("title");
                Log.d("titleGCM", title);
                message = extras.getString("message");
                Log.d("messageGCM", message);

                try {
                    messageJSON = new JSONObject(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (title.contains("newMessage")) {
                    if(MessagesActiviry.gcmBroadcastReceiver==null) {
                        String senderIdd = null, date, message = null, name = null;
                        try {
                          //  date = messageJSON.getString("date");
                            message = messageJSON.getString("message");
                            senderIdd = messageJSON.getString("senderIdd");
                            name = messageJSON.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent resultIntent = new Intent(context, MessagesActiviry_.class)
                                .putExtra("senderId", senderIdd);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                .setContentTitle(name).setContentText(message)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setContentIntent(resultPendingIntent)
                                .setGroup("newMessage")
                                .setAutoCancel(true)
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                        Notification notification = builder.build();
                        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.notify(0, notification);
                    }

                }else if (title.contains("newFriend")) {

                    if(MainActivity.gcmBroadcastReceiver==null) {
                        try {
                            String image = messageJSON.getString("image");
                            String name = messageJSON.getString("name");
                            String objectId = messageJSON.getString("objectId");

                            Intent resultIntent = new Intent(context, MainActivity_.class);
                            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                    .setContentTitle("New Friend").setContentText(name)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(name))
                                    .setContentIntent(resultPendingIntent)
                                    .setAutoCancel(true)
                                    .setGroup("newFriend")
                                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                            Notification notification = builder.build();
                            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            nm.notify(1, notification);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }


}
