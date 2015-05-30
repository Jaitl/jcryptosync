package com.jcryptosync.sync;

import com.google.gson.Gson;
import com.jcryptosync.data.ContainerPreferences;
import com.jcryptosync.data.MetaData;
import com.jcryptosync.data.SyncPreferences;
import com.jcryptosync.data.UserPreferences;
import com.jcryptosync.domain.ListCryptFiles;
import com.jcryptosync.domain.SecondClient;
import com.jcryptosync.domain.Token;
import com.jcryptosync.utils.SecurityUtils;
import com.jcryptosync.utils.SyncUtils;
import com.jcryptosync.vfs.filesystem.CryptFileSystem;
import com.jcryptosync.vfs.filesystem.FileOperations;
import com.jcryptosync.vfs.webdav.AbstractFile;
import com.jcryptosync.vfs.webdav.CryptFile;
import com.jcryptosync.vfs.webdav.Folder;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
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

public class SyncClient implements CryptFileSystem.ChangeEvents {
    private static Logger log = Logger.getLogger(SyncClient.class);

    public void runFirstSync() {
        SyncPreferences.getInstance().setSyncClient(this);
        CryptFileSystem.getInstance().setChangeEvents(this);
        authentication();
        requestFullFileList();
    }

    public List<SecondClient> findClients() {
        List<SecondClient> clientList = new ArrayList<>();
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
        return clientList;
    }

    public void authentication() {
        List<SecondClient> clientList = findClients();

        String clientId = ContainerPreferences.getInstance().getClientId();
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

            int jettyPort = ContainerPreferences.getInstance().getJettyPort();

            Token token = client.getSyncFilesService().authentication(sessionId, sessionDigest, jettyPort);
            if(token == null) {
                clientList.remove(client);
                break;
            }

            client.setIdClient(token.getFirstClientId());
            client.setIdSession(token.getSessionId());

            client.setToken(token);
            addTokenToHeader(client, token);


            log.info(String.format("get token from client: %s", token.getFirstClientId()));
        }

        Map<String, SecondClient> clientMap = SyncPreferences.getInstance().getClientMap();

        clientList.forEach((c) -> {
            if(!clientMap.containsKey(c.getIdSession())) {
                clientMap.put(c.getIdSession(), c);
            }
        });
    }

    public void requestFullFileList() {
        ListCryptFiles localFiles = new ListCryptFiles();
        MetaData db = MetaData.getInstance();
        localFiles.setRootId(db.getRootFolderId());
        db.getFileMetadata().values().forEach(localFiles::addToList);

        Collection<SecondClient> clientList = SyncPreferences.getInstance().getClientMap().values();

        if(clientList.size() > 0) {
            for (SecondClient client : clientList) {

                ListCryptFiles listCryptFiles = client.syncFilesService.getAllFiles();
                listCryptFiles.getFileList().forEach(f -> {
                    if (f instanceof CryptFile)
                        synchronizeFile(client, (CryptFile) f, listCryptFiles.getRootId());
                    else
                        synchronizeFolder((Folder) f, listCryptFiles.getRootId());
                });

                Runnable runnable = () -> {
                    client.getSyncFilesService().updateFiles(localFiles);
                };

                new Thread(runnable).start();
            }
        }

    }

    public void syncAllFiles(ListCryptFiles listCryptFiles, SecondClient client) {
        listCryptFiles.getFileList().forEach(f -> {
            if (f instanceof CryptFile)
                synchronizeFile(client, (CryptFile) f, listCryptFiles.getRootId());
            else
                synchronizeFolder((Folder) f, listCryptFiles.getRootId());
        });
    }

    public void addTokenToHeader(SecondClient client, Token token) {
        Map<String, Object> req_ctx = ((BindingProvider)client.getSyncFilesService()).getRequestContext();

        String WS_URL = String.format("http://%s:%s/api/SyncServer?wsdl", client.getHost(), client.getPort());

        req_ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, WS_URL);

        Gson gson = new Gson();
        String jsonToken = gson.toJson(token);

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("token", Collections.singletonList(jsonToken));
        req_ctx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
    }

    public SyncFiles connectToClient(SecondClient client) {
        URL url = null;

        try {
            url = new URL(String.format("http://%s:%s/api/SyncServer?wsdl", client.getHost(), client.getPort()));
        } catch (MalformedURLException e) {
            log.error("error create url", e);
        }

        QName qname = new QName("http://sync.jcryptosync.com/", "SyncServerService");

        Service service = Service.create(url, qname);

        return service.getPort(SyncFiles.class);
    }

    public void synchronizeFolder(Folder folder, String rootId) {
        if (folder.getUniqueId().equals(rootId))
            return;

        MetaData db = MetaData.getInstance();

        if(folder.getParentId().equals(rootId)) {
            folder.setParentId(db.getRootFolderId());
        }

        Folder oldFolder = (Folder) db.getFileMetadata().get(folder.getUniqueId());
        if (oldFolder != null) {
            db.getFileMetadata().replace(folder.getUniqueId(), folder);
        } else {
            db.getFileMetadata().put(folder.getUniqueId(), folder);
        }
        db.save();
    }

    public void synchronizeFile(SecondClient client, CryptFile file, String rootId) {
        MetaData db = MetaData.getInstance();

        Map<String, AbstractFile> localFiles = db.getFileMetadata();

        CryptFile localFile = null;
        if(localFiles.containsKey(file.getUniqueId())) {
            localFile = (CryptFile) localFiles.get(file.getUniqueId());

            if(localFile.getVector().isChange(file.getVector())) {
                if(localFile.getVector().isConflict(file.getVector())) {
                    log.error("conflict, file: " + file.getUniqueId());
                    return;
                }
            } else {
                return;
            }
        }

        String currentId = ContainerPreferences.getInstance().getClientId();
        if(client.token.getFirstClientId().equals(currentId)) {
            file.getVector().increaseSynchronization(client.getToken().getSecondClientId());
        } else {
            file.getVector().increaseSynchronization(client.getToken().getFirstClientId());
        }

        if(file.getParentId().equals(rootId)) {
            file.setParentId(db.getRootFolderId());
        }

        if(!file.isDeleted()) {

            if (localFile != null) {
                if (!Arrays.equals(file.getHash(), localFile.getHash())) {
                    FileOperations.deleteFile(localFile);
                    loadFile(client, file);
                }
            } else {
                if(file.getLength() > 0) {
                    loadFile(client, file);
                } else {
                    return;
                }
            }
        } else {
            FileOperations.deleteFile(file);
        }

        if(localFile == null) {
            db.getFileMetadata().put(file.getUniqueId(), file);
        } else {
            db.getFileMetadata().replace(file.getUniqueId(), file);
        }

        db.save();

        client.getSyncFilesService().fileIsSynced(file);
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

    @Override
    public void changeFile(CryptFile file) {
        Runnable runnable = () -> {
            Collection<SecondClient> clientList = SyncPreferences.getInstance().getClientMap().values();
            String rootId = MetaData.getInstance().getRootFolderId();
            clientList.forEach((c) -> c.getSyncFilesService().updateFile(file, rootId));
        };

        new Thread(runnable).start();
    }

    @Override
    public void changeFolder(Folder folder) {

        Runnable runnable = () -> {
            Collection<SecondClient> clientList = SyncPreferences.getInstance().getClientMap().values();
            String rootId = MetaData.getInstance().getRootFolderId();
            clientList.forEach((c) -> c.getSyncFilesService().updateFolder(folder, rootId));
        };

        new Thread(runnable).start();
    }
}
