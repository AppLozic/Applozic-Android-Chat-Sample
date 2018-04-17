package com.applozic.mobicomkit.api.account.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;

import java.util.Set;


public class MobiComUserPreference {

    private static final String USER_ID = "userId";
    public static MobiComUserPreference userpref;
    //Constants for preferneces ..
    private static String device_registration_id = "device_registration_id";
    private static String device_key_string = "device_key_string";
    private static String last_outbox_sync_time = "last_outbox_sync_time";
    private static String delivery_report_pref_key = "delivery_report_pref_key";
    private static String last_inbox_sync_time = "last_inbox_sync_time";
    private static String last_message_stat_sync_time = "last_message_stat_sync_time";
    private static String sent_sms_sync_pref_key = "sent_sms_sync_pref_key";
    private static String email = "email";
    private static String email_verified = "email_verified";
    private static String user_key_string = "user_key_string";
    private static String stop_service = "stop_service";
    private static String patch_available = "patch_available";
    private static String webhook_enable_key = "webhook_enable_key";
    private static String group_sms_freq_key = "group_sms_freq_key";
    private static String update_push_registration = "update_push_registration";
    private static String verify_contact_number = "verify_contact_number";
    private static String received_sms_sync_pref_key = "received_sms_sync_pref_key";
    private static String phone_number_key = "phone_number_key";
    private static String call_history_display_within_messages_pref_key = "call_history_display_within_messages_pref_key";
    private static String mobitexter_contact_sync_key = "mobitexter_contact_sync_key";
    private static String last_sms_sync_time = "last_sms_sync_time";
    private static String new_message_flag = "new_message_flag";
    private static String base_url = "base_url";
    private static String display_name = "display_name";
    private static String logged_in = "logged_in";
    private static String lastSeenAtSyncTime = "lastSeenAtSyncTime";
    private static String channelSyncTime = "channelSyncTime";
    private static String device_time_offset_from_UTC = "device_time_offset_from_UTC";
    private static String image_compression_enabled = "image_compression_enabled";
    private static String userBlockSyncTime = "user_block_Sync_Time";
    private static String max_compressed_image_size = "max_compressed_image_size";
    private static String image_link = "image_link";
    private static String registered_users_last_fetch_time = "registered_users_last_fetch_time";
    private static String password = "password";
    private static String authenticationType = "authenticationType";
    private static String mqtt_broker_url = "mqtt_broker_url";
    private static String contact_list_server_call = "contact_list_server_call";
    private static String pricing_package = "pricing_package";
    private static String delete_channel = "delete_channel";
    private static String encryption_Key = "encryption_Key";
    private static String enable_encryption = "enable_encryption";
    private static String enable_auto_download_on_wifi = "enable_auto_download_on_wifi";
    private static String enable_auto_download_on_cellular = "enable_auto_download_on_cellular";
    private static String video_call_token = "video_call_token";
    private static String user_type_id = "user_type_id";
    private static String application_info_call_done = "application_info_call_done";
    private static String notification_sound_fileName = "notificationSoundFileName";
    private static String CONTACTS_GROUP_ID = "CONTACTS_GROUP_ID";
    private static String CONTACT_GROUP_ID_LISTS = "contactGroupIdLists";
    private static String IS_CONTACT_GROUP_NAME_LIST = "isContactGroupNameList";
    private static String last_sync_time_for_metadata_update = "lastSyncTimeForMetadataUpdate";
    private static String START_TIME_FOR_MESSAGE_LIST_SCROLL = "startTimeForMessageListScroll";
    private static String USER_ROLE_TYPE = "userRoleType";
    private static String sync_contacts = "sync_contacts";
    private static String contact_sync_time = "contact_sync_time";
    private static String device_contact_sync_time = "device_contact_sync_time";
    private static String PARENT_GROUP_KEY = "PARENT_GROUP_KEY";

    private static SharedPreferences sharedPreferences;
    private Context context;
    private String countryCode;


    private MobiComUserPreference(Context context) {
        this.context = context;
        if (!TextUtils.isEmpty(MobiComKitClientService.getApplicationKey(context))) {
            sharedPreferences = context.getSharedPreferences(MobiComKitClientService.getApplicationKey(context), context.MODE_PRIVATE);
            initialize(context);
        }
    }

