package com.jcryptosync;

import com.jcryptosync.data.preferences.ContainerPreferences;
import com.jcryptosync.data.preferences.SyncPreferences;
import com.jcryptosync.domain.User;
import com.jcryptosync.exceptoins.ContainerMountException;
import com.jcryptosync.sync.AsyncAction;
import com.jcryptosync.sync.SyncClient;
import com.jcryptosync.ui.container.MessageService;
import com.jcryptosync.utils.SyncUtils;
import com.jcryptosync.vfs.manager.VFSManager;

import java.util.UUID;

public class Bootloader {
    private VFSManager containerManager;
    protected static transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Bootloader.class);
    private Jetty jetty;
    private ContainerPreferences containerPreferences;

    private SyncClient syncronizer;

    public void runApplication() {
        new AsyncAction().executeAction(() -> {
            containerPreferences = ContainerPreferences.getInstance();
            findPort();
            generateUser();
            generateContainerName();
            containerManager = VFSManager.createManager();
            jetty = new Jetty();
            runJetty();
            generateIdClient();

            try {
                openContainer();
                MessageService.showMessage("сетевой диск подключен");
            } catch (ContainerMountException e) {
                log.error("mount  error", e);
                MessageService.showMessage(e.getMessage());
            }

            syncronizer = new SyncClient();
            syncronizer.runFirstSync();
        });
    }


    public void stopApplication() throws ContainerMountException {
        containerManager.closeContainer();
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
        User user = SyncUtils.generateRandomUser();
        containerPreferences.setUser(user);
    }

    private void findPort() {
        int port = SyncUtils.getFreePort();
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
        //if(containerPreferences.getClientId() == null) {
            String idClient = UUID.randomUUID().toString();
            containerPreferences.setClientId(idClient);
        //}
    }
}
