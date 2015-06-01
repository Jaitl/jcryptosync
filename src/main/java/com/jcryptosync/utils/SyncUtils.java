package com.jcryptosync.utils;

import com.jcryptosync.data.preferences.ContainerPreferences;
import com.jcryptosync.data.preferences.SyncPreferences;
import com.jcryptosync.data.preferences.UserPreferences;
import com.jcryptosync.domain.SecondClient;
import com.jcryptosync.domain.Token;
import com.jcryptosync.domain.User;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class SyncUtils {
    public static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH-mm-ss");

    public static boolean verifySessionDigest(String sessionId, byte[] sessionDigest) {
        byte[] digest = generateSessionDigest(sessionId);

        if(digest != null) {
            return Arrays.equals(digest, sessionDigest);
        }

        return false;
    }

    public static Token generateToken(String secondClientId, String sessionId) {
        String clientId = ContainerPreferences.getInstance().getClientId();

        Token token = new Token();
        token.setFirstClientId(clientId);
        token.setSecondClientId(secondClientId);
        token.setSessionId(sessionId);

        String date = formatter.format(new Date());
        token.setDateCreate(date);
        token.setLifeHours(48);

        byte[] digest = computeTokenDigest(token);
        token.setDigest(digest);

        return token;
    }

    public static byte[] generateSessionDigest(String sessionId) {
        byte[] key = SyncPreferences.getInstance().getKey();
        Mac sha256_HMAC = null;

        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
            sha256_HMAC.init(secret_key);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return sha256_HMAC.doFinal(sessionId.getBytes());
    }

    public static byte[] computeTokenDigest(Token token) {
        String tokenString = "%" + token.getFirstClientId() + ":"
                + token.getSecondClientId() + ":"
                + token.getSessionId() + ":"
                + token.getDateCreate() + ":"
                + token.getLifeHours() + "%";

        return generateSessionDigest(tokenString);
    }

    public static boolean verifyToken(Token token, Map<String, SecondClient> clientMap) {
        byte[] digest = computeTokenDigest(token);

        // Проверка подписи
        if(!Arrays.equals(digest, token.getDigest()))
            return false;

        // Проверка сессии
        if(!clientMap.containsKey(token.getSessionId()))
            return false;

        String currentClientId = ContainerPreferences.getInstance().getClientId();
        String secondClientId = clientMap.get(token.getSessionId()).getIdClient();

        if(token.getFirstClientId().equals(currentClientId)) {
            if(!token.getSecondClientId().equals(secondClientId))
                return false;
        } else if(token.getSecondClientId().equals(currentClientId)) {
            if(!token.getFirstClientId().equals(secondClientId))
                return false;
        } else {
            return false;
        }

        Date dateCreate = null;

        try {
            dateCreate = formatter.parse(token.getDateCreate());
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        Date dateEnd = DateUtils.addHours(dateCreate, token.getLifeHours());

        Date currentDate = new Date();

        // Проверка срока действия токена
        return currentDate.before(dateEnd);


    }

    public static String generateName(String oldName, Date dateMod) {
        String date = formatter.format(dateMod);
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

    public static byte[] computeSHA256(byte[] data) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return messageDigest.digest(data);
    }

    public static byte[] computeKey(String password, byte[] masterkey) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] bytePass = messageDigest.digest(password.getBytes());

        byte[] unionPass = ArrayUtils.addAll(bytePass, masterkey);

        return computeSHA256(unionPass);
    }

    public static String computeGroupId(byte[] masterKey) {
        byte[] hash = computeSHA256(masterKey);

        return "group-" + Hex.encodeHexString(hash);
    }
}
