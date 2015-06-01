package com.jcryptosync;

import com.jcryptosync.data.preferences.ContainerPreferences;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

public class Jetty {
    private Server server;

    private static final String webappDirLocation = "src/main/webapp/";

    public Jetty() {

        int port = ContainerPreferences.getInstance().getJettyPort();

        server = new Server(port);
        server.setStopAtShutdown(true);
        WebAppContext root = new WebAppContext();

        root.setContextPath("/");
        File f = new File(webappDirLocation);
        root.setWar(f.getAbsolutePath());

        server.setHandler(root);
    }

    public void startServer() {
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
