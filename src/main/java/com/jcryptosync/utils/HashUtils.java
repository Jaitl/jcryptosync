package com.jcryptosync.utils;

import com.jcryptosync.data.ContainerPreferences;
import com.jcryptosync.data.UserPreferences;
import com.jcryptosync.vfs.webdav.CryptFile;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    public static byte[] computeSHA256(byte[] data) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return messageDigest.digest(data);
    }

    public static byte[] computeMD5(byte[] data) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return messageDigest.digest(data);
    }

    public static byte[] computeKey(String password, byte[] masterkey) {
        byte[] bytePass = computeMD5(password.getBytes());

        byte[] unionPass = ArrayUtils.addAll(bytePass, masterkey);

        return computeSHA256(unionPass);
    }

    public static String computeGroupId(byte[] masterKey) {
        byte[] hash = computeSHA256(masterKey);

        return "group-" + Hex.encodeHexString(hash);
    }
}
