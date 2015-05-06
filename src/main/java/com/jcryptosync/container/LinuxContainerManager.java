package com.jcryptosync.container;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.exceptoins.ContainerMountException;
import com.jcryptosync.container.utils.ContainerUtils;

import java.nio.file.Path;

public class LinuxContainerManager extends ContainerManager {

    String nameMountFolder = "cryptFiles";

    private Path pathToFilesDir = QuickPreferences.getPathToFilesDir();

    @Override
    public void openContainer() throws ContainerMountException {
        log.info("open container");

        String pathToServer = "http://localhost:8080/webdav";

        String path = pathToFilesDir.toString();
        ContainerUtils.linuxOpenContainer(pathToServer, nameMountFolder);
    }

    @Override
    public void closeContainer() throws ContainerMountException {
        log.info("close container");
        ContainerUtils.linuxCloseContainer(nameMountFolder);
    }

}
