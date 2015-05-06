package com.jcryptosync.container.utils;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.exceptoins.ContainerMountException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContainerUtils {

    private static final String user = "user";
    private static final String password = "password";

    public static void linuxOpenContainer(String pathToWebDav, String mountName) throws ContainerMountException {
        Runtime rt = Runtime.getRuntime();
        Process pr = null;
        QuickPreferences.getPathToFilesDir();
        Path home = Paths.get(System.getProperty("user.home"));
        Path pathToFolder = home.resolve(mountName);

        if(Files.notExists(pathToFolder)) {
            try {
                Files.createDirectory(pathToFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            pr = rt.exec(String.format("wdfs %s %s -o username=%s -o password=%s", pathToWebDav, pathToFolder, user, password));
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

    public static void linuxCloseContainer(String mountName) throws ContainerMountException {
        Runtime rt = Runtime.getRuntime();
        Process pr = null;

        Path home = Paths.get(System.getProperty("user.home"));
        Path pathToFolder = home.resolve(mountName);

        try {
            pr = rt.exec(String.format("fusermount -u %s", pathToFolder));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int code = pr.waitFor();
            if(code != 0)
                throw new ContainerMountException("Не удалось отключть контейнер");

            Files.delete(pathToFolder);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearFiles() {
        Path filesDir = QuickPreferences.getPathToFilesDir();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(filesDir)) {
            stream.forEach(p -> {
                try {
                    if (!Files.isDirectory(p))
                        Files.delete(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
