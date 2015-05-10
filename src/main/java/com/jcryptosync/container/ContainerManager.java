package com.jcryptosync.container;

import com.jcryptosync.container.exceptoins.ContainerMountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ContainerManager {

    protected static Logger log = LoggerFactory.getLogger(ContainerManager.class);

    private Jetty jetty = new Jetty();

    public void startJetty() {
        jetty.startServer();
    }

    public void stopJetty() {
        jetty.stopServer();
        log.info("stop jetty");
    }

    public abstract void openContainer() throws ContainerMountException;
    public abstract void closeContainer() throws ContainerMountException;
    public abstract void mountContainer() throws ContainerMountException;
    public abstract void unmountContainer() throws ContainerMountException;
    public abstract boolean isMount();

    public static ContainerManager createManager() {
        String OS = System.getProperty("os.name").toLowerCase();

        if(OS.contains("linux")) {
            return new LinuxContainerManager();
        } else {
            return null;
        }
    }
}
