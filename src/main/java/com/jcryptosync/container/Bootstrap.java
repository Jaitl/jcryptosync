package com.jcryptosync.container;

import com.jcryptosync.container.exceptoins.ContainerMountException;
import com.jcryptosync.container.utils.SecurityUtils;
import com.jcryptosync.container.webdav.User;

public class Bootstrap {
    private ContainerManager containerManager;
    protected static transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Bootstrap.class);
    private Jetty jetty;

    public void runApplication() {
        findPort();
        generateUser();
        containerManager = ContainerManager.createManager();
        jetty = new Jetty();
        runJetty();
    }


    public void stopApplication() {
        try {
            containerManager.closeContainer();
        } catch (ContainerMountException e) {
            e.printStackTrace();
        }

        stopJetty();
    }

    private void runJetty() {
        jetty.startServer();
    }

    private void stopJetty() {
        jetty.stopServer();
        log.info("stop jetty");
    }

    public void openContainer() throws ContainerMountException {
        containerManager.openContainer();
    }

    public void closeContainer() throws ContainerMountException {
        containerManager.closeContainer();
    }

    private void generateUser() {
        User user = SecurityUtils.generateRandomUser();
        ContainerPreferences.getInstance().setUser(user);
    }

    private void findPort() {
        int port = SecurityUtils.getFreePort();
        System.out.println("find port: " + port);
        ContainerPreferences.getInstance().setJettyPort(port);
    }
}
