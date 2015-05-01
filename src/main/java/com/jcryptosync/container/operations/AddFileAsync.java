package com.jcryptosync.container.operations;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.file.FileMetadata;
import com.jcryptosync.container.file.FileStorage;
import com.jcryptosync.utils.KeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.RecursiveAction;

public class AddFileAsync extends RecursiveAction {

    protected static Logger log = LoggerFactory.getLogger(AddFileAsync.class);

    private static FileStorage fileStorage = FileStorage.getInstance();

    private Path newFile;

    public AddFileAsync(Path newFile) {
        this.newFile = newFile;
    }

    @Override
    protected void compute() {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setHash(compareHash(newFile));

        String id = UUID.randomUUID().toString();
        fileMetadata.setId(id);

        fileMetadata.setName(newFile.getFileName().toString());

        SecretKey key = KeyUtils.generateKey();

        fileMetadata.setKey(key.getEncoded());

        Path pathToFile = QuickPreferences.getPathToCryptDir().resolve(id);

        saveCryptFile(newFile, key, pathToFile);

        fileStorage.addFileMetadata(fileMetadata);
        log.info("added new file: " + fileMetadata.getName());
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