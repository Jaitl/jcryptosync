package com.jcryptosync.domain;

import com.google.gson.Gson;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainKey {

    private byte[] key;

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public MainKey(byte[] key) {
        this.key = key;
    }

    public MainKey() {
    }

    public SecretKey getSecretKey() {
        return new SecretKeySpec(key, 0, key.length, "AES");
    }

    public static MainKey fromSecretKey(SecretKey key) {
        return new MainKey(key.getEncoded());
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static MainKey fromJson(String json) {
        Gson gson = new Gson();

        return gson.fromJson(json, MainKey.class);
    }
}
