package com.jcryptosync;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class UserPreferences {
    private static Preferences preferences = new Preferences();

    private static String PATH_TO_KEY = "path-to-key";
    private static String PATH_TO_CONTAINER = "path-to-container";
    private static String START_PORT = "start-port";
    private static String END_PORT = "end-port";

    public static void setPathToKey(String path) {
        preferences.put(PATH_TO_KEY, path);
    }

    public static Path getPathToKey() {
        return Paths.get(preferences.get(PATH_TO_KEY, ""));
    }

    public static void setPathToContainer(String path) {
        preferences.put(PATH_TO_CONTAINER, path);
    }

    public static Path getPathToContainer() {
        return Paths.get(preferences.get(PATH_TO_CONTAINER, ""));
    }

    private static Path getPathToDir(String dir) {
        Path pathToContainer = getPathToContainer();
        Path pathToRoot = pathToContainer.getParent();

        Path path = pathToRoot.resolve(dir);

        if(Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return path;
    }

    public static Path getPathToFilesDir() {
        return getPathToDir("Files");
    }

    public static Path getPathToCryptDir() {
        return getPathToDir("Crypt");
    }

    public static Path getPathToWorkDir() {
        return getPathToContainer().getParent();
    }

    public static int getStartPort() {
        return Integer.parseInt(preferences.get(START_PORT, "34580"));
    }

    public static int getEndPort() {
        return Integer.parseInt(preferences.get(END_PORT, "34600"));
    }
}
