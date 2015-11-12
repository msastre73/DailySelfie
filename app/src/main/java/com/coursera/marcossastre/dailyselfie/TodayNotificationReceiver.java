package com.coursera.marcossastre.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class TodayNotificationReceiver extends BroadcastReceiver {
    //Notification ID in case update is needed
    private static final int MY_NOTIFICATION_ID = 1;
    private static final String TAG = "NotificationReceiver";

    //Notification Text elements
    private final CharSequence tickerText = "Do you know what time is it?";

    //Notification View element
    private RemoteViews mContentView;

    //Notification Actions elements
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;

    //Notification vibration on arrival
    private final long[] mVibratePattern = {0, 200, 200, 300};

    @Override
    public void onReceive(Context context, Intent intent) {
        //The Intent to be used when the user click the Notification
        mNotificationIntent = new Intent(context, DailySelfieActivity.class);

        //The PendingIntent that wraps the underlying Intent
        mContentIntent = PendingIntent.getActivity(context, 0,mNotificationIntent, PendingIntent.FLAG_ONE_SHOT);

        //Get reference to the content view
        mContentView = new RemoteViews(context.getPackageName(), R.layout.notification);

        //Build the Notification
        Notification.Builder notificBuilder = new Notification.Builder(context)
                .setTicker(tickerText)
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setAutoCancel(true)
                .setContent(mContentView)
                .setContentIntent(mContentIntent)
                .setVibrate(mVibratePattern);

        //Gets the Notification Manager
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Passes the Notification to the Notification Manager
        mNotificationManager.notify(MY_NOTIFICATION_ID, notificBuilder.build());

        Log.i(TAG, "Notification Sent");








    }
}
