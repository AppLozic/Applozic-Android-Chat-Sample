package com.applozic.mobicomkit.api.conversation;

import android.content.Context;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.attachment.AttachmentManager;
import com.applozic.mobicomkit.api.attachment.AttachmentTask;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.exception.ApplozicException;
import com.applozic.mobicomkit.listners.MediaDownloadProgressHandler;
import com.applozic.mobicomkit.listners.MessageListHandler;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.Iterator;
import java.util.List;

/**
 * Created by ashish on 05/01/18.
 */

public class ApplozicConversation {

    public static void getLatestMessageList(Context context, boolean isScroll, MessageListHandler handler) {
        if (!isScroll) {
            new MessageListTask(context, null, null, null, null, handler, true).execute();
        } else {
            new MessageListTask(context, null, null, MobiComUserPreference.getInstance(context).getStartTimeForPagination(), null, handler, true).execute();
        }
    }

    public static void getLatestMessageList(Context context, Long startTime, MessageListHandler handler) {
        new MessageListTask(context, null, null, startTime, null, handler, true).execute();
    }

    public static void getMessageListForContact(Context context, Contact contact, Long endTime, MessageListHandler handler) {
        new MessageListTask(context, contact, null, null, endTime, handler, false).execute();
    }

    public static void getMessageListForChannel(Context context, Channel channel, Long endTime, MessageListHandler handler) {
        new MessageListTask(context, null, channel, null, endTime, handler, false).execute();
    }

    public static void getMessageListForContact(Context context, String userId, Long endTime, MessageListHandler handler) {
        new MessageListTask(context, new AppContactService(context).getContactById(userId), null, null, endTime, handler, false).execute();
    }

    public static void getMessageListForChannel(Context context, Integer channelKey, Long endTime, MessageListHandler handler) {
        new MessageListTask(context, null, ChannelService.getInstance(context).getChannel(channelKey), null, endTime, handler, false).execute();
    }

    public static void downloadMessage(Context context, Message message, MediaDownloadProgressHandler handler) {
        ApplozicException e;
        if (message == null || handler == null) {
            return;
        }
        if (!message.hasAttachment()) {
            e = new ApplozicException("Message does not have Attachment");
            handler.onProgressUpdate(0, e);
            handler.onCompleted(null, e);
        } else if (message.isAttachmentDownloaded()) {
            e = new ApplozicException("Attachment for the message already downloaded");
            handler.onProgressUpdate(0, e);
            handler.onCompleted(null, e);
        } else {
            AttachmentTask mDownloadThread = null;
            if (!AttachmentManager.isAttachmentInProgress(message.getKeyString())) {
                // Starts downloading this View, using the current cache setting
                mDownloadThread = AttachmentManager.startDownload(null, true, message, handler, context);
                // After successfully downloading the image, this marks that it's available.
            }
            if (mDownloadThread == null) {
                mDownloadThread = AttachmentManager.getBGThreadForAttachment(message.getKeyString());
                if (mDownloadThread != null) {
                    mDownloadThread.setAttachment(message, handler, context);
                }
            }
        }
    }

    public static synchronized void addLatestMessage(Message message, List<Message> messageList) {
        Iterator<Message> iterator = messageList.iterator();
        boolean shouldAdd = false;

        while (iterator.hasNext()) {
            Message currentMessage = iterator.next();

            if ((message.getGroupId() != null && currentMessage.getGroupId() != null && message.getGroupId().equals(currentMessage.getGroupId())) ||
                    (message.getContactIds() != null && currentMessage.getContactIds() != null && message.getContactIds().equals(currentMessage.getContactIds()))) {

            } else {
                currentMessage = null;
            }

            if (currentMessage != null) {
                if (message.getCreatedAtTime() >= currentMessage.getCreatedAtTime()) {
                    iterator.remove();
                } else {
                    return;
                }
            }

            shouldAdd = true;
        }

        if(shouldAdd){
            messageList.add(0, message);
        }
    }

}
