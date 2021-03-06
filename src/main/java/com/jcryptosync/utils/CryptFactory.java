package com.jcryptosync.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptFactory {
    public static SecretKey generateKey() {
        KeyGenerator keyGen = null;

        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();

        return secretKey;
    }

    public static Cipher createCipher() {
        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance("AES/CFB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        return cipher;
    }

}
