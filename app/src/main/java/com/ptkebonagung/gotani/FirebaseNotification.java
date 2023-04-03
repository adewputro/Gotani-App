package com.ptkebonagung.gotani;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.ptkebonagung.gotani.activity.SptaActivity;
import com.ptkebonagung.gotani.utils.APIService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseNotification extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseService";
    private APIService apiService;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            sendNotification(remoteMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, SptaActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent,
                0);

        String channelID = getString(R.string.default_notification_channel_id);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setCategory(Notification.CATEGORY_CALL)
                        .setOngoing(true)
                        .setWhen(System.currentTimeMillis())
                        .setTicker("Hearty365")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.ic_logo_ka)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSound)
                        .setFullScreenIntent(pendingIntent, true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(channelID,
                    "FIRBASE NOTIFICATION CHANNEL", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("GoTani");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(1,notificationBuilder.build());
    }

    /*@Override
    public void onNewToken(String s) {
        Log.d(TAG,"Refreshed Token : "+s);
        //Preference.clearFCMToken(getApplicationContext());
        //Preference.setFcmToken(getApplicationContext(),s);

        final String email          = Preference.getKeyEmailRegistered(getBaseContext());
        final String key            = Preference.getKeyApi(getBaseContext());
        final String token          = s;

        Call<APIKey> updateToken    = apiService.updateToken(email, token, key);
        updateToken.enqueue(new Callback<APIKey>() {
            @Override
            public void onResponse(Call<APIKey> call, Response<APIKey> response) {
                Preference.clearFCMToken(getBaseContext());
                Preference.setFcmToken(getBaseContext(), token);
                Log.d(TAG," Token updated was successfull");
            }

            @Override
            public void onFailure(Call<APIKey> call, Throwable t) {
                Log.d(TAG," Token updated was not successfull");
            }
        });


        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }*/
}
