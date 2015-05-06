package com.jcryptosync.container.domain;

public class File extends BaseFile {

    private byte[] key;
    private byte[] iv;

    public File(java.lang.String name, String folderId) {
        super(name, folderId);
    }

    public File() {
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean isFolder() {
        return false;
    }
}
