package com.jcryptosync.sync;

import com.jcryptosync.UserPreferences;
import com.jcryptosync.container.webdav.CryptFile;
import com.jcryptosync.container.webdav.DataBase;
import com.jcryptosync.container.webdav.ListCryptFiles;
import com.jcryptosync.sync.utils.SyncUtils;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@MTOM
@WebService(endpointInterface = "com.jcryptosync.sync.SyncFiles")
public class SyncFilesImpl implements SyncFiles {
    private static Logger log  = Logger.getLogger(SyncFilesImpl.class);
    private Map<String, String> sessionsMap = new HashMap<>();

    @Override
    public String getSessionId(String clientId, String groupId) {
        String currentGroupId = SyncPreferences.getInstance().getGroupId();

        if(!currentGroupId.equals(groupId))
            return null;

        String sessionId = UUID.randomUUID().toString();
        sessionsMap.put(sessionId, clientId);

        return sessionId;
    }

    @Override
    public Token authentication(String sessionId, byte[] sessionDigest) {

        if(sessionsMap.containsKey(sessionId)) {
            if (SyncUtils.verifySessionDigest(sessionId, sessionDigest)) {
                String secondClientId = sessionsMap.get(sessionId);

                return SyncUtils.generateToken(secondClientId, sessionsMap.get(sessionId));
            }
        }

        return null;
    }


    @Override
    public ListCryptFiles getAllFiles() {
        ListCryptFiles listCryptFiles = new ListCryptFiles();
        DataBase db = DataBase.getInstance();

        listCryptFiles.setRootId(db.getRootFolderId());

        db.getFileMetadata().values().forEach(listCryptFiles::addToList);

        return listCryptFiles;
    }

    @Override
    public DataHandler getFile(CryptFile file) {
        log.info("get file: " + file.getUniqueId());

        Path filePath = UserPreferences.getPathToCryptDir().resolve(file.getUniqueId());
        FileDataSource dataSource = new FileDataSource(filePath.toFile());

        return new DataHandler(dataSource);
    }

    @Override
    public String test(String name) {
        return String.format("Hello, %s! I'm JCryptoSync", name);
    }
}
