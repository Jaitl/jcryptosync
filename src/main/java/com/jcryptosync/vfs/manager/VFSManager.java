package com.jcryptosync.vfs.manager;

import com.jcryptosync.preferences.ContainerPreferences;
import com.jcryptosync.domain.User;
import com.jcryptosync.exceptoins.ContainerMountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class VFSManager {

    protected final User user;
    protected String pathToWebDavServer;

    public VFSManager() {
        ContainerPreferences preferences = ContainerPreferences.getInstance();

        int port = preferences.getJettyPort();
        pathToWebDavServer = String.format("http://127.0.0.1:%s/webdav", port);

        user = preferences.getUser();
    }

    protected static Logger log = LoggerFactory.getLogger(VFSManager.class);

    public void openContainer() throws ContainerMountException {
        log.info("open container");

        if(!isMount())
            mountContainer();
    }

    public void closeContainer() throws ContainerMountException {
        log.info("close container");

        if(isMount())
            unmountContainer();
    }

    public abstract void mountContainer() throws ContainerMountException;
    public abstract void unmountContainer() throws ContainerMountException;
    public abstract boolean isMount();

    public static VFSManager createManager() {
        String OS = System.getProperty("os.name").toLowerCase();

        if(OS.contains("linux")) {
            return new LinuxVFSManager();
        } else if(OS.contains("win")) {
            return new WindowsVFSManager();
        } else {
            return null;
        }
    }
}
