package com.jcryptosync.container.webdav;

import com.jcryptosync.container.FileOperations;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CryptFileSystem {

    private static final CryptFileSystem instance = new CryptFileSystem();
    protected static Logger log = Logger.getLogger(CryptFileSystem.class);

    private DataBase db;
    private Map<String, AbstractFile> fileMetadata;

    private CryptFileSystem() {
        db = DataBase.getInstance();
        fileMetadata = db.getFileMetadata();

        if(fileMetadata.entrySet().size() == 0) {
            Folder root = new Folder("Root", null);
            fileMetadata.put(root.getUniqueId(), root);
            db.saveRootFolderId(root.getUniqueId());
            db.save();
        }
    }

    public static CryptFileSystem getInstance() {
        return instance;
    }

    public void createNewFile(CryptFile cryptFile, InputStream is) {
        fileMetadata.put(cryptFile.getUniqueId(), cryptFile);

        FileOperations fileOperations = new FileOperations(cryptFile);
        fileOperations.addFile(is);

        db.save();
        log.debug("added new file: " + cryptFile.getName());
    }

    public void createNewFolder(Folder newFolder) {
        fileMetadata.put(newFolder.getUniqueId(), newFolder);

        db.save();
        log.debug("added folder file: " + newFolder.getName());
    }

    public void getFileContent(CryptFile cryptFile, OutputStream os) {
        FileOperations fileOperations = new FileOperations(cryptFile);
        fileOperations.getFile(os);

        log.debug("get file content: " + cryptFile.getName());
    }

    public void updateFile(CryptFile cryptFile, InputStream is) {

    }

    public void deleteFolder(Folder folder) {
        fileMetadata.remove(folder.getUniqueId());

        db.save();
    }

    public void deleteFile(CryptFile cryptFile) {
        fileMetadata.remove(cryptFile.getUniqueId());

        FileOperations fileOperations = new FileOperations(cryptFile);
        fileOperations.deleteFile();

        db.save();
    }

    public void renameFile(AbstractFile file, String name) {
        file.setName(name);

        fileMetadata.replace(file.getUniqueId(), file);

        db.save();
    }

    public void moveFile(AbstractFile file, Folder folder, String name) {
        file.setParentId(folder.getUniqueId());
        file.setName(name);
        fileMetadata.replace(file.getUniqueId(), file);

        db.save();

    }

    public Folder getRoot() {
        return (Folder) fileMetadata.get(db.getRootFolderId());
    }

    public List<AbstractFile> getChildren(Folder folder) {

            List<AbstractFile> children = fileMetadata.values().stream()
                    .filter(f -> folder.getUniqueId().equals(f.getParentId()))
                    .collect(Collectors.toList());

            return children;

    }

    public AbstractFile getChild(Folder folder, String name) {

        Optional<AbstractFile> file = getChildren(folder).stream().filter(f -> f.getName().equals(name)).findFirst();

        return file.orElse(null);
    }
}
