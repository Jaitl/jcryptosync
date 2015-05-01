package com.jcryptosync.container;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.file.FileMetadata;
import com.jcryptosync.container.file.FileStorage;
import com.jcryptosync.utils.KeyUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class ContainerDecryptAsync extends RecursiveAction {

    Callback callback;

    @Override
    protected void compute() {
        List<FileMetadata> metadataList = FileStorage.getInstance().getMetadataList();
        metadataList.parallelStream().forEach(f -> decryptFile(f));

        callback.callback();
    }

    private void decryptFile(FileMetadata metadata) {
        Path decryptFile = QuickPreferences.getPathToFilesDir();
        decryptFile = decryptFile.resolve(metadata.getName());

        Path enctyptFile = QuickPreferences.getPathToCryptDir();
        enctyptFile = enctyptFile.resolve(metadata.getId());

        Cipher cipher = KeyUtils.createCipher();

        byte[] key = metadata.getKey();

        SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "AES");

        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }


        try(FileInputStream fis = new FileInputStream(enctyptFile.toFile())) {
            CipherInputStream is = new CipherInputStream(fis, cipher);
            Files.copy(is, decryptFile);
        } catch (IOException e) {

        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void callback();
    }
}
