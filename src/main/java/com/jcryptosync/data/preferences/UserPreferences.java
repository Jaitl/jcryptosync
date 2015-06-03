package com.jcryptosync.data.preferences;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserPreferences {
    private static Preferences preferences = new Preferences();

    private static String PATH_TO_KEY = "path-to-key";
    private static String PATH_TO_CONTAINER = "path-to-container";
    private static String START_PORT = "start-port";
    private static String END_PORT = "end-port";
    private static String HARD_PASSWORD = "hard-password";
    private static String CLIENT_ADDRESS = "client address";

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

    public static Path getPathToCryptDir() {
        return getPathToDir("Crypt");
    }

    public static int getStartPort() {
        return Integer.parseInt(preferences.get(START_PORT, "34580"));
    }

    public static int getEndPort() {
        return Integer.parseInt(preferences.get(END_PORT, "34600"));
    }

    public static void setStartPort(String port) {
        preferences.put(START_PORT, port);
    }

    public static void setEndPort(String port) {
        preferences.put(END_PORT, port);
    }

    public static boolean isHardPassword() {
        return Boolean.parseBoolean(preferences.get(HARD_PASSWORD, "TRUE"));
    }

    public static void setHardPassword(boolean hardPassword) {
        preferences.put(HARD_PASSWORD, Boolean.toString(hardPassword));
    }

    public static String getClientAddress() {
        return preferences.get(CLIENT_ADDRESS, "");
    }

    public static void setClientAddress(String clientAddress) {
        preferences.put(CLIENT_ADDRESS, clientAddress);
    }
}
