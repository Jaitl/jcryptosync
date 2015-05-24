package com.jcryptosync.sync;

import com.jcryptosync.container.webdav.AbstractFile;
import com.jcryptosync.container.webdav.CryptFile;
import org.apache.log4j.Logger;

import javax.jws.WebService;
import java.io.FileDescriptor;
import java.util.List;

@WebService(endpointInterface = "com.jcryptosync.sync.SyncFiles")
public class SyncFilesImpl implements SyncFiles {
    private static Logger log  = Logger.getLogger(SyncFilesImpl.class);


    //@Override
    //public List<AbstractFile> getAllFiles() {
      //  return null;
    //}

    @Override
    public FileDescriptor getFile(CryptFile file) {
        return null;
    }

    @Override
    public String test(String name) {
        return String.format("Hello, %s! I'm JCryptoSync", name);
    }
}
