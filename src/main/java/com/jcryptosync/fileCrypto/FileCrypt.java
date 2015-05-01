package com.jcryptosync.fileCrypto;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.utils.KeyUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;

public class FileCrypt {
    public void save(Path path) {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setHash(compareHash(path));

        String id = UUID.randomUUID().toString();
        fileMetadata.setId(id);

        fileMetadata.setName(path.getFileName().toString());

        SecretKey key = KeyUtils.generateKey();

        fileMetadata.setKey(key.getEncoded());

        Path pathToFile = QuickPreferences.getPathToCryptDir().resolve(id);

        saveCryptFile(path, key, pathToFile);

        FileStorage fileStorage = FileStorage.getInstance();

        fileStorage.addFileMetadata(fileMetadata);
    }

    private byte[] compareHash(Path path) {
        MessageDigest messageDigest = KeyUtils.createMessageDigest();

        try(SeekableByteChannel channel = Files.newByteChannel(path)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (channel.read(buffer) > 0) {
                buffer.rewind();
                messageDigest.update(buffer);
                buffer.flip();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messageDigest.digest();
    }

    private void saveCryptFile(Path path, SecretKey key, Path pathToCryptFile) {
        Cipher cipher = KeyUtils.createCipher();

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }


        try (FileOutputStream fos = new FileOutputStream(pathToCryptFile.toFile())) {
            CipherOutputStream os = new CipherOutputStream(fos, cipher);

            Files.copy(path, os);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}