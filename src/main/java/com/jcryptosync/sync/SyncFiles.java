package com.jcryptosync.sync;

import com.jcryptosync.container.webdav.AbstractFile;
import com.jcryptosync.container.webdav.CryptFile;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.io.FileDescriptor;
import java.util.List;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SyncFiles {
    //@WebMethod
    //List<AbstractFile> getAllFiles();

    @WebMethod
    FileDescriptor getFile(CryptFile file);

    @WebMethod
    String test(String name);
}
