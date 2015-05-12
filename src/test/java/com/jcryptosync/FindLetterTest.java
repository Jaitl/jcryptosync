package com.jcryptosync;

import com.jcryptosync.container.WindowsContainerManager;
import org.junit.Assert;
import org.junit.Test;

public class FindLetterTest {
    @Test
    public void testFindLetter() {
        char letter = 'c';
        WindowsContainerManager manager = new WindowsContainerManager();
        char findLetter = manager.findLetter(letter);

        Assert.assertEquals('f', findLetter);
    }

    @Test
    public void testUsedLetter() {
        char letter = 'c';
        WindowsContainerManager manager = new WindowsContainerManager();
        boolean result = manager.isUsedLetter(letter);

        Assert.assertEquals(true, result);
    }
}
