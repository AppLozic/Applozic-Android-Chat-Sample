package com.applozic.mobicomkit.listners;

import com.applozic.mobicomkit.api.conversation.Message;

/**
 * Created by reytum on 5/12/17.
 */

public interface ApplozicUIListener {
    void onNewMessage(Message message);

    void onChannelNameUpdated();

    void onLoadMore(boolean loadMore);

    void onMessageSync(Message message, String key);

    void onMessageDeleted(String messageKey, String userId);

    void onMessageDelivered(Message message, String userId);

    void onAllMessagesDelivered(String userId);

    void onAllMessagesRead(String userId);

    void onConversationDeleted(String userId, Integer channelKey, String response);

    void onUpdateTypingStatus(String userId, String isTyping);

    void onUpdateLastSeen(String userId);

    void onMqttDisconnected();

    void onChannelSync();

    void onChannelTitleUpdated();

    void onConversationRead(String userId, boolean isGroup);

    void onUserDetailUpdated(String userId);
}
