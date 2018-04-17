package com.applozic.mobicomkit.api.conversation;

import android.content.Context;
import android.os.AsyncTask;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.exception.ApplozicException;
import com.applozic.mobicomkit.listners.MessageListHandler;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reytum on 27/11/17.
 */

public class MessageListTask extends AsyncTask<Void, Void, List<Message>> {

    private WeakReference<Context> context;
    private Contact contact;
    private Channel channel;
    private Long startTime;
    private Long endTime;
    private MessageListHandler handler;
    private boolean isForMessageList;
    private ApplozicException exception;

    public MessageListTask(Context context, Contact contact, Channel channel, Long startTime, Long endTime, MessageListHandler handler, boolean isForMessageList) {
        this.context = new WeakReference<Context>(context);
        this.contact = contact;
        this.channel = channel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.handler = handler;
        this.isForMessageList = isForMessageList;
    }

    @Override
    protected List<Message> doInBackground(Void... voids) {
        List<Message> messageList = null;
        try {
            if (isForMessageList) {
                messageList = new MobiComConversationService(context.get()).getLatestMessagesGroupByPeople(startTime, null);
            } else {
                messageList = new MobiComConversationService(context.get()).getMessages(startTime, endTime, contact, channel, null);
            }
        } catch (Exception e) {
            exception = new ApplozicException(e.getMessage());
        }
        return messageList;
    }

    @Override
    protected void onPostExecute(List<Message> messageList) {
        super.onPostExecute(messageList);
        if (messageList == null && exception == null) {
            exception = new ApplozicException("Some internal error occurred");
        }
        List<String> recList = new ArrayList<String>();
        List<Message> messages = new ArrayList<Message>();

        if (isForMessageList) {
            if (messageList != null) {
                for (Message message : messageList) {
                    if ((message.getGroupId() == null || message.getGroupId() == 0) && !recList.contains(message.getContactIds())) {
                        recList.add(message.getContactIds());
                        messages.add(message);
                    } else if (message.getGroupId() != null && !recList.contains("group" + message.getGroupId())) {
                        recList.add("group" + message.getGroupId());
                        messages.add(message);
                    }
                }
                if (!messageList.isEmpty()) {
                    MobiComUserPreference.getInstance(context.get()).setStartTimeForPagination(messageList.get(messageList.size() - 1).getCreatedAtTime());
                }
            }
        }
        if (handler != null) {
            if (isForMessageList) {
                handler.onResult(messages, exception);
            } else if (messageList != null) {
                handler.onResult(messageList, exception);
            }
        }
    }
}