package com.jcryptosync;

public class QuickPreferences {
    private static Preferences preferences = new Preferences();

    private static String PATH_TO_KEY = "path-to-key";
    private static String PATH_TO_CONTAINER = "path-to-container";

    public static void setPathToKey(String path) {
        preferences.put(PATH_TO_KEY, path);
    }

    public static String getPathToKey() {
        return preferences.get(PATH_TO_KEY, null);
    }

    public static void setPathToContainer(String path) {
        preferences.put(PATH_TO_CONTAINER, path);
    }

    public static String getPathToContainer() {
        return preferences.get(PATH_TO_CONTAINER, null);
    }
}
