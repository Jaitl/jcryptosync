package com.jcryptosync.container;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.fileCrypto.FileMetadata;
import com.jcryptosync.fileCrypto.FileStorage;
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

public class ContainerDecrypt {

    public void decrypt() {
        List<FileMetadata> metadataList = FileStorage.getInstance().getMetadataList();
        metadataList.stream().forEach(f -> decryptFile(f));
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
}
