package com.jcryptosync.container;

import com.jcryptosync.container.webdav.DataBase;
import com.jcryptosync.container.webdav.User;

import java.util.HashMap;
import java.util.Map;

public class ContainerPreferences {
    private static ContainerPreferences instance = new ContainerPreferences();

    private Map<String, Object> settings = new HashMap<>();

    private static final String JETTY_PORT = "jetty-port";
    private static final String USER = "user";

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
}
