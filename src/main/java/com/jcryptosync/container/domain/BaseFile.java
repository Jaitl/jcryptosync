package com.jcryptosync.container.domain;

import java.util.UUID;

public abstract class BaseFile {
    private java.lang.String name;

    private String fileId;

    private String folderId;

    public BaseFile(String name, String folderId) {
        this.name = name;
        this.folderId = folderId;
        this.fileId = new String(UUID.randomUUID().toString());
        System.out.println("new base file id: " + getFileId());
    }

    public BaseFile() {
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }


    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public java.lang.String getFileId() {
        return fileId;
    }

    public void setFileId(java.lang.String fileId) {
        this.fileId = fileId;
    }

    public abstract boolean isFile();
    public abstract boolean isFolder();
}
