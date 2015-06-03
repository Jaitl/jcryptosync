package com.jcryptosync.utils;

import com.jcryptosync.preferences.UserPreferences;
import com.jcryptosync.domain.User;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

public class SyncUtils {

    public static String generateName(String oldName, Date dateMod) {
        String date = TokenUtils.formatter.format(dateMod);
        String newName;

        int indexDot = oldName.lastIndexOf('.');

        if(indexDot > 0) {
            newName = oldName.substring(0, indexDot) + "(" + date + ")." + oldName.substring(indexDot + 1, oldName.length());
        } else {
            newName = oldName + "(" + date + ")";
        }

        return newName;
    }

    public static User generateRandomUser() {
        String name = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();

        return new User(name, password);
    }

    public static int getFreePort() {
        int port = UserPreferences.getStartPort();
        int end = UserPreferences.getEndPort();

        for(; port < end; port++) {
            if(!portIsOpen("localhost", port))
                break;
        }

        return port;
    }

    public static boolean portIsOpen(String host, int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 200);
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
