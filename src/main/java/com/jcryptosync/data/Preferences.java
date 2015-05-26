package com.jcryptosync.data;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Preferences {
    private static org.slf4j.Logger log = LoggerFactory.getLogger(Preferences.class);

    private final Properties properties = new Properties();

    public Preferences() {
        loadPropertiesFromFile();
    }

    public void put(String key, String value) {
        properties.setProperty(key, value);
        storePropertiesToFile();
    }

    public String get(String key, String defaultValue){
        return properties.getProperty(key, defaultValue);
    }

    private void loadPropertiesFromFile() {
        try {
            InputStream is = new FileInputStream(getPathToFile());

            properties.load(is);
        } catch (FileNotFoundException e) {
            log.error("error", e);
        } catch (IOException e) {
            log.error("error", e);
        }
    }

    private void storePropertiesToFile() {
        try {
            OutputStream os = new FileOutputStream(getPathToFile());
            properties.store(os, "");
        } catch (FileNotFoundException e) {
            log.error("error", e);
        } catch (IOException e) {
            log.error("error", e);
        }
    }

    private String getPathToFile() {
        File jar = null;

        try {
            jar = new File(Preferences.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            log.error("error", e);
        }

        Path mainPath = jar.getParentFile().toPath();
        Path pathToSettings = mainPath.resolve("settings");

        if(Files.notExists(pathToSettings)) {
            try {
                Files.createDirectory(pathToSettings);
            } catch (IOException e) {
                log.error("error", e);
            }
        }

        pathToSettings = pathToSettings.resolve("best.properties");

        if(Files.notExists(pathToSettings)) {
            try {
                Files.createFile(pathToSettings);
            } catch (IOException e) {
                log.error("error", e);
            }
        }

        return pathToSettings.toString();
    }
}