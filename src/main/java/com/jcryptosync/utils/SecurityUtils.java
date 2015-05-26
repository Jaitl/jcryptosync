package com.jcryptosync.utils;

import com.jcryptosync.data.UserPreferences;
import com.jcryptosync.domain.User;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

public class SecurityUtils {
    public static User generateRandomUser() {
        String name = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();

        return new User(name, password);
    }

    public static int getFreePort() {
        int port = UserPreferences.getStartPort();
        int end = UserPreferences.getEndPort();

        for(; port < end; port++) {
            if(!portIsOpen(port))
                break;
        }

        return port;
    }

    public static boolean portIsOpen(int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", port), 200);
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
