package com.coursera.marcossastre.dailyselfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.CompoundButton;

/**
 * Created by Marcos Sastre on 14/11/2015.
 */

public class DailySwitchListener implements CompoundButton.OnCheckedChangeListener{

    private static final String TAG = "Switch Listener";

    private PendingIntent mNotificationReceiverPendingIntent;
    private Context mContext;

    private AlarmManager mAlarmManager =
            (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    private final static long ALARM_DELAY = 2 * 60 * 1000L;


    public DailySwitchListener(Context mContext) {
        this.mContext = mContext;
    }

    public DailySwitchListener() {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //If the switch changes to ON creates the alarm, else cancel it
        //Creates an Intent to broadcast to TodayNotificationReceiver
        final Intent mNotificationReceiverIntent = new Intent(mContext,
                TodayNotificationReceiver.class);

        //Checks if the Pending Intents existe (i.e the alarm is set)
        boolean isAlarmSet = (PendingIntent.getBroadcast(mContext, 0,
                mNotificationReceiverIntent, PendingIntent.FLAG_NO_CREATE) != null);

        if (isChecked) {
            if(!isAlarmSet){
                scheduleDailyNotifications(mNotificationReceiverIntent);
                Log.i(TAG, "Alarm set");
            }

        }else{
            if(isAlarmSet){
                mAlarmManager.cancel(mNotificationReceiverPendingIntent);
                mNotificationReceiverPendingIntent.cancel();
                Log.i(TAG, "Alarm canceled");
            }



        }
    }

    //Method to set the repetitive Alarm to fire periodical notifications
    private void scheduleDailyNotifications(Intent notificationReceiverIntent){
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                mContext, 0, notificationReceiverIntent, 0);

        //Creates the repeating alarm
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + ALARM_DELAY,
                ALARM_DELAY, mNotificationReceiverPendingIntent);


    }
}