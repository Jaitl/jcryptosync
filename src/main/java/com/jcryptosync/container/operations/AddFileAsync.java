package com.jcryptosync.container.operations;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.file.FileMetadata;
import com.jcryptosync.container.file.FileStorage;
import com.jcryptosync.utils.CryptFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
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
        try {
            log.info("wait 2 second");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setHash(compareHash(newFile));

        String id = UUID.randomUUID().toString();
        fileMetadata.setId(id);

        fileMetadata.setName(newFile.getFileName().toString());

        SecretKey key = CryptFactory.generateKey();

        fileMetadata.setKey(key.getEncoded());

        byte[] ivByte = CryptFactory.generateRandomIV();
        fileMetadata.setIv(ivByte);

        Path pathToFile = QuickPreferences.getPathToCryptDir().resolve(id);

        saveCryptFile(newFile, key, ivByte, pathToFile);

        fileStorage.addFileMetadata(fileMetadata);

        log.info("added new file: " + fileMetadata.getName());
    }

    public static byte[] compareHash(Path path) {
        MessageDigest messageDigest = CryptFactory.createMessageDigest();

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


    public static void saveCryptFile(Path path, SecretKey key, byte[] iv, Path pathToCryptFile) {
        Cipher cipher = CryptFactory.createCipher();

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        try(CipherInputStream is = new CipherInputStream(new FileInputStream(path.toFile()), cipher)) {

            Files.copy(is, pathToCryptFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}