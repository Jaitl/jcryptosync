package com.jcryptosync.sync;

import com.jcryptosync.UserPreferences;
import com.jcryptosync.container.webdav.CryptFile;
import com.jcryptosync.container.webdav.DataBase;
import com.jcryptosync.container.webdav.ListCryptFiles;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;
import java.nio.file.Path;

@MTOM
@WebService(endpointInterface = "com.jcryptosync.sync.SyncFiles")
public class SyncFilesImpl implements SyncFiles {
    private static Logger log  = Logger.getLogger(SyncFilesImpl.class);


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
