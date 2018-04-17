package com.applozic.mobicomkit.api.account.register;

import android.content.Context;
import android.util.Log;

import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.feed.SyncApiResponse;
import com.applozic.mobicommons.json.GsonUtils;

/**
 * Created by devashish on 24/12/14.
 */
public class SyncClientService extends MobiComKitClientService {

    private static final String TAG = "SyncClientService";

    public static final String SYNC_URL = "/rest/ws/sync/get";

    private HttpRequestUtils httpRequestUtils;

    public enum SyncType {
        MESSAGE(0), GROUP(1), CONVERSATION(2), CONTACT(3);

        private Integer value;

        SyncType(Integer value) {
            this.value = value;
        }

        public Short getValue() {
            return value.shortValue();
        }
    }

    public SyncClientService(Context context) {
        super(context);
        this.httpRequestUtils = new HttpRequestUtils(context);
    }

    public String getSyncUrl() {
        return getBaseUrl() + SYNC_URL;
    }

    public SyncApiResponse getSyncCall(Long syncTime, SyncType syncType) {
        try {
            String response = httpRequestUtils.getResponse(getSyncUrl() + "?syncTime=" + syncTime + "&syncType=" + syncType.getValue() , "application/json", "application/json");
            if(response != null) {
                Log.i(TAG,"Sync call response: "+ response);
                return (SyncApiResponse) GsonUtils.getObjectFromJson(response, SyncApiResponse.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}