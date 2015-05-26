package com.jcryptosync.vfs.filesystem;

import com.jcryptosync.data.UserPreferences;
import com.jcryptosync.utils.CryptFactory;
import com.jcryptosync.vfs.webdav.CryptFile;
import io.milton.common.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

public class FileOperations {
    protected static Logger log = LoggerFactory.getLogger(FileOperations.class);

    public static void cryptFile(CryptFile file, InputStream is) {
        SecretKey key = CryptFactory.generateKey();

        file.setKey(key.getEncoded());

        byte[] ivByte = CryptFactory.generateRandomIV();
        file.setIv(ivByte);

        try {
            if(is.available() > 0) {
                saveCryptFile(file, is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("crypt file: " + file.getName());
    }

    public static void decryptFile(CryptFile file, OutputStream os) {
        Path enctyptFile = UserPreferences.getPathToCryptDir();
        enctyptFile = enctyptFile.resolve(file.getUniqueId());

        InputStream is = null;
        try {
            is = Files.newInputStream(enctyptFile, StandardOpenOption.READ);
        } catch (IOException e) {
            log.error("error open crypt file", e);
        }

        Cipher cipher = CryptFactory.createCipher();

        IvParameterSpec ivSpec = new IvParameterSpec(file.getIv());
        SecretKey key = new SecretKeySpec(file.getKey(), "AES");

        try {
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            log.error("decrypt error", e);
        }

        try(CipherOutputStream cos = new CipherOutputStream(os, cipher)) {
            StreamUtils.readTo(is, cos);

        } catch (IOException e) {
            log.error("decrypt error", e);
        }
    }

    public static void deleteFile(CryptFile file) {
        Path pathToCryptFile = UserPreferences.getPathToCryptDir();
        pathToCryptFile = pathToCryptFile.resolve(file.getUniqueId());

        try {
            Files.delete(pathToCryptFile);
        } catch (IOException e) {
            log.error("delete error", e);
        }
    }

    public static void updateFile(CryptFile file, InputStream is) {
        Path pathToCryptFile = UserPreferences.getPathToCryptDir();
        pathToCryptFile = pathToCryptFile.resolve(file.getUniqueId());

        if (Files.exists(pathToCryptFile)) {
            deleteFile(file);
        }

        saveCryptFile(file, is);
    }

    private static void saveCryptFile(CryptFile file, InputStream inIs) {
        Path pathToFile = UserPreferences.getPathToCryptDir().resolve(file.getUniqueId());

        Cipher cipher = CryptFactory.createCipher();

        OutputStream os = null;
        try {
            os = Files.newOutputStream(pathToFile, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            log.error("error create file", e);
        }

        IvParameterSpec ivSpec = new IvParameterSpec(file.getIv());
        SecretKey key = new SecretKeySpec(file.getKey(), "AES");

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            log.error("crypt error", e);
        }

        try(CipherInputStream is = new CipherInputStream(inIs, cipher)) {
            StreamUtils.readTo(is, os);

        } catch (IOException e) {
            log.error("crypt error", e);
        }
    }
}
