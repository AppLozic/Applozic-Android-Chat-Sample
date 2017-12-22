package com.applozic.mobicomkit.listners;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.exception.ApplozicException;

/**
 * Created by reytum on 27/11/17.
 */

public interface MediaUploadProgressHandler {
    void onUploadStarted(ApplozicException e);

    void onProgressUpdate(int percentage, ApplozicException e);

    void onCancelled(ApplozicException e);

    void onCompleted(ApplozicException e);

    void onSent(Message message);
}
