package com.applozic.mobicomkit.broadcast;

/**
 * Created by Rahul-PC on 21-08-2017.
 */

import android.content.Context;
import android.os.Bundle;

import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.conversation.Message;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class PushNotificationDispatcher {


    public static void scheduleJob(Context context, Message message) {

        try {
            Bundle bundle = new Bundle();
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
            bundle.putString(MobiComKitConstants.AL_MESSAGE_KEY, message.getKeyString());
            Class pushNotificationJobServiceClass = Class.forName("com.applozic.mobicomkit.uiwidgets.notification.PushNotificationJobService");
            Job myJob = dispatcher.newJobBuilder()
                    .setService(pushNotificationJobServiceClass) // the JobService that will be called
                    .setTag(MobiComKitConstants.PUSH_NOTIFICATION_DISPATCHER)  // uniquely identifies the job
                    .setRecurring(false)
                    .setTrigger(Trigger.executionWindow(0, 0))
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setExtras(bundle)
                    .build();
            dispatcher.mustSchedule(myJob);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}