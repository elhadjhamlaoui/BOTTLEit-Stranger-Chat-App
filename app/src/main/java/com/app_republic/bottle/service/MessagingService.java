package com.app_republic.bottle.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.app_republic.bottle.BuildConfig;
import com.app_republic.bottle.MainActivity;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String ADMIN_CHANNEL_ID = "bottle";
    private NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String body = "";

        if (remoteMessage.getData().get("key").equals("state")) {
            body = remoteMessage.getData().get("name");

            if (remoteMessage.getData().get("decision").equals("keep")) {
                body = body + " opened your bottle";
            } else {
                body = body + " released your bottle";
            }
        } else if (remoteMessage.getData().get("key").equals("result")) {

            if (remoteMessage.getData().get("found").equals("true")) {
                String country = ServiceUtils.getCountryName(getApplicationContext(),remoteMessage.getData()
                        .get("country"));
                body = remoteMessage.getData().get("name") + " from " + country + " found your bottle";
            } else {
                body = " unfortunately your bottle didn't find anyone.";
            }

        } else if (remoteMessage.getData().get("key").equals("received_bottle")) {
            body = " You have received a new bottle!";

        }


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Setting up Notification channels for android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }
        int notificationId = new Random().nextInt(60000);


        Map<String, String> data = remoteMessage.getData();
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        if (data.get("type") != null) {

            if (data.get("type").equals("version")) {
                notificationIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="
                                + BuildConfig.APPLICATION_ID));
            } else if (data.get("type").equals("ad_app")) {
                notificationIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="
                                + data.get("app_id")));
            } else if (data.get("type").equals("ad_url")) {
                notificationIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(data.get("url")));
            } else {
                notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            }

            body = data.get("body");
        }


        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)  //a resource for your custom small icon
                .setContentTitle(getString(R.string.app_name)) //the "title" value you sent in your notification
                .setContentText(body) //ditto
                .setAutoCancel(true)  //dismisses the notification on click
                .setContentIntent(intent)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());


    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        FirebaseDatabase.getInstance().getReference().child("user").child(SharedPreferenceHelper
                .getInstance(getApplicationContext()).getUID()).child("token").setValue(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.GREEN);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}


