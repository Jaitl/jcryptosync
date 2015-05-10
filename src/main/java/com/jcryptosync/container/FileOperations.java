package com.jcryptosync.container;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.utils.CryptFactory;
import com.jcryptosync.container.webdav.CryptFile;
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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

public class FileOperations {
    protected static Logger log = LoggerFactory.getLogger(FileOperations.class);

    private CryptFile file;

    public FileOperations(CryptFile file) {
        this.file = file;
    }

    public void addFile(InputStream is) {
        SecretKey key = CryptFactory.generateKey();

        file.setKey(key.getEncoded());

        byte[] ivByte = CryptFactory.generateRandomIV();
        file.setIv(ivByte);

        Path pathToFile = QuickPreferences.getPathToCryptDir().resolve(file.getUniqueId());

        saveCryptFile(is, key, ivByte, pathToFile);

        log.info("crypt file: " + file.getName());
    }

    public void getFile(OutputStream os) {
        Path enctyptFile = QuickPreferences.getPathToCryptDir();
        enctyptFile = enctyptFile.resolve(file.getUniqueId());

        Cipher cipher = CryptFactory.createCipher();

        byte[] key = file.getKey();

        SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "AES");

        byte[] iv = file.getIv();

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            log.error("decrypt error", e);
        }

        try(CipherOutputStream cos = new CipherOutputStream(os, cipher)) {
            Files.copy(enctyptFile, cos);
        } catch (IOException e) {
            log.error("decrypt error", e);
        }
    }

    public void deleteFile() {
        Path pathToCryptFile = QuickPreferences.getPathToCryptDir();
        pathToCryptFile = pathToCryptFile.resolve(file.getUniqueId());

        try {
            Files.delete(pathToCryptFile);
        } catch (IOException e) {
            log.error("delete error", e);
        }
    }

    private void saveCryptFile(InputStream outIs, SecretKey key, byte[] iv, Path pathToCryptFile) {
        Cipher cipher = CryptFactory.createCipher();

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            log.error("crypt error", e);
        }

        try(CipherInputStream is = new CipherInputStream(outIs, cipher)) {

            Files.copy(is, pathToCryptFile);

        } catch (IOException e) {
            log.error("crypt error", e);
        }
    }
}
