package com.jcryptosync.sync;

import java.util.HashMap;
import java.util.Map;

public class SyncPreferences {

    private static final SyncPreferences instance = new SyncPreferences();
    private Map<String, String> settingsString = new HashMap<>();
    private byte[] key;

    private static final String CLIENT_ID = "client_id";
    private static final String GROUP_ID = "group_id";

    private SyncPreferences() {}

    public static SyncPreferences getInstance() {
        return instance;
    }

    public void setClientId(String clientId) {
        settingsString.put(CLIENT_ID, clientId);
    }

    public String getClientId() {
        return settingsString.get(CLIENT_ID);
    }

    public void setGroupId(String groupId) {
        settingsString.put(GROUP_ID, groupId);
    }

    public String getGroupId() {
        return settingsString.get(GROUP_ID);
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }
}
