package com.applozic.mobicomkit.api.people;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.conversation.MessageClientService;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.SyncCallService;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

/**
 * Created by devashish on 15/12/13.
 */
public class UserIntentService extends JobIntentService {

    private static final String TAG = "UserIntentService";
    public static final String USER_ID = "userId";
    public static final String USER_LAST_SEEN_AT_STATUS = "USER_LAST_SEEN_AT_STATUS";
    public static final String CONTACT = "contact";
    public static final String CHANNEL = "channel";
    public static final String UNREAD_COUNT = "UNREAD_COUNT";
    public static final String SINGLE_MESSAGE_READ = "SINGLE_MESSAGE_READ";
    MessageClientService messageClientService;
    MobiComConversationService mobiComConversationService;

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1100;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static public void enqueueWork(Context context, Intent work) {
        enqueueWork(context, UserIntentService.class, JOB_ID, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        messageClientService = new MessageClientService(getApplicationContext());
        mobiComConversationService = new MobiComConversationService(getApplicationContext());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Integer unreadCount = intent.getIntExtra(UNREAD_COUNT, 0);
        boolean singleMessageRead = intent.getBooleanExtra(SINGLE_MESSAGE_READ, false);
        Contact contact = (Contact) intent.getSerializableExtra(CONTACT);
        Channel channel = (Channel) intent.getSerializableExtra(CHANNEL);
        if (unreadCount != 0 || singleMessageRead) {
            messageClientService.updateReadStatus(contact, channel);
        } else {
            String userId = intent.getStringExtra(USER_ID);
            if (!TextUtils.isEmpty(userId)) {
                SyncCallService.getInstance(UserIntentService.this).processUserStatus(userId);
            } else if (intent.getBooleanExtra(USER_LAST_SEEN_AT_STATUS, false)) {
                mobiComConversationService.processLastSeenAtStatus();
            }
        }

    }

}
