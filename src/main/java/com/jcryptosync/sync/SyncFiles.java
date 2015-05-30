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

    @WebMethod
    String getSessionId(String clientId, String groupId);

    Token authentication(String sessionId, byte[] sessionDigest, int remotePort);

    @WebMethod
    ListCryptFiles getAllFiles();

    @WebMethod
    DataHandler getFile(CryptFile file);

    @WebMethod
    void updateFile(CryptFile file, String rootId);

    @WebMethod
    void updateFolder(Folder folder, String rootId);

    @WebMethod
    void updateFiles(ListCryptFiles files);

    @WebMethod
    void fileIsSynced(CryptFile file);

    @WebMethod
    String test(String name);
}
