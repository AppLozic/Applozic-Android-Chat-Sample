package com.applozic.audiovideo.authentication;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicommons.commons.core.utils.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by devashish on 30/07/16.
 */
public class TokenGenClientService extends MobiComKitClientService {

    private static final String TAG = "TokenGenClientService";

    public TokenGenClientService(Context context) {
        super.context = context;
    }

    //Todo: Take url based on env.
    public String getUrl() {
        return getBaseUrl() + "/twilio/token";
    }

    public String getGeneratedToken() {

        try {

            MobiComUserPreference pref = MobiComUserPreference.getInstance(context);
            String identity = pref.getUserId();
            String device = pref.getDeviceKeyString();
            String data = "identity=" + identity + "&device=" + device;
            Log.i(TAG, getUrl());

            String response = postData(getUrl(), "application/x-www-form-urlencoded", null, data);
            Log.i(TAG, response);
            return response;

        } catch (Exception e) {
            Log.i(TAG, " Exception ::", e);
            return null;
        }
    }

    public String postData(String urlString, String contentType, String accept, String data) throws Exception {
        Utils.printLog(context,TAG, "Calling url: " + urlString);
        HttpURLConnection connection;
        URL url;
        try {

            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            if (!TextUtils.isEmpty(contentType)) {
                connection.setRequestProperty("Content-Type", contentType);
            }
            if (!TextUtils.isEmpty(accept)) {
                connection.setRequestProperty("Accept", accept);
            }
            connection.setRequestProperty(HttpRequestUtils.APPLICATION_KEY_HEADER, MobiComKitClientService.getApplicationKey(context));
            connection.setRequestProperty("Source", "1");
            connection.setRequestProperty(HttpRequestUtils.USERID_HEADER, HttpRequestUtils.USERID_HEADER_VALUE);
            connection.setRequestProperty(HttpRequestUtils.DEVICE_KEY_HEADER, MobiComUserPreference.getInstance(context).getDeviceKeyString());
            Short authenticationType = Short.valueOf(MobiComUserPreference.getInstance(context).getAuthenticationType());
            if (User.AuthenticationType.APPLOZIC.getValue() == authenticationType) {
                connection.setRequestProperty(HttpRequestUtils.ACCESS_TOKEN, MobiComUserPreference.getInstance(context).getPassword());
            }

            if (MobiComKitClientService.getAppModuleName(context) != null) {
                connection.setRequestProperty(HttpRequestUtils.APP_MODULE_NAME_KEY_HEADER, MobiComKitClientService.getAppModuleName(context));
            }

            MobiComUserPreference userPreferences = MobiComUserPreference.getInstance(context);
            if (userPreferences.isRegistered()) {
                String userCredentials = getCredentials().getUserName() + ":" + String.valueOf(getCredentials().getPassword());
                String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
                connection.setRequestProperty("Authorization", basicAuth);
            }
            connection.connect();

            if (connection == null) {
                return null;
            }
            if (data != null) {
                byte[] dataBytes = data.getBytes("UTF-8");
                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                os.write(dataBytes);
                os.flush();
                os.close();
            }
            BufferedReader br = null;
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            }
            StringBuilder sb = new StringBuilder();
            try {
                String line;
                if (br != null) {
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Utils.printLog(context,TAG, "Response : " + sb.toString());
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.printLog(context,TAG, "Http call failed");
        return null;
    }


}
