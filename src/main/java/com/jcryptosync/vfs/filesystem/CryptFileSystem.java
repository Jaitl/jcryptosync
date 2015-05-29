package com.jcryptosync.vfs.filesystem;

import com.jcryptosync.data.ContainerPreferences;
import com.jcryptosync.data.MetaData;
import com.jcryptosync.vfs.webdav.AbstractFile;
import com.jcryptosync.vfs.webdav.CryptFile;
import com.jcryptosync.vfs.webdav.Folder;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CryptFileSystem {

    private static final CryptFileSystem instance = new CryptFileSystem();
    protected static Logger log = Logger.getLogger(CryptFileSystem.class);

    private MetaData db;
    private Map<String, AbstractFile> fileMetadata;
    private ChangeEvents changeEvents;

    private CryptFileSystem() {
        db = MetaData.getInstance();
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

        if(cryptFile.getLength() > 0) {

            String clientId = ContainerPreferences.getInstance().getClientId();
            cryptFile.getVector().increaseModification(clientId);
            cryptFile.getVector().increaseSynchronization(clientId);
        }

        fileMetadata.put(cryptFile.getUniqueId(), cryptFile);

        FileOperations.cryptFile(cryptFile, is);


        db.save();
        log.debug("added new file: " + cryptFile.getName());

        if(cryptFile.getLength() > 0) {
            if(changeEvents != null)
                changeEvents.changeFile(cryptFile);
        }
    }

    public void createNewFolder(Folder newFolder) {
        fileMetadata.put(newFolder.getUniqueId(), newFolder);

        db.save();
        log.debug("added folder file: " + newFolder.getName());

        if(changeEvents != null)
            changeEvents.changeFolder(newFolder);
    }

    public void getFileContent(CryptFile cryptFile, OutputStream os) {

        FileOperations.decryptFile(cryptFile, os);

        log.debug("get file content: " + cryptFile.getName());
    }

    public void updateFile(CryptFile cryptFile, InputStream is) {
        FileOperations.updateFile(cryptFile, is);

        log.debug("file updated: " + cryptFile.getName());

        cryptFile.setModDate(new Date());

        String clientId = ContainerPreferences.getInstance().getClientId();
        cryptFile.getVector().increaseModification(clientId);
        cryptFile.getVector().increaseSynchronization(clientId);

        fileMetadata.replace(cryptFile.getUniqueId(), cryptFile);
        db.save();

        if(changeEvents != null)
            changeEvents.changeFile(cryptFile);
    }

    public void deleteFolder(Folder folder) {
        folder.setModDate(new Date());
        folder.setDeleted(true);
        fileMetadata.replace(folder.getUniqueId(), folder);
        db.save();

        log.debug("folder deleted: " + folder.getName());

        if(changeEvents != null)
            changeEvents.changeFolder(folder);
    }

    public void deleteFile(CryptFile cryptFile) {
        cryptFile.setModDate(new Date());
        cryptFile.setDeleted(true);
        fileMetadata.replace(cryptFile.getUniqueId(), cryptFile);

        FileOperations.deleteFile(cryptFile);
        db.save();

        log.debug("file deleted: " + cryptFile.getName());

        if(changeEvents != null)
            changeEvents.changeFile(cryptFile);
    }

    public void renameFile(AbstractFile file, String name) {
        file.setModDate(new Date());

        log.debug(String.format("file rename from %s to %s ", file.getName(), name));

        file.setName(name);

        if(file instanceof CryptFile) {
            CryptFile cryptFile = (CryptFile) file;
            String clientId = ContainerPreferences.getInstance().getClientId();
            cryptFile.getVector().increaseModification(clientId);

            if(changeEvents != null)
                changeEvents.changeFile(cryptFile);
        } else {
            if(changeEvents != null)
                changeEvents.changeFolder((Folder) file);
        }

        fileMetadata.replace(file.getUniqueId(), file);
        db.save();
    }

    public void moveFile(AbstractFile file, Folder folder, String name) {
        file.setModDate(new Date());

        file.setParentId(folder.getUniqueId());
        file.setName(name);

        if(file instanceof CryptFile) {
            CryptFile cryptFile = (CryptFile) file;
            String clientId = ContainerPreferences.getInstance().getClientId();
            cryptFile.getVector().increaseModification(clientId);

            if(changeEvents != null)
                changeEvents.changeFile(cryptFile);
        } else {
            if(changeEvents != null)
                changeEvents.changeFolder((Folder) file);
        }

        fileMetadata.replace(file.getUniqueId(), file);

        db.save();

        log.debug(String.format("file moved: %s", name));
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

    public ChangeEvents getChangeEvents() {
        return changeEvents;
    }

    public void setChangeEvents(ChangeEvents changeEvents) {
        this.changeEvents = changeEvents;
    }

    public interface ChangeEvents {
        void changeFile(CryptFile file);
        void changeFolder(Folder folder);
    }
}
