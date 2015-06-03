package com.jcryptosync.domain;

import com.google.gson.Gson;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainKey {

    private byte[] key;
    private byte[] digest;

    public byte[] getKey() {
        return key;
    }
    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getDigest() {
        return digest;
    }

    public void setDigest(byte[] digest) {
        this.digest = digest;
    }

    public MainKey(byte[] key, byte[] digest) {
        this.key = key;
        this.digest = digest;
    }

    public MainKey() {
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
