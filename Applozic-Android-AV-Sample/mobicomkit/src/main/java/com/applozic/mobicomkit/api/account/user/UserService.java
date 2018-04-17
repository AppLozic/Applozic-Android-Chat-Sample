package com.applozic.mobicomkit.api.account.user;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.account.register.SyncClientService;
import com.applozic.mobicomkit.api.notification.MuteUserResponse;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.contact.BaseContactService;
import com.applozic.mobicomkit.contact.database.ContactDatabase;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicomkit.feed.RegisteredUsersApiResponse;
import com.applozic.mobicomkit.feed.SyncApiResponse;
import com.applozic.mobicomkit.feed.SyncBlockUserApiResponse;
import com.applozic.mobicomkit.feed.SyncPxy;
import com.applozic.mobicomkit.sync.SyncUserBlockFeed;
import com.applozic.mobicomkit.sync.SyncUserBlockListFeed;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.contact.Contact;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sunil on 17/3/16.
 */
public class UserService {

    private static final String TAG = "UserService";
    private static UserService userService;
    Context context;
    UserClientService userClientService;
    BaseContactService baseContactService;
    private MobiComUserPreference userPreference;
    SyncClientService syncClientService;

    private UserService(Context context) {
        this.context = context;
        userClientService = new UserClientService(context);
        userPreference = MobiComUserPreference.getInstance(context);
        baseContactService = new AppContactService(context);
        this.syncClientService = new SyncClientService(context);
    }

    public static UserService getInstance(Context context) {
        if (userService == null) {
            userService = new UserService(context.getApplicationContext());
        }
        return userService;
    }

