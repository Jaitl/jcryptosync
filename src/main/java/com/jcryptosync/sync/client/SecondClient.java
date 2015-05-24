package com.jcryptosync.sync.client;

import com.jcryptosync.sync.SyncFiles;

import javax.xml.ws.Service;

public class SecondClient {
    public String idClient;
    public String host;
    public SyncFiles syncFilesService;
    public int port;

    public SecondClient() {
    }

    public SecondClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
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
}
