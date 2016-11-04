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
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.memoseed.letsspeak.Adapters.MessagesAdapter;
import com.memoseed.letsspeak.GCM.GcmBroadcastReceiver;
import com.memoseed.letsspeak.GCM.GcmIntentService;
import com.memoseed.letsspeak.Models.MessageItem;
import com.memoseed.letsspeak.R;
import com.memoseed.letsspeak.UTils.UTils;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

@EActivity(R.layout.activity_messages)
public class MessagesActiviry extends AppCompatActivity {

    String TAG = getClass().getSimpleName();
    String gcmTitle, gcmMessage;
    JSONObject messageJSON;
    public static GcmBroadcastReceiver gcmBroadcastReceiver = null;
    public static String senderId;
    ParseObject parseUser;

    List<String> users = new ArrayList<>();
    ProgressDialog pD;
    String chatId;

    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy - hh:mm aaa");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        format.setTimeZone(TimeZone.getDefault());
        pD = new ProgressDialog(this);
        senderId = getIntent().getExtras().getString("senderId");
        try {
            chatId = getIntent().getExtras().getString("chatId");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        users.add(ParseUser.getCurrentUser().getObjectId());
        users.add(senderId);

        getMessages();

        updateFromGCM();
    }


    public void getMessages() {
        if (UTils.isOnline(this)) {
            UTils.showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.pleaseWait), pD);
            ParseQuery query = ParseQuery.getQuery("_User");
            query.whereEqualTo("objectId", senderId);
            query.addDescendingOrder("createdAt");
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        parseUser = object;
                        setTitle(object.getString("full_name")+" ---- id: "+object.getObjectId());

                        ParseQuery query1 = ParseQuery.getQuery("Messages");
                        query1.whereContainsAll("users", users);
                        query1.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    UTils.hideProgressDialog(pD);
                                    for (ParseObject object1 : objects) {
                                        messagesAdapter.addMessage(new MessageItem(object1.getCreatedAt(),
                                                object1.getString("message"),
                                                object1.getString("from"),
                                                ""));
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
                                    finish();
                                    e.printStackTrace();
                                }


                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
                        finish();
                        e.printStackTrace();
                    }
                }

            });
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @ViewById
    EditText editMessage;
    @ViewById
    ImageView imSend;

    @Click
    void imSend() {
        if (editMessage.getText().toString().isEmpty()) {
            editMessage.setError(getString(R.string.empty_message));
        } else {
            if (UTils.isOnline(this)) {
                sendMessage();
            } else {
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
            }
        }
    }

    @ViewById
    RecyclerView rView;
    MessagesAdapter messagesAdapter;
    List<MessageItem> list = new ArrayList<>();

    @AfterViews
    void afterViews() {
        messagesAdapter = new MessagesAdapter(list, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        rView.setLayoutManager(linearLayoutManager);
        rView.setAdapter(messagesAdapter);
    }

    String message = "";

    private void sendMessage() {
        UTils.showProgressDialog(getString(R.string.sending), getResources().getString(R.string.pleaseWait), pD);
        ParseObject parseObject = new ParseObject("Messages");
        parseObject.put("message", editMessage.getText().toString());
        parseObject.put("users", users);
        parseObject.put("from", ParseUser.getCurrentUser().getObjectId());
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                UTils.hideProgressDialog(pD);
                if (e == null) {
                    editMessage.setText("");
                    messagesAdapter.addMessage(new MessageItem(parseObject.getCreatedAt(),
                            parseObject.getString("message"),
                            parseObject.getString("from"),
                            ParseUser.getCurrentUser().getString("full_name")));
                    message = parseObject.getString("message");

                    JSONObject messagee = new JSONObject();
                    try {
                        messagee.put("date", parseObject.getCreatedAt().getTime());
                        messagee.put("message", parseObject.getString("message"));
                        messagee.put("senderIdd", ParseUser.getCurrentUser().getObjectId());
                        messagee.put("name", ParseUser.getCurrentUser().getString("full_name"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    UTils.sendPush("newMessage", messagee.toString(), parseUser.getString("device_id"));

                } else {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
                }
            }
        });


        ParseQuery query = ParseQuery.getQuery("Chats");
        query.whereContainsAll("users", users);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Log.d(TAG,"1");
                    if(object!=null){
                        Log.d(TAG,"2");
                        object.put("last_message", message);
                        object.put("sender_name", ParseUser.getCurrentUser().getString("full_name"));
                        object.put("from", ParseUser.getCurrentUser());
                        object.put("to", parseUser);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d(TAG,"3");
                                } else {
                                    Log.d(TAG,"4");
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else{
                        Log.d(TAG,"5");
                        ParseObject parseObject2 = new ParseObject("Chats");
                        parseObject2.put("last_message", message);
                        parseObject2.put("sender_name", ParseUser.getCurrentUser().getString("full_name"));
                        parseObject2.put("users", users);
                        parseObject2.put("from", ParseUser.getCurrentUser());
                        parseObject2.put("to", parseUser);
                        parseObject2.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d(TAG,"6");
                                } else {
                                    Log.d(TAG,"7");
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                }else{
                    e.printStackTrace();
                    Log.d(TAG,"8");
                    ParseObject parseObject2 = new ParseObject("Chats");
                    parseObject2.put("last_message", message);
                    parseObject2.put("sender_name", ParseUser.getCurrentUser().getString("full_name"));
                    parseObject2.put("users", users);
                    parseObject2.put("from", ParseUser.getCurrentUser());
                    parseObject2.put("to", parseUser);
                    parseObject2.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG,"6");
                            } else {
                                Log.d(TAG,"7");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

        });


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

                            try {
                                long date = messageJSON.getLong("date");
                                String message = messageJSON.getString("message");
                                String senderIdd = messageJSON.getString("senderIdd");
                                String name = messageJSON.getString("name");
                                if (senderId.matches(senderIdd)) {
                                        messagesAdapter.addMessage(new MessageItem(new Date(date), message, senderIdd, name));

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
                                            .setGroup("newMessage")
                                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                                    Notification notification = builder.build();
                                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


                                    nm.notify(0, notification);
                                }
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
