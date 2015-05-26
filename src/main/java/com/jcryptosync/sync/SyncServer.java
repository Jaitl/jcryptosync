package com.jcryptosync.sync;

import com.google.gson.Gson;
import com.jcryptosync.data.MetaData;
import com.jcryptosync.data.SyncPreferences;
import com.jcryptosync.data.UserPreferences;
import com.jcryptosync.domain.ListCryptFiles;
import com.jcryptosync.domain.Token;
import com.jcryptosync.utils.SyncUtils;
import com.jcryptosync.vfs.webdav.CryptFile;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOM;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@MTOM
@WebService(endpointInterface = "com.jcryptosync.sync.SyncFiles")
public class SyncServer implements SyncFiles {
    private static Logger log  = Logger.getLogger(SyncServer.class);
    private Map<String, String> sessionsMap = new HashMap<>();

    @Resource
    WebServiceContext wsctx;

    @Override
    public String getSessionId(String clientId, String groupId) {
        String currentGroupId = SyncPreferences.getInstance().getGroupId();

        if(!currentGroupId.equals(groupId))
            return null;

        String sessionId = UUID.randomUUID().toString();
        sessionsMap.put(sessionId, clientId);

        log.info(String.format("send sessionId: %s to %s", sessionId, clientId));

        return sessionId;
    }

    @Override
    public Token authentication(String sessionId, byte[] sessionDigest) {

        if(sessionsMap.containsKey(sessionId)) {
            if (SyncUtils.verifySessionDigest(sessionId, sessionDigest)) {
                String secondClientId = sessionsMap.get(sessionId);

                log.info(String.format("authentication with %s, sessionId: %s", secondClientId, sessionId));

                return SyncUtils.generateToken(secondClientId, sessionId);
            }
        }

        return null;
    }


    @Override
    public ListCryptFiles getAllFiles() {

        if(verifyToken()) {
            ListCryptFiles listCryptFiles = new ListCryptFiles();
            MetaData db = MetaData.getInstance();

            listCryptFiles.setRootId(db.getRootFolderId());

            db.getFileMetadata().values().forEach(listCryptFiles::addToList);

            return listCryptFiles;
        }

        return null;
    }

    @Override
    public DataHandler getFile(CryptFile file) {

        if(verifyToken()) {
            log.info("get file: " + file.getUniqueId());

            Path filePath = UserPreferences.getPathToCryptDir().resolve(file.getUniqueId());
            FileDataSource dataSource = new FileDataSource(filePath.toFile());

            return new DataHandler(dataSource);

        }

        return null;
    }

    @Override
    public String test(String name) {
        return String.format("Hello, %s! I'm JCryptoSync", name);
    }

    private boolean verifyToken() {

        MessageContext mctx = wsctx.getMessageContext();

        Map http_headers = (Map) mctx.get(MessageContext.HTTP_REQUEST_HEADERS);
        List tokenLing = (List) http_headers.get("token");

        if(tokenLing == null)
            return false;

        String jsonToken = tokenLing.get(0).toString();

        Gson gson = new Gson();
        Token token = gson.fromJson(jsonToken, Token.class);

        return SyncUtils.verifyToken(token, sessionsMap);
    }
}
