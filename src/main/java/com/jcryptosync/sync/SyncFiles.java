package com.jcryptosync.sync;

import com.jcryptosync.domain.ListCryptFiles;
import com.jcryptosync.domain.Token;
import com.jcryptosync.vfs.webdav.CryptFile;
import com.jcryptosync.vfs.webdav.Folder;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SyncFiles {

    String getSessionId(String clientId, String groupId);

    Token authentication(String sessionId, byte[] sessionDigest, int remotePort);

    ListCryptFiles getAllFiles();

    DataHandler getFile(CryptFile file);

    void updateFile(CryptFile file, String rootId);

    void updateFolder(Folder folder, String rootId);

    void updateFiles(ListCryptFiles files);

    void fileIsSynced(CryptFile file);

    String test(String name);
}
