package com.jcryptosync.container.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcryptosync.FileRuntimeAdapter;
import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.operations.AddFileAsync;
import com.jcryptosync.container.operations.DeleteFileAsync;
import com.jcryptosync.container.operations.GetFileAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FileSystem {
    private Map<String, Folder> folderMap = new HashMap<>();

    protected static Logger log = LoggerFactory.getLogger(FileSystem.class);

    public FileSystem() {
        if(!loadRoot()) {
            Folder root = new Folder("Files", null);

            folderMap.put(root.getFileId(), root);
            folderMap.put("root", root);
        }
    }

    public File addNewFile(java.lang.String name, Folder folder, InputStream is) {
        File file = new File(name, folder.getFolderId());
        folder.addFile(file);

        log.info("add new file to filesystem: ", file.getFileId());

        AddFileAsync addFile = new AddFileAsync(file, is);
        addFile.compute();

        saveRoot();

        return file;
    }

    public Folder addNewFolder(java.lang.String name, Folder folder) {
        Folder newFolder = new Folder(name, folder.getFolderId());
        folder.addFile(newFolder);
        folderMap.put(newFolder.getFileId(), newFolder);

        return newFolder;
    }

    public void getFile(File file, OutputStream os) {
        GetFileAsync getFileAsync = new GetFileAsync(file, os);
        getFileAsync.compute();

        log.info("file get end");
    }

    public void moveFile(BaseFile file, Folder folder, java.lang.String newName) {
        Folder parentFolder = folderMap.get(file.getFolderId());
        parentFolder.removeFile(file);
        folder.addFile(file);
        file.setName(newName);
    }

    public void renameFile(BaseFile file, java.lang.String newName) {
        file.setName(newName);
    }

    public void deleteFile(File file) {
        Folder folder = folderMap.get(file.getFolderId());
        folder.removeFile(file);

        DeleteFileAsync deleteFile = new DeleteFileAsync(file);
        deleteFile.compute();
    }

    public void deleteFolder(Folder file) {
        Folder folder = folderMap.get(file.getFolderId());
        folder.removeFile(file);
        folderMap.remove(file.getFileId());
    }

    public Folder getRootString() {
        return folderMap.get("root");
    }


    private synchronized void saveRoot() {

        folderStore.setStoreMap(folderMap);



        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BaseFile.class, new FileRuntimeAdapter());

        Gson gson = builder.create();

        java.lang.String json = gson.toJson(folderStore);

        try {
            if (Files.exists(QuickPreferences.getPathToContainer()))
                Files.delete(QuickPreferences.getPathToContainer());

            Files.write(QuickPreferences.getPathToContainer(), json.getBytes("UTF-8"), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadRoot() {
        byte[] bytes = new byte[0];

        try {
            if (Files.exists(QuickPreferences.getPathToContainer()))
                bytes = Files.readAllBytes(QuickPreferences.getPathToContainer());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bytes.length > 0) {

            java.lang.String json = null;
            try {
                json = new java.lang.String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(BaseFile.class, new FileRuntimeAdapter());

                log.info("load...");
                Gson gson = builder.create();

                folderStore = gson.fromJson(json, FolderStore.class);
                folderMap = folderStore.getStoreMap();
                return true;
            } catch (Exception e){
                log.error("error load base", e);
            }
        }

        return false;
    }

    FolderStore folderStore = new FolderStore();

    private class FolderStore {
        private Map<String, Folder> storeMap = new HashMap<>();

        public Map<String, Folder> getStoreMap() {
            return storeMap;
        }

        public void setStoreMap(Map<String, Folder> storeMap) {
            this.storeMap = storeMap;
        }
    }

}
