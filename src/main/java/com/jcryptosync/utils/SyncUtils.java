package com.jcryptosync.utils;

import com.jcryptosync.data.ContainerPreferences;
import com.jcryptosync.data.SyncPreferences;
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
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

    public static boolean verifyToken(Token token, Map<String, String> sessions) {
        byte[] digest = computeTokenDigest(token);

        // Проверка подписи
        if(!Arrays.equals(digest, token.getDigest()))
            return false;

        String currentClientId = ContainerPreferences.getInstance().getClientId();

        // Проверка id текущего клиента
        if(!token.getFirstClientId().equals(currentClientId))
            return false;

        // Проверка сессии
        if(!sessions.containsKey(token.getSessionId()))
            return false;

        String clientId = sessions.get(token.getSessionId());

        // Проверка id второго клиента
        if(!clientId.equals(token.getSecondClientId()))
            return false;

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

    //Hex.encodeHexString()
}