    public synchronized void processSyncUserBlock() {
        try {
            SyncBlockUserApiResponse apiResponse = userClientService.getSyncUserBlockList(userPreference.getUserBlockSyncTime());
            if (apiResponse != null && SyncBlockUserApiResponse.SUCCESS.equals(apiResponse.getStatus())) {
                SyncUserBlockListFeed syncUserBlockListFeed = apiResponse.getResponse();
                if (syncUserBlockListFeed != null) {
                    List<SyncUserBlockFeed> blockedToUserList = syncUserBlockListFeed.getBlockedToUserList();
                    List<SyncUserBlockFeed> blockedByUserList = syncUserBlockListFeed.getBlockedByUserList();
                    if (blockedToUserList != null && blockedToUserList.size() > 0) {
                        for (SyncUserBlockFeed syncUserBlockedFeed : blockedToUserList) {
                            Contact contact = new Contact();
                            if (syncUserBlockedFeed.getUserBlocked() != null && !TextUtils.isEmpty(syncUserBlockedFeed.getBlockedTo())) {
                                if (baseContactService.isContactExists(syncUserBlockedFeed.getBlockedTo())) {
                                    baseContactService.updateUserBlocked(syncUserBlockedFeed.getBlockedTo(), syncUserBlockedFeed.getUserBlocked());
                                } else {
                                    contact.setBlocked(syncUserBlockedFeed.getUserBlocked());
                                    if (ApplozicClient.getInstance(context).isDeviceContactSync()) {
                                        contact.setDeviceContactType(Contact.ContactType.APPLOZIC.getValue());
                                    }
                                    contact.setUserId(syncUserBlockedFeed.getBlockedTo());
                                    baseContactService.upsert(contact);
                                    baseContactService.updateUserBlocked(syncUserBlockedFeed.getBlockedTo(), syncUserBlockedFeed.getUserBlocked());
                                }
                            }
                        }
                    }
                    if (blockedByUserList != null && blockedByUserList.size() > 0) {
                        for (SyncUserBlockFeed syncUserBlockByFeed : blockedByUserList) {
                            Contact contact = new Contact();
                            if (syncUserBlockByFeed.getUserBlocked() != null && !TextUtils.isEmpty(syncUserBlockByFeed.getBlockedBy())) {
                                if (baseContactService.isContactExists(syncUserBlockByFeed.getBlockedBy())) {
                                    baseContactService.updateUserBlockedBy(syncUserBlockByFeed.getBlockedBy(), syncUserBlockByFeed.getUserBlocked());
                                } else {
                                    contact.setBlockedBy(syncUserBlockByFeed.getUserBlocked());
                                    if (ApplozicClient.getInstance(context).isDeviceContactSync()) {
                                        contact.setDeviceContactType(Contact.ContactType.APPLOZIC.getValue());
                                    }
                                    contact.setUserId(syncUserBlockByFeed.getBlockedBy());
                                    baseContactService.upsert(contact);
                                    baseContactService.updateUserBlockedBy(syncUserBlockByFeed.getBlockedBy(), syncUserBlockByFeed.getUserBlocked());
                                }
                            }
                        }
                    }
                }
                userPreference.setUserBlockSyncTime(apiResponse.getGeneratedAt());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ApiResponse processUserBlock(String userId, boolean block) {
        ApiResponse apiResponse = userClientService.userBlock(userId, block);
        if (apiResponse != null && apiResponse.isSuccess()) {
            baseContactService.updateUserBlocked(userId, block);
            return apiResponse;
        }
        return null;
    }

    public synchronized void processUserDetail(Set<UserDetail> userDetails) {
        if (userDetails != null && userDetails.size() > 0) {
            for (UserDetail userDetail : userDetails) {
                processUser(userDetail);
            }
        }
    }

    public synchronized void processUserDetailsByContactNos(Set<String> contactNumbers) {
        userClientService.postUserDetailsByContactNos(contactNumbers);
    }

    public synchronized void processUserDetails(String userId) {
        Set<String> userIds = new HashSet<String>();
        userIds.add(userId);
        processUserDetails(userIds);
    }

    public synchronized void processUserDetails(Set<String> userIds) {
        String response = userClientService.getUserDetails(userIds);
        if (!TextUtils.isEmpty(response)) {
            UserDetail[] userDetails = (UserDetail[]) GsonUtils.getObjectFromJson(response, UserDetail[].class);
            for (UserDetail userDetail : userDetails) {
                processUser(userDetail);
            }
        }
    }

    public synchronized void processUser(UserDetail userDetail) {
        processUser(userDetail, Contact.ContactType.APPLOZIC);
    }


    public synchronized void processUser(UserDetail userDetail, Contact.ContactType contactType) {
        Contact contact = new Contact();
        contact.setUserId(userDetail.getUserId());
        contact.setContactNumber(userDetail.getPhoneNumber());
        contact.setConnected(userDetail.isConnected());
        contact.setStatus(userDetail.getStatusMessage());
        contact.setFullName(userDetail.getDisplayName());
        contact.setLastSeenAt(userDetail.getLastSeenAtTime());
        contact.setUserTypeId(userDetail.getUserTypeId());
        contact.setUnreadCount(0);
        contact.setLastMessageAtTime(userDetail.getLastMessageAtTime());
        contact.setMetadata(userDetail.getMetadata());
        contact.setRoleType(userDetail.getRoleType());
        if (!TextUtils.isEmpty(userDetail.getImageLink())) {
            contact.setImageURL(userDetail.getImageLink());
        }
        if (ApplozicClient.getInstance(context).isDeviceContactSync()) {
            contact.setDeviceContactType(contactType.getValue());
        } else {
            contact.setContactType(contactType.getValue());
        }
        baseContactService.upsert(contact);
    }

    public synchronized void processMuteUserResponse(MuteUserResponse response) {
        Contact contact = new Contact();
        contact.setUserId(response.getUserId());
        BroadcastService.sendMuteUserBroadcast(context, BroadcastService.INTENT_ACTIONS.MUTE_USER_CHAT.toString(), true, response.getUserId());
        if (!TextUtils.isEmpty(response.getImageLink())) {
            contact.setImageURL(response.getImageLink());
        }
        contact.setUnreadCount(response.getUnreadCount());
        if (response.getNotificationAfterTime() != null && response.getNotificationAfterTime() != 0) {
            contact.setNotificationAfterTime(response.getNotificationAfterTime());
        }
        contact.setConnected(response.isConnected());
        baseContactService.upsert(contact);
    }

    public synchronized String[] getOnlineUsers(int numberOfUser) {
        try {
            Map<String, String> userMapList = userClientService.getOnlineUserList(numberOfUser);
            if (userMapList != null && userMapList.size() > 0) {
                String[] userIdArray = new String[userMapList.size()];
                Set<String> userIds = new HashSet<String>();
                int i = 0;
                for (Map.Entry<String, String> keyValue : userMapList.entrySet()) {
                    Contact contact = new Contact();
                    contact.setUserId(keyValue.getKey());
                    contact.setConnected(keyValue.getValue().contains("true"));
                    userIdArray[i] = keyValue.getKey();
                    userIds.add(keyValue.getKey());
                    baseContactService.upsert(contact);
                    i++;
                }
                processUserDetails(userIds);
                return userIdArray;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized RegisteredUsersApiResponse getRegisteredUsersList(Long startTime, int pageSize) {
        String response = userClientService.getRegisteredUsers(startTime, pageSize);
        RegisteredUsersApiResponse apiResponse = null;
        if (!TextUtils.isEmpty(response) && !MobiComKitConstants.ERROR.equals(response)) {
            apiResponse = (RegisteredUsersApiResponse) GsonUtils.getObjectFromJson(response, RegisteredUsersApiResponse.class);
            if (apiResponse != null) {
                processUserDetail(apiResponse.getUsers());
                userPreference.setRegisteredUsersLastFetchTime(apiResponse.getLastFetchTime());
            }
            return apiResponse;
        }
        return null;
    }

    public ApiResponse muteUserNotifications(String userId, Long notificationAfterTime) {
        ApiResponse response = userClientService.muteUserNotifications(userId, notificationAfterTime);

        if (response == null) {
            return null;
        }
        if (response.isSuccess()) {
            new ContactDatabase(context).updateNotificationAfterTime(userId, notificationAfterTime);
        }

        return response;
    }

    public List<MuteUserResponse> getMutedUserList() {
        MuteUserResponse[] mutedUserList = userClientService.getMutedUserList();

        if (mutedUserList == null) {
            return null;
        }
        for (MuteUserResponse muteUserResponse : mutedUserList) {
            processMuteUserResponse(muteUserResponse);
        }
        return Arrays.asList(mutedUserList);
    }

    public String updateDisplayNameORImageLink(String displayName, String profileImageLink, String localURL, String status) {
        return updateDisplayNameORImageLink(displayName, profileImageLink, status, null);
    }

    public String updateDisplayNameORImageLink(String displayName, String profileImageLink, String localURL, String status, String contactNumber) {

        ApiResponse response = userClientService.updateDisplayNameORImageLink(displayName, profileImageLink, status, contactNumber);

        if (response == null) {
            return null;
        }
        if (response != null && response.isSuccess()) {
            Contact contact = baseContactService.getContactById(MobiComUserPreference.getInstance(context).getUserId());
            if (!TextUtils.isEmpty(displayName)) {
                contact.setFullName(displayName);
            }
            if (!TextUtils.isEmpty(profileImageLink)) {
                contact.setImageURL(profileImageLink);
            }
            contact.setLocalImageUrl(localURL);
            if (!TextUtils.isEmpty(status)) {
                contact.setStatus(status);
            }
            if (!TextUtils.isEmpty(contactNumber)) {
                contact.setContactNumber(contactNumber);
            }
            baseContactService.upsert(contact);
            Contact contact1 = baseContactService.getContactById(MobiComUserPreference.getInstance(context).getUserId());
            Utils.printLog(context, "UserService", contact1.getImageURL() + ", " + contact1.getDisplayName() + "," + contact1.getStatus() + "," + contact1.getStatus());
        }
        return response.getStatus();
    }


    public void processUserDetailsResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            List<UserDetail> userDetails = (List<UserDetail>) GsonUtils.getObjectFromJson(response, new TypeToken<List<UserDetail>>() {
            }.getType());
            for (UserDetail userDetail : userDetails) {
                processUser(userDetail);
            }
        }
    }

    public void processUserDetailsByUserIds(Set<String> userIds) {
        userClientService.postUserDetailsByUserIds(userIds);
    }

    public ApiResponse processUserReadConversation() {
        return userClientService.getUserReadServerCall();
    }

    public String processUpdateUserPassword(String oldPassword, String newPassword) {
        String response = userClientService.updateUserPassword(oldPassword, newPassword);
        if (!TextUtils.isEmpty(response) && MobiComKitConstants.SUCCESS.equals(response)) {
            userPreference.setPassword(newPassword);
        }
        return response;
    }


    public void processPackageDetail() {
        CustomerPackageDetail customerPackageDetail = new CustomerPackageDetail();
        customerPackageDetail.setApplicationKey((MobiComKitClientService.getApplicationKey(context)));
        customerPackageDetail.setPackageName(context.getPackageName());
        String response = userClientService.packageDetail(customerPackageDetail);
        if (!TextUtils.isEmpty(response) && response.equals(MobiComKitConstants.APPLICATION_INFO_RESPONSE)) {
            userPreference.setApplicationInfoCallDone(true);
        } else {
            userPreference.setApplicationInfoCallDone(false);
        }
    }

    public void processContactSync() {
        Set<String> userIds = new HashSet<String>();
        SyncApiResponse apiResponse = syncClientService.getSyncCall(MobiComUserPreference.getInstance(context).getContactSyncTime(), SyncClientService.SyncType.CONTACT);
        if (apiResponse == null || apiResponse.getResponse() == null || apiResponse.getResponse().isEmpty()) {
            Utils.printLog(context, TAG, "Contact Sync call response is empty.");
            return;
        }
        for (SyncPxy syncPxy : apiResponse.getResponse()) {
            userIds.add(syncPxy.getParam());
        }
        processUserDetails(userIds);
        MobiComUserPreference.getInstance(context).setContactSyncTime(apiResponse.getGeneratedAt());
    }
}
