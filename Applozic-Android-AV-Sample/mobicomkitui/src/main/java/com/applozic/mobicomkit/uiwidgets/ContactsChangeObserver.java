package com.applozic.mobicomkit.uiwidgets;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import com.applozic.mobicomkit.uiwidgets.people.contact.DeviceContactSyncService;

/**
 * Created by ashish on 23/03/18.
 */

public class ContactsChangeObserver extends ContentObserver {

    private static final String TAG = "ContactsChangeObserver";
    Context context;
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public ContactsChangeObserver(Handler handler,Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.i(TAG, "ContentObserver is called for contacts change");
        Intent intent = new Intent(context.getApplicationContext(), DeviceContactSyncService.class);
        intent.putExtra(DeviceContactSyncService.PROCESS_MODIFIED_DEVICE_CONTACTS, true);
        DeviceContactSyncService.enqueueWork(context.getApplicationContext(), intent);
    }

}
