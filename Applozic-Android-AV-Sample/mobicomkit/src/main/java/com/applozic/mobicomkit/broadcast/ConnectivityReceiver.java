package com.applozic.mobicomkit.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.ApplozicIntentService;
import com.applozic.mobicommons.commons.core.utils.Utils;

/**
 * Created by devashish on 29/08/15.
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    static final private String TAG = "ConnectivityReceiver";
    static final private String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    Context context;
    private static boolean firstConnect = true;

    @Override
    public void onReceive(@NonNull final Context context,@NonNull Intent intent) {
        this.context = context;
        String action = intent.getAction();
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(action));
        Utils.printLog(context, TAG, action);

        if(BOOT_COMPLETED.equalsIgnoreCase(action)){
            Intent connectivityIntent = new Intent(context, ApplozicIntentService.class);
            connectivityIntent.putExtra(ApplozicIntentService.AL_SYNC_ON_CONNECTIVITY, true);
            ApplozicIntentService.enqueueWork(context,connectivityIntent);
        }

        if (CONNECTIVITY_CHANGE.equalsIgnoreCase(action)) {
            if (!Utils.isInternetAvailable(context)) {
                firstConnect = true;
                return;
            }
            if (!MobiComUserPreference.getInstance(context).isLoggedIn()) {
                return;
            }
            ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
                if (firstConnect) {
                    firstConnect = false;
                    Intent connectivityIntent = new Intent(context, ApplozicIntentService.class);
                    connectivityIntent.putExtra(ApplozicIntentService.AL_SYNC_ON_CONNECTIVITY, true);
                    ApplozicIntentService.enqueueWork(context,connectivityIntent);
                }
            }
        }
    }
}


