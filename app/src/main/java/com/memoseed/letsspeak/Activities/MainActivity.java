package com.memoseed.letsspeak.Activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.memoseed.letsspeak.Adapters.MainPagerAdapter;
import com.memoseed.letsspeak.Fragments.Chats;
import com.memoseed.letsspeak.Fragments.Friends;
import com.memoseed.letsspeak.GCM.GcmBroadcastReceiver;
import com.memoseed.letsspeak.GCM.GcmIntentService;
import com.memoseed.letsspeak.Models.ChatItem;
import com.memoseed.letsspeak.Models.FriendItem;
import com.memoseed.letsspeak.R;
import com.memoseed.letsspeak.UTils.TempValues;
import com.memoseed.letsspeak.UTils.UTils;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    String TAG = getClass().getSimpleName();
    String gcmTitle, gcmMessage;
    JSONObject messageJSON;
    public static GcmBroadcastReceiver gcmBroadcastReceiver = null;

    Toolbar toolbar;
    public static ProgressDialog pD;

    @ViewById
    public static FloatingActionButton fbRefresh;
    @Click
    void fbRefresh(){
        UTils.recreateActivityCompat(this);
    }

    @ViewById
    TextView txtTitle;
    @ViewById
    TextView txtId;
    @ViewById
    ImageView imLogOut;
    @Click
    void imLogOut(){
        UTils.showProgressDialog("Logging Out",getResources().getString(R.string.pleaseWait),pD);
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                UTils.hideProgressDialog(pD);
                startActivity(new Intent(MainActivity.this,SplashActivity_.class));
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        pD = new ProgressDialog(this);

        updateFromGCM();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(gcmBroadcastReceiver);
        super.onDestroy();
    }

    @ViewById
    TabLayout tabs;
    @ViewById
    ViewPager viewPager;
    MainPagerAdapter mainPagerAdapter;

    @AfterViews
    void afterViews(){
        txtTitle.setText(ParseUser.getCurrentUser().getString("full_name"));
        txtId.setText("id: "+ParseUser.getCurrentUser().getObjectId());
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                    if(position==0){
                        ((Chats)mainPagerAdapter.getItem(0)).getChats();
                    }else if(position== 1){
                        ((Friends)mainPagerAdapter.getItem(1)).getFriends();
                    }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((Chats)mainPagerAdapter.getItem(0)).getChats();
            }
        },1000);

    }

    public void updateFromGCM() {
        gcmBroadcastReceiver = new GcmBroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
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
                        Logger.getLogger(TAG).log(Level.INFO, extras.toString());

                        gcmTitle = extras.getString("title");
                        Log.d("titleGCM", gcmTitle);
                        gcmMessage = extras.getString("message");
                        Log.d("messageGCM", gcmMessage);
                        try {
                            messageJSON = new JSONObject(gcmMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (gcmTitle.contains("newMessage")) {
                            ((Chats)mainPagerAdapter.getItem(0)).getChats();
                            String senderIdd = null, date, message = null, name = null;
                            try {
                                date = messageJSON.getString("date");
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
                                    .setAutoCancel(true)
                                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                            Notification notification = builder.build();
                            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            nm.notify(UTils.createID(), notification);
                        }else if (gcmTitle.contains("newFriend")) {
                            ((Friends)mainPagerAdapter.getItem(1)).getFriends();
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
                                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                                Notification notification = builder.build();
                                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                nm.notify(UTils.createID(), notification);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };
        IntentFilter callInterceptorIntentFilter = new IntentFilter("com.google.android.c2dm.intent.RECEIVE");
        registerReceiver(gcmBroadcastReceiver, callInterceptorIntentFilter);
    }
}
