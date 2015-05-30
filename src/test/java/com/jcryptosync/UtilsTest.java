package com.jcryptosync;

import com.jcryptosync.utils.SyncUtils;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

public class UtilsTest {
    @Test
    public void newNameTest() throws ParseException {
        String strDate = "14.05.2015 22:22:22";
        Date date = SyncUtils.formatter.parse(strDate);
        String name1 = "blabla";
        String name2 = "blabla.txt";
        String name3 = "blabla.bla.bla.mp4";

        String result = SyncUtils.generateName(name1, date);

        Assert.assertEquals(name1 + "("+ strDate + ")", result);

        result = SyncUtils.generateName(name2, date);

        Assert.assertEquals("blabla(" + strDate + ").txt", result);

        result = SyncUtils.generateName(name3, date);

        Assert.assertEquals("blabla.bla.bla(" + strDate + ").mp4", result);
    }
}
