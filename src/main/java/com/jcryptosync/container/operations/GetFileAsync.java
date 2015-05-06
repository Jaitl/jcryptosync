package com.jcryptosync.container.operations;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.domain.File;
import com.jcryptosync.container.utils.CryptFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

public class GetFileAsync {
    protected static Logger log = LoggerFactory.getLogger(GetFileAsync.class);

    File file;
    OutputStream os;

    public GetFileAsync(File file, OutputStream os) {
        this.file = file;
        this.os = os;
    }

    public void compute() {

        decryptFile(file);

        log.info("file decrypted:" + file.getName());
    }

    private void decryptFile(File metadata) {

        Path enctyptFile = QuickPreferences.getPathToCryptDir();
        enctyptFile = enctyptFile.resolve(metadata.getFileId());

        Cipher cipher = CryptFactory.createCipher();

        byte[] key = metadata.getKey();

        SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "AES");

        byte[] iv = metadata.getIv();

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        try(CipherOutputStream cos = new CipherOutputStream(os, cipher)) {

            Files.copy(enctyptFile, cos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
