package com.jcryptosync.container;

import com.jcryptosync.container.exceptoins.ContainerMountException;
import com.jcryptosync.container.utils.SecurityUtils;
import com.jcryptosync.container.webdav.User;
import com.jcryptosync.sync.SyncPreferences;
import com.jcryptosync.sync.Syncronizer;

import java.util.UUID;

public class Bootstrap {
    private ContainerManager containerManager;
    protected static transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Bootstrap.class);
    private Jetty jetty;
    private ContainerPreferences containerPreferences;

    private Syncronizer syncronizer;

    public void runApplication() {
        containerPreferences = ContainerPreferences.getInstance();
        findPort();
        generateUser();
        generateContainerName();
        containerManager = ContainerManager.createManager();
        jetty = new Jetty();
        runJetty();generateIdClient();

        syncronizer = new Syncronizer();

        Runnable runnable = () -> {
            syncronizer.runFirstSync();
        };

        new Thread(runnable).start();
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
        containerPreferences.setUser(user);
    }

    private void findPort() {
        int port = SecurityUtils.getFreePort();
        System.out.println("find port: " + port);
        containerPreferences.setJettyPort(port);
    }

    private void generateContainerName() {
        String containerName = System.getProperty("user.name");
        String soil[] = UUID.randomUUID().toString().split("-");
        containerName += "-" + soil[0];
        containerPreferences.setContainerName(containerName);
    }

    private void generateIdClient() {
        String idClient = UUID.randomUUID().toString();

        SyncPreferences.getInstance().setClientId(idClient);
    }
}
