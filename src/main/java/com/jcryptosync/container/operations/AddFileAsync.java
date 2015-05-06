package com.jcryptosync.container.operations;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.domain.File;
import com.jcryptosync.container.utils.CryptFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;

public class AddFileAsync {

    protected static Logger log = LoggerFactory.getLogger(AddFileAsync.class);

    private File file;
    private InputStream is;

    public AddFileAsync(File file, InputStream is) {
        this.file = file;
        this.is = is;
    }

    public void compute() {
        SecretKey key = CryptFactory.generateKey();

        file.setKey(key.getEncoded());

        byte[] ivByte = CryptFactory.generateRandomIV();
        file.setIv(ivByte);

        Path pathToFile = QuickPreferences.getPathToCryptDir().resolve(file.getFileId());

        saveCryptFile(is, key, ivByte, pathToFile);

        //fileStorage.addFileMetadata(fileMetadata);

        log.info("crypt file: " + file.getName());
    }

    public static byte[] compareHash(InputStream is) {
        MessageDigest messageDigest = CryptFactory.createMessageDigest();


        byte[] arr = new byte[1024];
        try {
            while ((is.read(arr)) != -1) {
                messageDigest.update(arr);
                arr = new byte[1024];
            }
        } catch (IOException e) {
            log.error("hash error", e);
        }

        return messageDigest.digest();
    }


    public static void saveCryptFile(InputStream outIs, SecretKey key, byte[] iv, Path pathToCryptFile) {
        Cipher cipher = CryptFactory.createCipher();

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        try(CipherInputStream is = new CipherInputStream(outIs, cipher)) {

            Files.copy(is, pathToCryptFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}