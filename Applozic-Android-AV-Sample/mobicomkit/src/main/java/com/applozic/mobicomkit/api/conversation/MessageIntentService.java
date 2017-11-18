package com.applozic.mobicomkit.api.conversation;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.conversation.schedule.ScheduleMessageService;
import com.applozic.mobicommons.json.GsonUtils;

/**
 * Created by devashish on 15/12/13.
 */
public class MessageIntentService extends JobIntentService {

    private static final String TAG = "MessageIntentService";
    private MessageClientService messageClientService;

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1111;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static public void enqueueWork(Context context, Intent work) {
        enqueueWork(context, MessageIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        messageClientService = new MessageClientService(MessageIntentService.this);
        final Message message = (Message) GsonUtils.getObjectFromJson(intent.getStringExtra(MobiComKitConstants.MESSAGE_JSON_INTENT), Message.class);
        Thread thread = new Thread(new MessageSender(message));
        thread.start();
    }


    private class MessageSender implements Runnable {
        private Message message;

        public MessageSender(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                messageClientService.sendMessageToServer(message, ScheduleMessageService.class);
                messageClientService.syncPendingMessages(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
