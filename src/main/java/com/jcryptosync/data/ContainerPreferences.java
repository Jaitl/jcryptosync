package com.jcryptosync.data;

import com.jcryptosync.domain.User;

import java.util.HashMap;
import java.util.Map;

public class ContainerPreferences {
    private static ContainerPreferences instance = new ContainerPreferences();

    private Map<String, Object> settings = new HashMap<>();

    private static final String JETTY_PORT = "jetty-port";
    private static final String USER = "user";
    private static final String CONTAINER_NAME = "container-name";

    public static ContainerPreferences getInstance() {
        return instance;
    }

    private ContainerPreferences() {

    }

    public void setJettyPort(int port) {
        settings.put(JETTY_PORT, port);
    }

    public int getJettyPort() {
        return (int) settings.get(JETTY_PORT);
    }

    public void setUser(User user) {
        settings.put(USER, user);
    }

    public User getUser() {
        return (User) settings.get(USER);
    }

    public void setContainerName(String name) {
        settings.put(CONTAINER_NAME, name);
    }

    public String getContainerName() {
        return (String) settings.get(CONTAINER_NAME);
    }
}
