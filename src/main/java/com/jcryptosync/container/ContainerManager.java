package com.jcryptosync.container;

import com.jcryptosync.exceptoins.ContainerMountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ContainerManager {

    protected static Logger log = LoggerFactory.getLogger(ContainerManager.class);

    public abstract void openContainer() throws ContainerMountException;
    public abstract void closeContainer() throws ContainerMountException;
    public abstract void startFileWatcher();
    public abstract void stopFileWatcher();

    public static ContainerManager createManager() {
        String OS = System.getProperty("os.name").toLowerCase();

        if(OS.contains("linux")) {
            return new LinuxContainerManager();
        } else {
            return null;
        }
    }
}
