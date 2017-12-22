package com.applozic.mobicomkit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.api.account.user.UserLogoutTask;
import com.applozic.mobicomkit.api.conversation.ApplozicMqttIntentService;
import com.applozic.mobicomkit.broadcast.ApplozicBroadcastReceiver;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.listners.AlLoginHandler;
import com.applozic.mobicomkit.listners.AlLogoutHandler;
import com.applozic.mobicomkit.listners.AlPushNotificationHandler;
import com.applozic.mobicomkit.listners.ApplozicUIListener;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

/**
 * Created by sunil on 29/8/16.
 */
public class Applozic {

    private static final String APPLICATION_KEY = "APPLICATION_KEY";
    private static final String DEVICE_REGISTRATION_ID = "DEVICE_REGISTRATION_ID";
    private static final String MY_PREFERENCE = "applozic_preference_key";
    public static Applozic applozic;
    public SharedPreferences sharedPreferences;
    private Context context;
    private ApplozicBroadcastReceiver applozicBroadcastReceiver;

    private Applozic(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(MY_PREFERENCE, context.MODE_PRIVATE);
    }

    public static Applozic init(Context context, String applicationKey) {
        applozic = getInstance(context);
        applozic.setApplicationKey(applicationKey);
        return applozic;
    }

    public static Applozic getInstance(Context context) {
        if (applozic == null) {
            applozic = new Applozic(context.getApplicationContext());
        }
        return applozic;
    }

    public String getApplicationKey() {
        return sharedPreferences.getString(APPLICATION_KEY, null);
    }

    public Applozic setApplicationKey(String applicationKey) {
        sharedPreferences.edit().putString(APPLICATION_KEY, applicationKey).commit();
        return this;
    }

    public String getDeviceRegistrationId() {
        return sharedPreferences.getString(DEVICE_REGISTRATION_ID, null);
    }

    public Applozic setDeviceRegistrationId(String registrationId) {
        sharedPreferences.edit().putString(DEVICE_REGISTRATION_ID, registrationId).commit();
        return this;
    }

    public static void disconnectPublish(Context context, String deviceKeyString, String userKeyString) {
        if (!TextUtils.isEmpty(userKeyString) && !TextUtils.isEmpty(deviceKeyString)) {
            Intent intent = new Intent(context, ApplozicMqttIntentService.class);
            intent.putExtra(ApplozicMqttIntentService.USER_KEY_STRING, userKeyString);
            intent.putExtra(ApplozicMqttIntentService.DEVICE_KEY_STRING, deviceKeyString);
            ApplozicMqttIntentService.enqueueWork(context, intent);
        }
    }

    public static void disconnectPublish(Context context) {
        final String deviceKeyString = MobiComUserPreference.getInstance(context).getDeviceKeyString();
        final String userKeyString = MobiComUserPreference.getInstance(context).getSuUserKeyString();
        disconnectPublish(context, deviceKeyString, userKeyString);
    }

    public static void connectPublish(Context context) {
        Intent subscribeIntent = new Intent(context, ApplozicMqttIntentService.class);
        subscribeIntent.putExtra(ApplozicMqttIntentService.SUBSCRIBE, true);
        ApplozicMqttIntentService.enqueueWork(context, subscribeIntent);
    }

    public static void subscribeToTyping(Context context, Channel channel, Contact contact) {
        Intent intent = new Intent(context, ApplozicMqttIntentService.class);
        if (channel != null) {
            intent.putExtra(ApplozicMqttIntentService.CHANNEL, channel);
        } else if (contact != null) {

        }
        intent.putExtra(ApplozicMqttIntentService.SUBSCRIBE_TO_TYPING, true);
        ApplozicMqttIntentService.enqueueWork(context, intent);
    }

    public static void unSubscribeToTyping(Context context, Channel channel, Contact contact) {
        Intent intent = new Intent(context, ApplozicMqttIntentService.class);
        if (channel != null) {
            intent.putExtra(ApplozicMqttIntentService.CHANNEL, channel);
        } else if (contact != null) {

        }
        intent.putExtra(ApplozicMqttIntentService.UN_SUBSCRIBE_TO_TYPING, true);
        ApplozicMqttIntentService.enqueueWork(context, intent);
    }

    public static void publishTypingStatus(Context context, Channel channel, Contact contact, boolean typingStarted) {
        Intent intent = new Intent(context, ApplozicMqttIntentService.class);

        if (channel != null) {
            intent.putExtra(ApplozicMqttIntentService.CHANNEL, channel);
        } else if (contact != null) {
            intent.putExtra(ApplozicMqttIntentService.CONTACT, contact);
        }
        intent.putExtra(ApplozicMqttIntentService.TYPING, typingStarted);
        ApplozicMqttIntentService.enqueueWork(context, intent);
    }

    public static void loginUser(Context context, User user, AlLoginHandler loginHandler) {
        if (MobiComUserPreference.getInstance(context).isLoggedIn()) {
            RegistrationResponse registrationResponse = new RegistrationResponse();
            registrationResponse.setMessage("User already Logged in");
            loginHandler.onSuccess(registrationResponse, context);
        } else {
            new UserLoginTask(user, loginHandler, context).execute();
        }
    }

    public static void logoutUser(Context context, AlLogoutHandler logoutHandler) {
        new UserLogoutTask(logoutHandler, context).execute();
    }

    public static void registerForPushNotification(Context context, String pushToken, AlPushNotificationHandler handler) {
        new PushNotificationTask(context, pushToken, handler).execute();
    }

    public static void registerForPushNotification(Context context, AlPushNotificationHandler handler) {
        registerForPushNotification(context, Applozic.getInstance(context).getDeviceRegistrationId(), handler);
    }


    public void registerUIListener(ApplozicUIListener applozicUIListener) {
        applozicBroadcastReceiver = new ApplozicBroadcastReceiver(context, applozicUIListener);
        LocalBroadcastManager.getInstance(context).registerReceiver(applozicBroadcastReceiver, BroadcastService.getIntentFilter());
    }

    public void unregisterUIListener() {
        if (applozicBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(applozicBroadcastReceiver);
            applozicBroadcastReceiver = null;
        }
    }

}
