package com.jcryptosync;

import com.jcryptosync.utils.PasswordUtils;
import org.junit.Assert;
import org.junit.Test;

public class PasswordUtilsTest {
    @Test
    public void testPass() {
        String pass1 = "12345";
        String pass2 = "fe34fGfdabed";
        String pass3 = "r5gFvd6hgD5dvdF";
        String pass4 = "r#5gF$vd6%hgDdvdF";

        Assert.assertEquals(false, PasswordUtils.checkPassword(pass1));
        Assert.assertEquals(false, PasswordUtils.checkPassword(pass2));
        Assert.assertEquals(false, PasswordUtils.checkPassword(pass3));
        Assert.assertEquals(true, PasswordUtils.checkPassword(pass4));
    }
}
