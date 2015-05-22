package com.jcryptosync.container;

import com.jcryptosync.container.exceptoins.ContainerMountException;
import com.jcryptosync.container.webdav.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ContainerManager {

    protected final User user;
    protected String pathToWebDavServer;

    public ContainerManager() {
        ContainerPreferences preferences = ContainerPreferences.getInstance();

        int port = preferences.getJettyPort();
        pathToWebDavServer = String.format("http://localhost:%s/webdav", port);

        user = preferences.getUser();
    }

    protected static Logger log = LoggerFactory.getLogger(ContainerManager.class);

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

    public static ContainerManager createManager() {
        String OS = System.getProperty("os.name").toLowerCase();

        if(OS.contains("linux")) {
            return new LinuxContainerManager();
        } else if(OS.contains("win")) {
            return new WindowsContainerManager();
        } else {
            return null;
        }
    }
}
