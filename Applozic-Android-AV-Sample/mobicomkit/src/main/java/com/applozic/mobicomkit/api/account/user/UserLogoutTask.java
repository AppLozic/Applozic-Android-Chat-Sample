package com.applozic.mobicomkit.api.account.user;

import android.content.Context;
import android.os.AsyncTask;

import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicomkit.listners.AlLogoutHandler;

import java.lang.ref.WeakReference;

public class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {

    private TaskListener taskListener;
    private final WeakReference<Context> context;
    UserClientService userClientService;
    private Exception mException;
    private AlLogoutHandler logoutHandler;

    public UserLogoutTask(TaskListener listener, Context context) {
        this.taskListener = listener;
        this.context = new WeakReference<Context>(context);
        userClientService = new UserClientService(context);
    }

    public UserLogoutTask(AlLogoutHandler listener, Context context) {
        this.logoutHandler = listener;
        this.context = new WeakReference<Context>(context);
        userClientService = new UserClientService(context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ApiResponse apiResponse = null;
        try {
            apiResponse = userClientService.logout();
            return apiResponse != null && apiResponse.isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        // And if it is we call the callback function on it.
        if (result && this.taskListener != null) {
            this.taskListener.onSuccess(context.get());

        } else if (mException != null && this.taskListener != null) {
            this.taskListener.onFailure(mException);
        } else if (result && this.logoutHandler != null) {
            this.logoutHandler.onSuccess(context.get());
        } else if (mException != null && this.logoutHandler != null) {
            this.logoutHandler.onFailure(mException);
        }
    }

    public interface TaskListener {
        void onSuccess(Context context);

        void onFailure(Exception exception);
    }
}