package com.jcryptosync.data;

import com.jcryptosync.domain.MainKey;
import com.jcryptosync.exceptoins.NoCorrectPasswordException;
import com.jcryptosync.utils.CryptFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;

public class MasterKeyManager {

    private static String iv = "jCryptoSync12345";

    public MainKey generateNewPrimaryKey() {
        SecretKey secretKey = CryptFactory.generateKey();

        return MainKey.fromSecretKey(secretKey);
    }

    public SecretKey generateKeyFromPassword(String password) {
        MessageDigest digest = CryptFactory.createMessageDigest();

        byte[] hash = null;

        try {
            hash = digest.digest(password.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new SecretKeySpec(hash, 0, 16, "AES");
    }

    public byte[] encryptKey(MainKey mainKey, SecretKey key) {
        Cipher cipher = CryptFactory.createCipher();

        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        byte[] plainKey = new byte[0];
        try {
            plainKey = mainKey.toJson().getBytes("UTF-8");
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

    public MainKey decryptKey(byte[] cryptKey, SecretKey key) throws NoCorrectPasswordException {
        Cipher cipher = CryptFactory.createCipher();

        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());

        try {
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
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

        return MainKey.fromJson(jsonKey);
    }

    public void saveNewCryptKeyToFile(String password, Path pathToKey) throws IOException {
        MainKey mainKey = generateNewPrimaryKey();
        SecretKey passKey = generateKeyFromPassword(password);
        byte[] cryptKey = encryptKey(mainKey, passKey);

        Files.write(pathToKey, cryptKey, StandardOpenOption.CREATE_NEW);
    }

    public MainKey loadPrimaryKeyFromFile(String password, Path pathToKey) throws IOException, NoCorrectPasswordException {
        byte[] cryptKey;

        cryptKey = Files.readAllBytes(pathToKey);


        SecretKey passKey = generateKeyFromPassword(password);

        return decryptKey(cryptKey, passKey);
    }

    public boolean checkPassword(String password, Path pathToKey) throws IOException, NoCorrectPasswordException {
        MainKey key = loadPrimaryKeyFromFile(password, pathToKey);

        return key.getKey() != null;
    }
}
