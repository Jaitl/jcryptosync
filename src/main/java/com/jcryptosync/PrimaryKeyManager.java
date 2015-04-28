package com.jcryptosync;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class PrimaryKeyManager {
    public PrimaryKey generateKey() {
        KeyGenerator keyGen = null;

        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        return new PrimaryKey(secretKey);
    }
}
