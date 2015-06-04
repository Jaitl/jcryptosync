package com.jcryptosync.data;

import com.jcryptosync.domain.MainKey;
import com.jcryptosync.exceptoins.NoCorrectMasterKeyException;
import com.jcryptosync.exceptoins.NoCorrectPasswordException;
import com.jcryptosync.utils.CryptFactory;
import com.jcryptosync.utils.HashUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainKeyManager {
    private static String iv = "jCryptoSync12345";

    public byte[] generateAndSaveNewMainKey(String password, Path pathToKey) {
        SecretKey secretKey = CryptFactory.generateKey();
        byte[] passKey = HashUtils.computeMD5(password.getBytes());
        byte[] hmacKey = generateDigest(secretKey.getEncoded(), passKey);

        MainKey mainKey = new MainKey(secretKey.getEncoded(), hmacKey);
        byte[] cryptMainKey = cryptMainKey(mainKey, new SecretKeySpec(passKey, "AES"));

        try {
            Files.write(pathToKey, cryptMainKey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return secretKey.getEncoded();
    }

    public byte[] loadMainKey(String password, Path pathToKey) throws NoCorrectPasswordException, NoCorrectMasterKeyException {
        byte[] cryptMainKey = new byte[0];
        try {
            cryptMainKey = Files.readAllBytes(pathToKey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] passKey = HashUtils.computeMD5(password.getBytes());
        MainKey mainKey = decryptMainKey(cryptMainKey, new SecretKeySpec(passKey, "AES"));

        byte[] hmac = generateDigest(mainKey.getKey(), passKey);

        if(!Arrays.equals(hmac, mainKey.getDigest()))
            throw new NoCorrectMasterKeyException("");

        return mainKey.getKey();
    }

    private MainKey decryptMainKey(byte[] cryptMainKey, SecretKey key) throws NoCorrectPasswordException {
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
            plainKey = cipher.doFinal(cryptMainKey);
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

    private byte[] cryptMainKey(MainKey mainKey, SecretKey secretKey) {
        Cipher cipher = CryptFactory.createCipher();

        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
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

    private  byte[] generateDigest(byte[] masterKey, byte[] passKey) {
        Mac sha256_HMAC = null;

        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(passKey, "HmacSHA256");
            sha256_HMAC.init(secret_key);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return sha256_HMAC.doFinal(masterKey);
    }

    public static byte[] computeCompositeKey(String password, byte[] masterKey) {
        byte[] hashPass = HashUtils.computeMD5(password.getBytes());
        byte[] hashKey = HashUtils.computeMD5(masterKey);

        byte[] joinHash = ArrayUtils.addAll(hashPass, hashKey);

        return HashUtils.computeSHA256(joinHash);
    }

    public static String computeGroupId(byte[] masterKey) {
        byte[] hash = HashUtils.computeSHA256(masterKey);

        return "group-" + Hex.encodeHexString(hash);
    }
}
