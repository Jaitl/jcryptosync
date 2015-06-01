package com.jcryptosync.data;

import com.jcryptosync.data.preferences.UserPreferences;
import com.jcryptosync.vfs.webdav.AbstractFile;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public class MetaData {
    private static final MetaData instance = new MetaData();

    private static final String ROOT_FOLDER_ID = "rootFolderId";
    private static final String FILE_METADATA = "fileMetadata";

    private DB db;

    private Map<String, AbstractFile> metaMap;

    private MetaData() {
        Path pathToDb = UserPreferences.getPathToContainer();
        db = DBMaker.newFileDB(pathToDb.toFile()).closeOnJvmShutdown().make();
        metaMap = db.getTreeMap(FILE_METADATA);
    }

    public static MetaData getInstance() {
        return instance;
    }

    public void addFile(AbstractFile file) {
        metaMap.put(file.getUniqueId(), file);
        db.commit();
    }

    public void updateFile(AbstractFile file) {
        metaMap.replace(file.getUniqueId(), file);
        db.commit();
    }

    public boolean containsFile(String idFile) {
        return metaMap.containsKey(idFile);
    }

    public Collection<AbstractFile> getCollectionFiles() {
        return metaMap.values();
    }

    public AbstractFile getFileById(String idFile) {
        return metaMap.get(idFile);
    }

    public int getCountFiles() {
        return metaMap.size();
    }

    public void saveRootFolderId(String id) {
        db.createAtomicString(ROOT_FOLDER_ID, id);
        db.commit();
    }

    public String getRootFolderId() {
        return db.getAtomicString(ROOT_FOLDER_ID).toString();
    }
}