    public static MobiComUserPreference getInstance(Context context) {
        if (userpref == null) {
            userpref = new MobiComUserPreference(context.getApplicationContext());
        } else {
            if (!TextUtils.isEmpty(MobiComKitClientService.getApplicationKey(context))) {
                sharedPreferences = context.getSharedPreferences(MobiComKitClientService.getApplicationKey(context), context.MODE_PRIVATE);
            }
        }
        return userpref;
    }

    /*
    public void setDeviceRegistrationId(String deviceRegistrationId) {
        sharedPreferences.edit().putString(OsuConstants.DEVICE_REGISTRATION_ID, deviceRegistrationId).commit();
    }

    public String getDeviceRegistrationId() {
        return sharedPreferences.getString(OsuConstants.DEVICE_REGISTRATION_ID, null);
    }*/

    public boolean isRegistered() {
        return !TextUtils.isEmpty(getDeviceKeyString());
    }

    public String getDeviceRegistrationId() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(device_registration_id, null);
        }
        return null;
    }

    public void setDeviceRegistrationId(String deviceRegistrationId) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(device_registration_id, deviceRegistrationId).commit();
        }
    }

    public String getDeviceKeyString() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(device_key_string, null);
        }
        return null;
    }

    public void setDeviceKeyString(String deviceKeyString) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(device_key_string, deviceKeyString).commit();
        }
    }

    public long getLastOutboxSyncTime() {
        return sharedPreferences.getLong(last_outbox_sync_time, 0L);
    }

    public void setLastOutboxSyncTime(long lastOutboxSyncTime) {
        sharedPreferences.edit().putLong(last_outbox_sync_time, lastOutboxSyncTime).commit();
    }

    public boolean isReportEnable() {
        return sharedPreferences.getBoolean(delivery_report_pref_key, false);
    }

    public void setReportEnable(boolean reportEnable) {
        sharedPreferences.edit().putBoolean(delivery_report_pref_key, reportEnable).commit();
    }

    public String getLastSyncTime() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(last_sms_sync_time, "0");
        }
        return null;
    }

    public void setLastSyncTime(String lastSyncTime) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(last_sms_sync_time, lastSyncTime).commit();
        }
    }

    public long getLastInboxSyncTime() {
        return sharedPreferences.getLong(last_inbox_sync_time, 0L);
    }

    public void setLastInboxSyncTime(long lastInboxSyncTime) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putLong(last_inbox_sync_time, lastInboxSyncTime).commit();
        }
    }

    public Long getLastMessageStatSyncTime() {
        return sharedPreferences.getLong(last_message_stat_sync_time, 0);
    }

    public void setLastMessageStatSyncTime(long lastMessageStatSyncTime) {
        sharedPreferences.edit().putLong(last_message_stat_sync_time, lastMessageStatSyncTime).commit();
    }

    public boolean isSentSmsSyncFlag() {
        return sharedPreferences.getBoolean(sent_sms_sync_pref_key, true);
    }

    public void setSentSmsSyncFlag(boolean sentSmsSyncFlag) {
        sharedPreferences.edit().putBoolean(sent_sms_sync_pref_key, sentSmsSyncFlag).commit();
    }

    public String getEmailIdValue() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(email, null);
        }
        return null;
    }

    public void setEmailIdValue(String emailIdValue) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(email, emailIdValue).commit();
        }
    }

    public String getUserId() {
        if (sharedPreferences != null) {
            String userId = sharedPreferences.getString(USER_ID, null);
            if (TextUtils.isEmpty(userId)) {
                return getEmailIdValue();
            }
            return userId;
        }
        return null;
    }

    public void setUserId(String userId) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(USER_ID, userId).commit();
        }
    }

    public boolean isEmailVerified() {
        return sharedPreferences.getBoolean(email_verified, true);
    }

    public void setEmailVerified(boolean emailVerified) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(email_verified, emailVerified).commit();
        }
    }

    public String getSuUserKeyString() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(user_key_string, null);
        }
        return null;
    }

    public void setSuUserKeyString(String suUserKeyString) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(user_key_string, suUserKeyString).commit();
        }
    }

    public boolean isStopServiceFlag() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(stop_service, false);
        }
        return false;
    }

    public void setStopServiceFlag(Boolean stopServiceFlag) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(stop_service, stopServiceFlag).commit();
        }
    }

    public boolean isPatchAvailable() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(patch_available, false);
        }
        return false;
    }

    public void setPatchAvailable(Boolean patchAvailable) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(patch_available, patchAvailable).commit();
        }
    }

    public boolean isWebHookEnable() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(webhook_enable_key, false);
        }
        return false;
    }

    public void setWebHookEnable(boolean enable) {
        sharedPreferences.edit().putBoolean(webhook_enable_key, enable).commit();
    }

    public int getGroupSmsDelayInSec() {
        return sharedPreferences.getInt(group_sms_freq_key, 0);
    }

    public void setDelayGroupSmsDelayTime(int delay) {
        sharedPreferences.edit().
                putInt(group_sms_freq_key, delay).commit();
    }


