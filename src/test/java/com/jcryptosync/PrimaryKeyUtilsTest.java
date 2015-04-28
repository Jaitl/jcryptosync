package com.jcryptosync;

import com.jcryptosync.exceptoins.NoCorrectPasswordException;
import com.jcryptosync.utils.PrimaryKeyUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.SecretKey;

public class PrimaryKeyUtilsTest {

    @Test
    public void testCreateKey() {
        PrimaryKey primaryKey = PrimaryKeyUtils.generateNewPrimaryKey();
        String password = "rTf45345%^gfF^DS$fs";
        SecretKey key = PrimaryKeyUtils.generateKeyFromPassword(password);

        byte[] cryptKey = PrimaryKeyUtils.encryptKey(primaryKey, key);

        PrimaryKey decryptKey = null;
        try {
            decryptKey = PrimaryKeyUtils.decryptKey(cryptKey, key);
        } catch (NoCorrectPasswordException e) {
            e.printStackTrace();
        }

        Assert.assertArrayEquals(primaryKey.getKey(), decryptKey.getKey());
    }
}
