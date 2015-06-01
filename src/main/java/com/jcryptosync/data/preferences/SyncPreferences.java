package com.jcryptosync.data.preferences;

import com.jcryptosync.domain.SecondClient;
import com.jcryptosync.sync.SyncClient;

import java.util.HashMap;
import java.util.Map;

public class SyncPreferences {

    private static final SyncPreferences instance = new SyncPreferences();
    private Map<String, String> settingsString = new HashMap<>();
    private Map<String, SecondClient> clientMap = new HashMap<>();
    private byte[] key;
    private SyncClient syncClient;

    private static final String GROUP_ID = "group_id";

    private SyncPreferences() {}

    public static SyncPreferences getInstance() {
        return instance;
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

    public SyncClient getSyncClient() {
        return syncClient;
    }

    public void setSyncClient(SyncClient syncClient) {
        this.syncClient = syncClient;
    }

    public Map<String, SecondClient> getClientMap() {
        return clientMap;
    }


}
