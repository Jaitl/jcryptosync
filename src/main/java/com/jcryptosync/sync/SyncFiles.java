package com.jcryptosync.sync;

import com.jcryptosync.container.webdav.AbstractFile;
import com.jcryptosync.container.webdav.CryptFile;
import com.jcryptosync.container.webdav.ListCryptFiles;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.io.FileDescriptor;
import java.util.List;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SyncFiles {
    @WebMethod
    ListCryptFiles getAllFiles();

    @WebMethod
    DataHandler getFile(CryptFile file);

    @WebMethod
    String test(String name);
}
