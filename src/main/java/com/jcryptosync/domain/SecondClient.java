package com.jcryptosync.domain;

import com.jcryptosync.sync.SyncFiles;

public class SecondClient {
    public String host;
    public SyncFiles syncFilesService;
    public int port;
    public Token token;

    public SecondClient() {
    }

    public SecondClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public SyncFiles getSyncFilesService() {
        return syncFilesService;
    }

    public void setSyncFilesService(SyncFiles syncFilesService) {
        this.syncFilesService = syncFilesService;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
