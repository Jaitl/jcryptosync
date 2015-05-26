package com.jcryptosync;

import com.jcryptosync.vfs.manager.WindowsVFSManager;
import org.junit.Assert;
import org.junit.Test;

public class FindLetterTest {
    @Test
    public void testFindLetter() {
        char letter = 'c';
        WindowsVFSManager manager = new WindowsVFSManager();
        char findLetter = manager.findLetter(letter);

        Assert.assertEquals('f', findLetter);
    }

    @Test
    public void testUsedLetter() {
        char letter = 'c';
        WindowsVFSManager manager = new WindowsVFSManager();
        boolean result = manager.isUsedLetter(letter);

        Assert.assertEquals(true, result);
    }
}
