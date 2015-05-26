package com.jcryptosync.sync;

import com.jcryptosync.domain.ListCryptFiles;
import com.jcryptosync.domain.Token;
import com.jcryptosync.vfs.webdav.CryptFile;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SyncFiles {

    @WebMethod
    String getSessionId(String clientId, String groupId);

    Token authentication(String sessionId, byte[] sessionDigest);

    @WebMethod
    ListCryptFiles getAllFiles();

    @WebMethod
    DataHandler getFile(CryptFile file);

    @WebMethod
    String test(String name);
}
