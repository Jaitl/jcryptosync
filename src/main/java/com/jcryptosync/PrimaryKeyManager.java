package com.jcryptosync;

import com.jcryptosync.utils.PrimaryKeyUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PrimaryKeyManager {

    public static void saveNewPrimaryKey(String password, Path pathToKey) throws IOException {
        PrimaryKey primaryKey = PrimaryKeyUtils.generateNewPrimaryKey();
        SecretKey passKey = PrimaryKeyUtils.generateKeyFromPassword(password);
        byte[] cryptKey = PrimaryKeyUtils.encryptKey(primaryKey, passKey);

        Files.write(pathToKey, cryptKey, StandardOpenOption.CREATE_NEW);
    }

    public static PrimaryKey loadPrimaryKey(String password, Path pathToKey) throws IOException {
        byte[] cryptKey = new byte[0];

        cryptKey = Files.readAllBytes(pathToKey);


        SecretKey passKey = PrimaryKeyUtils.generateKeyFromPassword(password);

        return PrimaryKeyUtils.decryptKey(cryptKey, passKey);
    }
}
