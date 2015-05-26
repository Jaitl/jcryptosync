package com.jcryptosync;

import com.jcryptosync.exceptoins.NoCorrectPasswordException;
import com.jcryptosync.domain.MainKey;
import com.jcryptosync.data.MasterKeyManager;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.SecretKey;

public class PrimaryCryptFactoryTest {

    @Test
    public void testCreateKey() {

        MasterKeyManager keyManager = new MasterKeyManager();

        MainKey mainKey = keyManager.generateNewPrimaryKey();
        String password = "rTf45345%^gfF^DS$fs";
        SecretKey key = keyManager.generateKeyFromPassword(password);

        byte[] cryptKey = keyManager.encryptKey(mainKey, key);

        MainKey decryptKey = null;
        try {
            decryptKey = keyManager.decryptKey(cryptKey, key);
        } catch (NoCorrectPasswordException e) {
            e.printStackTrace();
        }

        Assert.assertArrayEquals(mainKey.getKey(), decryptKey.getKey());
    }
}
