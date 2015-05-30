package com.jcryptosync.utils;

import com.jcryptosync.data.ContainerPreferences;
import com.jcryptosync.data.SyncPreferences;
import com.jcryptosync.domain.SecondClient;
import com.jcryptosync.domain.Token;
import org.apache.commons.lang.time.DateUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class SyncUtils {
    public static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

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
}
