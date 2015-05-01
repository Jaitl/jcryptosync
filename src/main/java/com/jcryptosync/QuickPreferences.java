package com.jcryptosync;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QuickPreferences {
    private static Preferences preferences = new Preferences();

    private static String PATH_TO_KEY = "path-to-key";
    private static String PATH_TO_CONTAINER = "path-to-container";

    public static void setPathToKey(String path) {
        preferences.put(PATH_TO_KEY, path);
    }

    public static Path getPathToKey() {
        return Paths.get(preferences.get(PATH_TO_KEY, null));
    }

    public static void setPathToContainer(String path) {
        preferences.put(PATH_TO_CONTAINER, path);
    }

    public static Path getPathToContainer() {
        return Paths.get(preferences.get(PATH_TO_CONTAINER, null));
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
}
