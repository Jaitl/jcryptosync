package com.jcryptosync.sync;

import com.google.gson.Gson;
import com.jcryptosync.data.ContainerPreferences;
import com.jcryptosync.data.MetaData;
import com.jcryptosync.data.SyncPreferences;
import com.jcryptosync.data.UserPreferences;
import com.jcryptosync.domain.ListCryptFiles;
import com.jcryptosync.domain.SecondClient;
import com.jcryptosync.domain.Token;
import com.jcryptosync.utils.SyncUtils;
import com.jcryptosync.vfs.webdav.CryptFile;
import com.jcryptosync.vfs.webdav.Folder;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOM;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@MTOM
@WebService(endpointInterface = "com.jcryptosync.sync.SyncFiles")
public class SyncServer implements SyncFiles {
    private static Logger log  = Logger.getLogger(SyncServer.class);

    @Resource
    WebServiceContext wsctx;

    @Override
    public String getSessionId(String clientId, String groupId) {
        String currentGroupId = SyncPreferences.getInstance().getGroupId();

        if(!currentGroupId.equals(groupId))
            return null;

        String sessionId = UUID.randomUUID().toString();
        SecondClient secondClient = new SecondClient();
        secondClient.setIdClient(clientId);
        secondClient.setIdSession(sessionId);
        SyncPreferences.getInstance().getClientMap().put(sessionId, secondClient);

        log.info(String.format("send sessionId: %s to %s", sessionId, clientId));

        return sessionId;
    }

    @Override
    public Token authentication(String sessionId, byte[] sessionDigest, int remotePort) {

        Map<String, SecondClient> clientMap = SyncPreferences.getInstance().getClientMap();

        if(clientMap.containsKey(sessionId)) {
            if (SyncUtils.verifySessionDigest(sessionId, sessionDigest)) {
                String secondClientId = clientMap.get(sessionId).getIdClient();

                log.info(String.format("authentication with %s, sessionId: %s", secondClientId, sessionId));

                Token token = SyncUtils.generateToken(secondClientId, sessionId);

                setSecondClientData(sessionId, token, remotePort);

                return token;
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

            db.getCollectionFiles().forEach(listCryptFiles::addToList);

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
    public void updateFile(CryptFile file, String rootId) {
        log.info("update file: " + file);

        if(verifyToken()) {
            Token token = getToken();
            String sessionId = token.getSessionId();
            SecondClient secondClient = SyncPreferences.getInstance().getClientMap().get(sessionId);

            SyncPreferences.getInstance().getSyncClient().synchronizeFile(secondClient, file, rootId);
        }
    }

    @Override
    public void updateFolder(Folder folder, String rootId) {
        log.info("update folder: " + folder);

        if(verifyToken()) {
            SyncPreferences.getInstance().getSyncClient().synchronizeFolder(folder, rootId);
        }
    }

    @Override
    public void updateFiles(ListCryptFiles files) {
        log.info("update files");

        if(verifyToken()) {

            Token token = getToken();
            String sessionId = token.getSessionId();
            SecondClient secondClient = SyncPreferences.getInstance().getClientMap().get(sessionId);

            SyncPreferences.getInstance().getSyncClient().syncAllFiles(files, secondClient);
        }
    }

    @Override
    public void fileIsSynced(CryptFile file) {
        if(verifyToken()) {
            MetaData metaData = MetaData.getInstance();

            CryptFile cryptFile = (CryptFile) metaData.getFileById(file.getUniqueId());
            cryptFile.getVector().increaseSynchronization(ContainerPreferences.getInstance().getClientId());
            metaData.updateFile(cryptFile);
        }
    }

    @Override
    public String test(String name) {
        return String.format("Hello, %s! I'm JCryptoSync", name);
    }

    private Token getToken() {
        MessageContext mctx = wsctx.getMessageContext();

        Map http_headers = (Map) mctx.get(MessageContext.HTTP_REQUEST_HEADERS);
        List tokenLing = (List) http_headers.get("token");

        if(tokenLing == null)
            return null;

        String jsonToken = tokenLing.get(0).toString();

        Gson gson = new Gson();
        return gson.fromJson(jsonToken, Token.class);
    }

    private boolean verifyToken() {

        Token token = getToken();
        if(token == null)
            return false;

        Map<String, SecondClient> clientMap = SyncPreferences.getInstance().getClientMap();
        return SyncUtils.verifyToken(token, clientMap);
    }

    private void setSecondClientData(String sessionId, Token token, int remotePort) {
        MessageContext mc = wsctx.getMessageContext();
        HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);

        String remoteHost = req.getRemoteHost();

        SecondClient client = SyncPreferences.getInstance().getClientMap().get(sessionId);
        client.setHost(remoteHost);
        client.setPort(remotePort);
        client.setToken(token);
        SyncClient syncClient = SyncPreferences.getInstance().getSyncClient();
        client.setSyncFilesService(syncClient.connectToClient(client));
        syncClient.addTokenToHeader(client, token);
    }
}
