package com.applozic.mobicomkit.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicommons.commons.core.utils.SntpClient;

/**
 * Created by adarsh on 28/7/15.
 */
public class TimeChangeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("TimeChange :: ", "This has been called on date change");
                SntpClient sntpClient = new SntpClient();
                long diff = 0;
                if (sntpClient.requestTime("0.africa.pool.ntp.org", 30000)) {
                    long utcTime = sntpClient.getNtpTime() + SystemClock.elapsedRealtime() - sntpClient.getNtpTimeReference();
                    diff = utcTime - System.currentTimeMillis();
                    MobiComUserPreference.getInstance(context).setDeviceTimeOffset(diff);
                }

            }
        }).start();

    }
}
