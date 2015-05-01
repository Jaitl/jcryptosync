package com.jcryptosync.utils;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.exceptoins.ContainerMountException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ContainerUtils {

    private static String pathToMount = "/media/Files";

    public static void linuxOpenContainer(String path) throws ContainerMountException {
        Runtime rt = Runtime.getRuntime();
        Process pr = null;

        try {
            pr = rt.exec(String.format("bindfs -n %s %s", path, pathToMount));
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

    public static void linuxCloseContainer() throws ContainerMountException {
        Runtime rt = Runtime.getRuntime();
        Process pr = null;

        try {
            pr = rt.exec(String.format("fusermount -u %s", pathToMount));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int code = pr.waitFor();
            if(code != 0)
                throw new ContainerMountException("Не удалось отключть контейнер");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void clearFiles() {
        Path filesDir = QuickPreferences.getPathToFilesDir();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(filesDir)) {
            stream.forEach(p -> {
                try {
                    if(!Files.isDirectory(p))
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
