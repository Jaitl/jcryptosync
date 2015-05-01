package com.jcryptosync.container;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.exceptoins.ContainerMountException;
import com.jcryptosync.fileCrypto.FileStorage;
import com.jcryptosync.utils.ContainerUtils;

import java.nio.file.Path;

public class LinuxContainerManager extends ContainerManager {

    private Path pathToFilesDir = QuickPreferences.getPathToFilesDir();
    FilesFolderWatcher watcher = new FilesFolderWatcher();

    @Override
    public void openContainer() throws ContainerMountException {
        log.info("open container");

        String path = pathToFilesDir.toString();
        ContainerUtils.linuxOpenContainer(path);
    }

    @Override
    public void closeContainer() throws ContainerMountException {
        log.info("close container");
        ContainerUtils.linuxCloseContainer();
    }

    @Override
    public void startFileWatcher() {
        FileStorage.getInstance().loadMetadata();

        log.info("decrypt Files folder");
        ContainerDecrypt containerDecrypt = new ContainerDecrypt();
        containerDecrypt.decrypt();

        watcher.start();
    }

    @Override
    public void stopFileWatcher() {
        watcher.stopWatch();
        log.info("clear Files folder");
        ContainerUtils.clearFiles();
    }
}
