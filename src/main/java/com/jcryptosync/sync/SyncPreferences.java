package com.jcryptosync.sync;

import com.jcryptosync.sync.client.SecondClient;

import java.util.ArrayList;
import java.util.List;

public class SyncPreferences {

    private static SyncPreferences instance = new SyncPreferences();

    private SyncPreferences() {}

    public static SyncPreferences getInstance() {
        return instance;
    }

    private List<SecondClient> clientList = new ArrayList<>();

    public void addClient(SecondClient client) {
        clientList.add(client);
    }

    public List<SecondClient> getClientList() {
        return clientList;
    }
}
