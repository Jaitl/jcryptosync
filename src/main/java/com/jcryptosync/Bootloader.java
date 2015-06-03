package com.jcryptosync;

import com.jcryptosync.data.preferences.ContainerPreferences;
import com.jcryptosync.data.preferences.SyncPreferences;
import com.jcryptosync.data.preferences.UserPreferences;
import com.jcryptosync.domain.User;
import com.jcryptosync.exceptoins.ContainerMountException;
import com.jcryptosync.sync.AsyncAction;
import com.jcryptosync.sync.SyncClient;
import com.jcryptosync.ui.container.MessageService;
import com.jcryptosync.utils.SyncUtils;
import com.jcryptosync.vfs.manager.VFSManager;
import io.milton.common.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributes;
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
            loadWebAppFiles(getClass().getClassLoader());
            containerManager = VFSManager.createManager();
            SyncPreferences.getInstance().setVfsManager(containerManager);
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

    private void generateUser() {
        User user = SyncUtils.generateRandomUser();
        containerPreferences.setUser(user);
    }

    private void findPort() {
        int port = SyncUtils.getFreePort();
        containerPreferences.setJettyPort(port);
    }

    private void generateContainerName() {
        String containerName = System.getProperty("user.name");
        String soil[] = UUID.randomUUID().toString().split("-");
        containerName += "-" + soil[0];
        containerPreferences.setContainerName(containerName);
    }

    private void generateIdClient() {
        if(containerPreferences.getClientId() == null) {
            String idClient = UUID.randomUUID().toString();
            containerPreferences.setClientId(idClient);
        }
    }

    private void loadWebAppFiles(ClassLoader classLoader) {

        Path pathToWebApp = UserPreferences.getPathToContainer().getParent().resolve(".webapp");

        if(Files.notExists(pathToWebApp)) {
            try {
                Files.createDirectory(pathToWebApp);

                if(System.getProperty("os.name").toLowerCase().contains("win")) {
                    Files.setAttribute(pathToWebApp, "dos:hidden", true);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Path pathToWebInf = pathToWebApp.resolve("WEB-INF");

        if(Files.notExists(pathToWebInf)) {
            try {
                Files.createDirectory(pathToWebInf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Path pathToWebXml = pathToWebInf.resolve("web.xml");

        if(Files.notExists(pathToWebXml)) {
            InputStream is = classLoader.getResourceAsStream("webapp/WEB-INF/web.xml");
            try {
                Files.copy(is, pathToWebXml);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Path pathToSunXml = pathToWebInf.resolve("sun-jaxws.xml");

        if(Files.notExists(pathToSunXml)) {
            InputStream is = classLoader.getResourceAsStream("webapp/WEB-INF/sun-jaxws.xml");
            try {
                Files.copy(is, pathToSunXml);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