//    public boolean getNewPatchAvailable() {
//        return newPatchAvailable;
//    }
//
//    public boolean getUpdateRegFlag() {
//        return updateRegFlag;
//    }

    public boolean isUpdateRegFlag() {
        return sharedPreferences.getBoolean(update_push_registration, false);
    }

    public void setUpdateRegFlag(boolean updateRegFlag) {
        sharedPreferences.edit().putBoolean(update_push_registration, updateRegFlag).commit();
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isVerifyContactNumber() {
        return sharedPreferences.getBoolean(verify_contact_number, false);
    }

    public void setVerifyContactNumber(boolean verifyContactNumber) {
        sharedPreferences.edit().putBoolean(verify_contact_number, verifyContactNumber).commit();
    }

    public boolean getReceivedSmsSyncFlag() {
        return sharedPreferences.getBoolean(received_sms_sync_pref_key, true);
    }

    public void setReceivedSmsSyncFlag(boolean receivedSmsSyncFlag) {
        sharedPreferences.edit().putBoolean(received_sms_sync_pref_key, receivedSmsSyncFlag).commit();
    }

    public String getContactNumber() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(phone_number_key, null);
        }
        return null;
    }

    public void setContactNumber(String contactNumber) {
        // contactNumber = ContactNumberUtils.getPhoneNumber(contactNumber, getCountryCode());
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(phone_number_key, contactNumber).commit();
        }
    }

    public boolean isDisplayCallRecordEnable() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(call_history_display_within_messages_pref_key, false);
        }
        return false;
    }

    public void setDisplayCallRecordEnable(boolean enable) {
        sharedPreferences.edit().putBoolean(call_history_display_within_messages_pref_key, enable).commit();
    }

    public boolean getNewMessageFlag() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(new_message_flag, false);
        }
        return false;
    }

    public void setNewMessageFlag(boolean enable) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(new_message_flag, enable).commit();
        }
    }

    public long getDeviceTimeOffset() {
        if (sharedPreferences != null) {
            return sharedPreferences.getLong(device_time_offset_from_UTC, 0L);
        }
        return 0;
    }

    public boolean setDeviceTimeOffset(long diiference) {
        if (sharedPreferences != null) {
            return sharedPreferences.edit().putLong(device_time_offset_from_UTC, diiference).commit();
        }
        return false;
    }

    //Local initialization of few fields)
    public void initialize(Context context) {
      /*  TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = telephonyManager.getSimCountryIso().toUpperCase();
        String contactNumber = telephonyManager.getLine1Number();
        setCountryCode(countryCode);
        if (!TextUtils.isEmpty(contactNumber)) {
            setContactNumber(contactNumber);
        }
        if (getLastMessageStatSyncTime() == null || getLastMessageStatSyncTime() == 0) {
            setLastMessageStatSyncTime(new Date().getTime());
        }*/
    }

    public boolean isMobiTexterContactSyncCompleted() {
        return sharedPreferences.getBoolean(mobitexter_contact_sync_key, false);
    }

    public void setMobiTexterContactSyncCompleted(boolean status) {
        sharedPreferences.edit().
                putBoolean(mobitexter_contact_sync_key, status).commit();
    }

    public String getUrl() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(base_url, null);
        }
        return null;
    }

    public void setUrl(String url) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(base_url, url).commit();
        }
    }

    public String getMqttBrokerUrl() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(mqtt_broker_url, null);
        }
        return null;
    }

    public void setMqttBrokerUrl(String url) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(mqtt_broker_url, url).commit();
        }
    }

    public int getPricingPackage() {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(pricing_package, RegistrationResponse.PricingType.STARTER.getValue());
        }
        return 0;
    }

    public void setPricingPackage(int pricingPackage) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putInt(pricing_package, pricingPackage).commit();
        }
    }

    public String getDisplayName() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(display_name, null);
        }
        return null;
    }

    public void setDisplayName(String displayName) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(display_name, displayName).commit();
        }
    }

    public boolean isLoggedIn() {
        if (sharedPreferences != null) {
            return !TextUtils.isEmpty(getUserId());
        }
        return false;
    }

    public String getLastSeenAtSyncTime() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(lastSeenAtSyncTime, "0");
        }
        return null;
    }

    public void setLastSeenAtSyncTime(String lastSeenAtTime) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(lastSeenAtSyncTime, lastSeenAtTime).commit();
        }
    }

    public String getChannelSyncTime() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(channelSyncTime, "0");
        }
        return null;
    }

    public void setChannelSyncTime(String syncChannelTime) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(channelSyncTime, syncChannelTime).commit();
        }
    }

    public int getCompressedImageSizeInMB() {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(max_compressed_image_size, 10);
        }
        return 0;
    }

    public void setCompressedImageSizeInMB(int maxSize) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putInt(max_compressed_image_size, maxSize).commit();
        }
    }

    public void setLastSyncTimeForMetadataUpdate(String lastSyncTime) {
        sharedPreferences.edit().putString(last_sync_time_for_metadata_update, lastSyncTime).commit();
    }

    public String getLastSyncTimeForMetadataUpdate() {
        return sharedPreferences.getString(last_sync_time_for_metadata_update, null);
    }

    public String getUserBlockSyncTime() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(userBlockSyncTime, "0");
        }
        return null;
    }

    public void setUserBlockSyncTime(String lastUserBlockSyncTime) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(userBlockSyncTime, lastUserBlockSyncTime).commit();
        }
    }

    public long getRegisteredUsersLastFetchTime() {
        if (sharedPreferences != null) {
            return sharedPreferences.getLong(registered_users_last_fetch_time, 0l);
        }
        return 0;
    }

    public void setRegisteredUsersLastFetchTime(long lastFetchTime) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putLong(registered_users_last_fetch_time, lastFetchTime).commit();
        }
    }

    public String getImageLink() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(image_link, null);
        }
        return null;
    }

    public void setImageLink(String imageUrl) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(image_link, imageUrl).commit();
        }
    }

    public String getPassword() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(password, null);
        }
        return null;
    }

    public void setPassword(String val) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(password, val).commit();
        }
    }

    public String getAuthenticationType() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(authenticationType, "0");
        }
        return null;
    }

    public void setAuthenticationType(String val) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(authenticationType, val).commit();
        }
    }

    public void setDeleteChannel(boolean channelDelete) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(delete_channel, channelDelete).commit();
        }
    }

    public boolean isChannelDeleted() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(delete_channel, false);
        }
        return false;
    }

    @Override
    public String toString() {
        return "MobiComUserPreference{" +
                "context=" + context +
                ", countryCode='" + getCountryCode() + '\'' +
                ", deviceKeyString=" + getDeviceKeyString() +
                ", contactNumber=" + getContactNumber() +
                '}';
    }

    public boolean clearAll() {

        if (sharedPreferences != null) {
            return sharedPreferences.edit().clear().commit();
        }
        return false;

        //Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);


    }

    public boolean isImageCompressionEnabled() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(image_compression_enabled, true);
        }
        return false;
    }

    public void setImageCompressionEnabled(boolean imageCompressionEnabled) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(image_compression_enabled, imageCompressionEnabled).commit();
        }
    }

    public boolean getWasContactListServerCallAlreadyDone() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(contact_list_server_call, false);
        }
        return false;
    }

    public void setWasContactListServerCallAlreadyDone(Boolean serverCallAlreadyDone) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(contact_list_server_call, serverCallAlreadyDone).commit();
        }
    }

    public String getEncryptionKey() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(encryption_Key, null);
        }
        return null;
    }

    public void setEncryptionKey(String encryptionKey) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(encryption_Key, encryptionKey).commit();
        }
    }

    public boolean isEncryptionEnabled() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(enable_encryption, false);
        }
        return false;
    }

    public void enableEncryption(boolean enableEncryption) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(enable_encryption, enableEncryption).commit();
        }
    }

    public boolean getAutoDownloadOnWifi() {
        return sharedPreferences.getBoolean(enable_auto_download_on_wifi, false);
    }

    public void setAutoDownloadOnWifi(boolean enable) {
        sharedPreferences.edit().putBoolean(enable_auto_download_on_wifi, enable).commit();
    }

    public boolean getAutoDownloadOnCellular() {
        return sharedPreferences.getBoolean(enable_auto_download_on_cellular, false);
    }

    public void setAutoDownloadOnCellular(boolean enable) {
        sharedPreferences.edit().putBoolean(enable_auto_download_on_cellular, enable).commit();
    }

    public String getVideoCallToken() {
        return sharedPreferences.getString(video_call_token, null);
    }

    public void setVideoCallToken(String token) {
        sharedPreferences.edit().putString(video_call_token, token).commit();

    }

    public String getUserTypeId() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(user_type_id, null);
        }
        return null;
    }

    public void setUserTypeId(String userTypeId) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(user_type_id, userTypeId).commit();
        }
    }


    public boolean getApplicationInfoCall() {
        return sharedPreferences.getBoolean(application_info_call_done, false);
    }

    public void setApplicationInfoCallDone(boolean customerResponse) {
        sharedPreferences.edit().putBoolean(application_info_call_done, customerResponse).commit();
    }

    public String getNotificationSoundFilePath() {
        return sharedPreferences.getString(notification_sound_fileName, null);
    }

    public void setNotificationSoundFilePath(String soundUri) {
        sharedPreferences.edit().putString(notification_sound_fileName, soundUri).commit();
    }

    public String getContactsGroupId() {
        return sharedPreferences.getString(CONTACTS_GROUP_ID, null);
    }

    public void setContactsGroupId(String contactsGroupId) {
        sharedPreferences.edit().putString(CONTACTS_GROUP_ID, contactsGroupId).commit();
    }

    public void setContactGroupIdList(Set<String> contactGroupList) {
        sharedPreferences.edit().putStringSet(CONTACT_GROUP_ID_LISTS, contactGroupList).commit();
    }

    public Set<String> getContactGroupIdList() {
        return sharedPreferences.getStringSet(CONTACT_GROUP_ID_LISTS, null);
    }

    public boolean isContactGroupNameList() {
        return sharedPreferences.getBoolean(IS_CONTACT_GROUP_NAME_LIST, false);
    }

    public void setIsContactGroupNameList(boolean isContactGroupNameList) {
        sharedPreferences.edit().putBoolean(IS_CONTACT_GROUP_NAME_LIST, isContactGroupNameList).commit();
    }

    public void setStartTimeForPagination(Long startTime) {
        sharedPreferences.edit().putLong(START_TIME_FOR_MESSAGE_LIST_SCROLL, startTime).commit();
    }

    public Long getStartTimeForPagination() {
        return sharedPreferences.getLong(START_TIME_FOR_MESSAGE_LIST_SCROLL, 0);
    }

    public void setUserRoleType(Short roleType) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putInt(USER_ROLE_TYPE, roleType).commit();
        }
    }

    public Short getUserRoleType() {
        if (sharedPreferences != null) {
            return Short.valueOf((short) sharedPreferences.getInt(USER_ROLE_TYPE, 0));
        }
        return 0;
    }

    public boolean isSyncRequired() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(sync_contacts, false);
        }
        return false;
    }


    public void setSyncContacts(boolean syncConatcts) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(sync_contacts, syncConatcts).commit();
        }
    }


    public long getDeviceContactSyncTime() {
        if (sharedPreferences != null) {
            return sharedPreferences.getLong(device_contact_sync_time, 0);
        }
        return 0;
    }


    public void setDeviceContactSyncTime(long contactSyncTime) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putLong(device_contact_sync_time, contactSyncTime).commit();
        }
    }


    public long getContactSyncTime() {
        if (sharedPreferences != null) {
            return sharedPreferences.getLong(contact_sync_time, 0);
        }
        return 0;
    }


    public void setContactSyncTime(long contactSyncTime) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putLong(contact_sync_time, contactSyncTime).commit();
        }
    }

    public Integer getParentGroupKey() {
        return sharedPreferences.getInt(PARENT_GROUP_KEY, 0);
    }

    public void setParentGroupKey(Integer parentGroupKey) {
        sharedPreferences.edit().putInt(PARENT_GROUP_KEY, parentGroupKey).commit();
    }
}
