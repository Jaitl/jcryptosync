package com.jcryptosync.sync;

import com.google.gson.Gson;
import com.jcryptosync.UserPreferences;
import com.jcryptosync.container.ContainerPreferences;
import com.jcryptosync.container.utils.SecurityUtils;
import com.jcryptosync.container.webdav.CryptFile;
import com.jcryptosync.container.webdav.DataBase;
import com.jcryptosync.container.webdav.Folder;
import com.jcryptosync.container.webdav.ListCryptFiles;
import com.jcryptosync.sync.client.SecondClient;
import com.jcryptosync.sync.utils.SyncUtils;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.crypto.SecretKey;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class Syncronizer {
    private static Logger log = Logger.getLogger(Syncronizer.class);

    private List<SecondClient> clientList = new ArrayList<>();

    public void runFirstSync() {
        findClients();
        authentication();
        requestFullFileList();
    }

    public void findClients() {
        int startPort = UserPreferences.getStartPort();
        int endPort = UserPreferences.getEndPort();

        int currentPort = ContainerPreferences.getInstance().getJettyPort();

        for(; startPort <= endPort; startPort++) {
            if(currentPort != startPort) {
                if (SecurityUtils.portIsOpen(startPort)) {
                    clientList.add(new SecondClient("localhost", startPort));

                    log.info("found client, port: " + startPort);
                }
            }
        }
    }

    public void authentication() {
        String clientId = SyncPreferences.getInstance().getClientId();
        String groupId = SyncPreferences.getInstance().getGroupId();

        SecondClient[] clientArray = clientList.toArray(new SecondClient[clientList.size()]);

        for(int i = 0; i < clientArray.length; i++) {
            SecondClient client = clientArray[i];
            client.syncFilesService = connectToClient(client);

            String sessionId = client.getSyncFilesService().getSessionId(clientId, groupId);

            log.info(String.format("get sessionID: %s", sessionId));

            if(sessionId == null) {
                clientList.remove(client);
                break;
            }

            byte[] sessionDigest = SyncUtils.generateSessionDigest(sessionId);

            Token token = client.getSyncFilesService().authentication(sessionId, sessionDigest);
            if(token == null) {
                clientList.remove(client);
                break;
            }

            client.setToken(token);
            addTokenToHeader(client, token);

            log.info(String.format("get token from client: %s", token.getFirstClientId()));
        }
    }

    public void requestFullFileList() {
        if(clientList.size() > 0) {
            SecondClient client = clientList.get(0);

            ListCryptFiles listCryptFiles = client.syncFilesService.getAllFiles();
            listCryptFiles.getFileList().forEach(f -> {
                if(f instanceof CryptFile)
                    synchronizeFile(client, (CryptFile) f, listCryptFiles.getRootId());
                else
                    synchronizeFolder((Folder) f, listCryptFiles.getRootId());
            });
        }

    }

    public void addTokenToHeader(SecondClient client, Token token) {
        Map<String, Object> req_ctx = ((BindingProvider)client.getSyncFilesService()).getRequestContext();

        String WS_URL = String.format("http://%s:%s/api/SyncFiles?wsdl", client.getHost(), client.getPort());

        req_ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, WS_URL);

        Gson gson = new Gson();
        String jsonToken = gson.toJson(token);

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("token", Collections.singletonList(jsonToken));
        req_ctx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
    }

    private SyncFiles connectToClient(SecondClient client) {
        URL url = null;

        try {
            url = new URL(String.format("http://%s:%s/api/SyncFiles?wsdl", client.getHost(), client.getPort()));
        } catch (MalformedURLException e) {
            log.error("error create url", e);
        }

        QName qname = new QName("http://sync.jcryptosync.com/", "SyncFilesImplService");

        Service service = Service.create(url, qname);

        return service.getPort(SyncFiles.class);
    }

    public void synchronizeFolder(Folder folder, String rootId) {
        if (folder.getUniqueId().equals(rootId))
            return;

        DataBase db = DataBase.getInstance();

        Folder oldFolder = (Folder) db.getFileMetadata().get(folder.getUniqueId());
        if(oldFolder != null)
            return;

        if(folder.getParentId().equals(rootId)) {
            folder.setParentId(db.getRootFolderId());
        }

        db.getFileMetadata().put(folder.getUniqueId(), folder);
        db.save();

    }

    public void synchronizeFile(SecondClient client, CryptFile file, String rootId) {
        DataBase db = DataBase.getInstance();

        CryptFile oldFile = (CryptFile) db.getFileMetadata().get(file.getUniqueId());
        if(oldFile != null)
            return;

        if(file.getParentId().equals(rootId)) {
            file.setParentId(db.getRootFolderId());
        }

        loadFile(client, file);

        db.getFileMetadata().put(file.getUniqueId(), file);
        db.save();
    }

    public void loadFile(SecondClient client, CryptFile file) {

        DataHandler dataHandler = client.syncFilesService.getFile(file);

        Path pathToCryptFile = UserPreferences.getPathToCryptDir().resolve(file.getUniqueId());

        try (FileOutputStream os = new FileOutputStream(pathToCryptFile.toFile())) {
            dataHandler.writeTo(os);
        } catch (IOException e) {
            log.error("error load file", e);
        }
    }
}
