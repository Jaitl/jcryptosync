package com.jcryptosync.vfs.filesystem;

import com.jcryptosync.preferences.ContainerPreferences;
import com.jcryptosync.preferences.UserPreferences;
import com.jcryptosync.sync.VectorTimePair;
import com.jcryptosync.ui.container.MessageService;
import com.jcryptosync.utils.CryptFactory;
import com.jcryptosync.utils.SyncUtils;
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
import java.security.*;
import java.util.Date;
import java.util.UUID;

public class FileOperations {
    protected static Logger log = LoggerFactory.getLogger(FileOperations.class);

    public static void cryptFile(CryptFile file, InputStream is) {
        SecretKey key = CryptFactory.generateKey();

        file.setKey(key.getEncoded());

        byte[] ivByte = generateRandomIV();
        file.setIv(ivByte);

        try {
            if (is.available() > 0) {
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

        try (CipherOutputStream cos = new CipherOutputStream(os, cipher)) {
            StreamUtils.readTo(is, cos);
            is.close();

        } catch (IOException e) {
            log.error("decrypt error", e);
        }

        MessageService.encryptFile(file);
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

        try {
            if(is.available() > 0)
                saveCryptFile(file, is);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try (CipherInputStream is = new CipherInputStream(inIs, cipher)) {
            byte [] buffer = new byte[1024];
            int sizeRead = -1;
            while ((sizeRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, sizeRead);
                os.write(buffer, 0, sizeRead);
            }
        } catch (IOException e) {
            log.error("crypt error", e);
        }

        try {
            if(os != null)
            os.close();
        } catch (IOException e) {
            log.error("crypt error", e);
        }

        byte[] hash = digest.digest();
        file.setHash(hash);

        MessageService.cryptFile(file);
    }

    public static CryptFile copyFile(CryptFile cryptFile, String name) {
        CryptFile newFile = new CryptFile();
        newFile.setId(UUID.randomUUID().toString());
        newFile.setCreatedDate(new Date());
        newFile.setModDate(new Date());
        newFile.setDeleted(false);
        newFile.setLength(cryptFile.getLength());
        newFile.setContentType(cryptFile.getContentType());
        newFile.setParentId(cryptFile.getParentId());
        newFile.setDeleted(cryptFile.isDeleted());

        newFile.setVector(new VectorTimePair());
        newFile.getVector().increaseModification(ContainerPreferences.getInstance().getClientId());
        newFile.getVector().increaseSynchronization(ContainerPreferences.getInstance().getClientId());

        String newName;

        if (name != null) {
            newName = name;
        } else {
            newName = SyncUtils.generateName(cryptFile.getName(), cryptFile.getModDate());
        }

        newFile.setName(newName);

        SecretKey key = CryptFactory.generateKey();

        newFile.setKey(key.getEncoded());

        byte[] ivByte = generateRandomIV();
        newFile.setIv(ivByte);

        if(cryptFile.getLength() > 0) {
            byte[] hash = copyFileData(cryptFile, newFile);
            newFile.setHash(hash);
        }

        return newFile;
    }

    private static byte[] copyFileData(CryptFile oldFile, CryptFile newFile) {
        Path pathToOldFile = UserPreferences.getPathToCryptDir().resolve(oldFile.getUniqueId());
        Path pathToNewFile = UserPreferences.getPathToCryptDir().resolve(newFile.getUniqueId());

        Cipher inputCipher = CryptFactory.createCipher();
        Cipher outputCipher = CryptFactory.createCipher();

        try {
            inputCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(oldFile.getKey(), "AES"), new IvParameterSpec(oldFile.getIv()));
            outputCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(newFile.getKey(), "AES"), new IvParameterSpec(newFile.getIv()));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            log.error("decrypt error", e);
        }

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        CipherInputStream cis = null;
        CipherOutputStream cos = null;
        try {
            cis = new CipherInputStream(Files.newInputStream(pathToOldFile, StandardOpenOption.READ), inputCipher);
            cos = new CipherOutputStream(Files.newOutputStream(pathToNewFile, StandardOpenOption.CREATE_NEW), outputCipher);

            byte [] buffer = new byte[1024];
            int sizeRead = -1;
            while ((sizeRead = cis.read(buffer)) != -1) {
                digest.update(buffer, 0, sizeRead);
                cos.write(buffer, 0, sizeRead);
            }
        } catch (IOException e) {
            log.error("copy error", e);
        } finally {
            try {
                if(cis != null)
                    cis.close();

                if(cos != null)
                    cos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return digest.digest();
    }

    public static byte[] generateRandomIV() {
        byte[] ivBytes = new byte[16];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivBytes);

        return ivBytes;
    }
}
