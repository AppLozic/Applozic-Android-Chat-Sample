package com.applozic.mobicomkit.api.conversation;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.text.TextUtils;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by devashish on 15/12/13.
 */
public class ConversationIntentService extends JobIntentService {

    public static final String SYNC = "AL_SYNC";
    public static final String AL_MESSAGE = "AL_MESSAGE";
    private static final String TAG = "ConversationIntent";
    public static final String MESSAGE_METADATA_UPDATE = "MessageMetadataUpdate";
    public static final String MUTED_USER_LIST_SYNC = "MutedUserListSync";
    private static final int PRE_FETCH_MESSAGES_FOR = 6;
    private MobiComMessageService mobiComMessageService;


    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static public void enqueueWork(Context context, Intent work) {
        enqueueWork(context, ConversationIntentService.class, JOB_ID, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mobiComMessageService = new MobiComMessageService(this, MessageIntentService.class);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent == null) {
            return;
        }
        boolean sync = intent.getBooleanExtra(SYNC, false);
        Utils.printLog(ConversationIntentService.this, TAG, "Syncing messages service started: " + sync);
        boolean metadataSync = intent.getBooleanExtra(MESSAGE_METADATA_UPDATE, false);
        boolean mutedUserListSync = intent.getBooleanExtra(MUTED_USER_LIST_SYNC, false);

        if (mutedUserListSync) {
            Utils.printLog(ConversationIntentService.this, TAG, "Muted user list sync started..");
            new Thread(new MutedUserListSync()).start();
            return;
        }

        if (metadataSync) {
            Utils.printLog(ConversationIntentService.this, TAG, "Syncing messages service started for metadata update");
            mobiComMessageService.syncMessageForMetadataUpdate();
            return;
        }

        Message message = (Message) intent.getSerializableExtra(AL_MESSAGE);

        if (message != null) {
            mobiComMessageService.processInstantMessage(message);
        } else {
            if (sync) {
                mobiComMessageService.syncMessages();
            } else {
                Thread thread = new Thread(new ConversationSync());
                thread.start();
            }
        }
    }

    private class ConversationSync implements Runnable {

        public ConversationSync() {
        }

        @Override
        public void run() {
            try {
                MobiComConversationService mobiComConversationService = new MobiComConversationService(ConversationIntentService.this);
                List<Message> messages = mobiComConversationService.getLatestMessagesGroupByPeople();
                UserService.getInstance(ConversationIntentService.this).processSyncUserBlock();

                if (ApplozicClient.getInstance(ConversationIntentService.this).isDeviceContactSync()) {
                    Set<String> contactNoSet = new HashSet<String>();
                    List<Contact> contacts = new AppContactService(ConversationIntentService.this).getContacts(Contact.ContactType.DEVICE);
                    for (Contact contact : contacts) {
                        if (!TextUtils.isEmpty(contact.getFormattedContactNumber())) {
                            contactNoSet.add(contact.getFormattedContactNumber());
                        }
                    }

                    if (!contactNoSet.isEmpty()) {
                        UserService userService = UserService.getInstance(getApplicationContext());
                        userService.processUserDetailsByContactNos(contactNoSet);
                    }
                    MobiComUserPreference.getInstance(ConversationIntentService.this).setDeviceContactSyncTime(new Date().getTime());
                }

                for (Message message : messages.subList(0, Math.min(PRE_FETCH_MESSAGES_FOR, messages.size()))) {
                    Contact contact = null;
                    Channel channel = null;

                    if (message.getGroupId() != null) {
                        channel = new Channel(message.getGroupId());
                    } else {
                        contact = new Contact(message.getContactIds());
                    }

                    mobiComConversationService.getMessages(1L, null, contact, channel, null, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MutedUserListSync implements Runnable {
        @Override
        public void run() {
            try {
                UserService.getInstance(ConversationIntentService.this).getMutedUserList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
