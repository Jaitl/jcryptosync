package com.jcryptosync;

import com.google.gson.Gson;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class PrimaryKey {
    private String type = "AES-128";

    private byte[] key;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public PrimaryKey(byte[] key) {
        this.key = key;
    }

    public PrimaryKey() {
    }

    public SecretKey getSecretKey() {
        return new SecretKeySpec(key, 0, key.length, "AES");
    }

    public static PrimaryKey fromSecretKey(SecretKey key) {
        return new PrimaryKey(key.getEncoded());
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static PrimaryKey fromJson(String json) {
        Gson gson = new Gson();

        return gson.fromJson(json, PrimaryKey.class);
    }
}
