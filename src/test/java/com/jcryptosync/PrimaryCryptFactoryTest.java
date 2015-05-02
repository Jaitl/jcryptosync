package com.jcryptosync;

import com.jcryptosync.exceptoins.NoCorrectPasswordException;
import com.jcryptosync.primarykey.PrimaryKey;
import com.jcryptosync.primarykey.PrimaryKeyManager;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.SecretKey;

public class PrimaryCryptFactoryTest {

    @Test
    public void testCreateKey() {

        PrimaryKeyManager keyManager = new PrimaryKeyManager();

        PrimaryKey primaryKey = keyManager.generateNewPrimaryKey();
        String password = "rTf45345%^gfF^DS$fs";
        SecretKey key = keyManager.generateKeyFromPassword(password);

        byte[] cryptKey = keyManager.encryptKey(primaryKey, key);

        PrimaryKey decryptKey = null;
        try {
            decryptKey = keyManager.decryptKey(cryptKey, key);
        } catch (NoCorrectPasswordException e) {
            e.printStackTrace();
        }

        Assert.assertArrayEquals(primaryKey.getKey(), decryptKey.getKey());
    }
}
