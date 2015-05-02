package com.jcryptosync.utils;

import com.jcryptosync.PrimaryKey;
import com.jcryptosync.exceptoins.NoCorrectPasswordException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.MessageDigest;

public class PrimaryKeyUtils {

    public static PrimaryKey generateNewPrimaryKey() {
        SecretKey secretKey = CryptFactory.generateKey();

        return PrimaryKey.fromSecretKey(secretKey);
    }

    public static SecretKey generateKeyFromPassword(String password) {
        MessageDigest digest = CryptFactory.createMessageDigest();

        byte[] hash = null;

        try {
            hash = digest.digest(password.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new SecretKeySpec(hash, 0, 16, "AES");
    }

    public static byte[] encryptKey(PrimaryKey primaryKey, SecretKey key) {
        Cipher cipher = CryptFactory.createCipher();

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] plainKey = new byte[0];
        try {
            plainKey = primaryKey.toJson().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] cipherKey = null;

        try {
            cipherKey = cipher.doFinal(plainKey);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return cipherKey;
    }

    public static PrimaryKey decryptKey(byte[] cryptKey, SecretKey key) throws NoCorrectPasswordException {
        Cipher cipher = CryptFactory.createCipher();

        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] plainKey = null;

        try {
            plainKey = cipher.doFinal(cryptKey);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            throw new NoCorrectPasswordException("Неправильный пароль");
        }

        String jsonKey = null;
        try {
            jsonKey = new String(plainKey, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return PrimaryKey.fromJson(jsonKey);
    }

    public static void saveNewCryptKeyToFile(String password, Path pathToKey) throws IOException {
        PrimaryKey primaryKey = PrimaryKeyUtils.generateNewPrimaryKey();
        SecretKey passKey = PrimaryKeyUtils.generateKeyFromPassword(password);
        byte[] cryptKey = PrimaryKeyUtils.encryptKey(primaryKey, passKey);

        Files.write(pathToKey, cryptKey, StandardOpenOption.CREATE_NEW);
    }

    public static PrimaryKey loadPrimaryKeyFromFile(String password, Path pathToKey) throws IOException, NoCorrectPasswordException {
        byte[] cryptKey;

        cryptKey = Files.readAllBytes(pathToKey);


        SecretKey passKey = PrimaryKeyUtils.generateKeyFromPassword(password);

        return PrimaryKeyUtils.decryptKey(cryptKey, passKey);
    }

    public static boolean checkPassword(String password, Path pathToKey) throws IOException, NoCorrectPasswordException {
        PrimaryKey key = loadPrimaryKeyFromFile(password, pathToKey);

        return key.getType().contains("AES");
    }
}
