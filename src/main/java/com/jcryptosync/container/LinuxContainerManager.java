package com.jcryptosync.container;

import com.jcryptosync.container.exceptoins.ContainerMountException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LinuxContainerManager extends ContainerManager {

    private String nameMountFolder = "cryptFiles";

    private Path pathToMountFolder;


    public LinuxContainerManager() {
        Path home = Paths.get(System.getProperty("user.home"));
        pathToMountFolder = home.resolve(nameMountFolder);
    }

    @Override
    public boolean isMount() {
        return Files.exists(pathToMountFolder);
    }

    @Override
    public void mountContainer() throws ContainerMountException {
        Runtime rt = Runtime.getRuntime();
        Process pr = null;

        try {
            Files.createDirectory(pathToMountFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            pr = rt.exec(String.format("wdfs %s %s -o username=%s -o password=%s", pathToWebDavServer, pathToMountFolder, user, password));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int code = pr.waitFor();
            if(code != 0)
                throw new ContainerMountException("Не удалось подключить контейнер");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unmountContainer() throws ContainerMountException {
        Runtime rt = Runtime.getRuntime();
        Process pr = null;

        try {
            pr = rt.exec(String.format("fusermount -u %s", pathToMountFolder));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int code = pr.waitFor();
            if(code != 0)
                throw new ContainerMountException("Не удалось отключть контейнер");

            Files.delete(pathToMountFolder);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
