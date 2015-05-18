package com.jcryptosync.container.webdav;

import com.jcryptosync.UserPreferences;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.nio.file.Path;
import java.util.Map;

public class DataBase {
    private static final DataBase instance = new DataBase();

    private static final String ROOT_FOLDER_ID = "rootFolderId";
    private static final String FILE_METADATA = "fileMetadata";
    private static final String INTERNAL_SETTINGS = "internalSettings";

    private DB db;

    private DataBase() {
        Path pathToDb = UserPreferences.getPathToContainer();
        db = DBMaker.newFileDB(pathToDb.toFile()).closeOnJvmShutdown().make();
    }

    public static DataBase getInstance() {
        return instance;
    }

    public Map<String, AbstractFile> getFileMetadata() {
        return  db.getTreeMap(FILE_METADATA);
    }
    public Map<String, Object> getInternalSettings() {
        return  db.getTreeMap(INTERNAL_SETTINGS);
    }

    public void save() {
        db.commit();
    }

    public void saveRootFolderId(String id) {
        db.createAtomicString(ROOT_FOLDER_ID, id);
        save();
    }

    public String getRootFolderId() {
        return db.getAtomicString(ROOT_FOLDER_ID).toString();
    }
}