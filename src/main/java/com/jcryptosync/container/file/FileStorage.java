package com.jcryptosync.container.file;

import com.google.gson.Gson;
import com.jcryptosync.QuickPreferences;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStorage {
    private static final FileStorage INSTANCE = new FileStorage();

    private FileStorage() {}

    public static FileStorage getInstance() {
        return INSTANCE;
    }

    Storage storage = new Storage();


    public void addFileMetadata(FileMetadata fileMetadata) {
        storage.mapMetadata.put(fileMetadata.getName(), fileMetadata);

        saveMetadata();
    }

    public void deleteFileMetadata(String name) {
        storage.mapMetadata.remove(name);

        saveMetadata();
    }

    public FileMetadata getMetadata(String name) {
        return storage.mapMetadata.get(name);
    }
    
    public List<FileMetadata> getMetadataList() {
        return new ArrayList<>(storage.mapMetadata.values());
    }

    private synchronized void saveMetadata() {

        Gson gson = new Gson();
        String json = gson.toJson(storage);

        try {
            if(Files.exists(QuickPreferences.getPathToContainer()))
                Files.delete(QuickPreferences.getPathToContainer());

            Files.write(QuickPreferences.getPathToContainer(), json.getBytes("UTF-8"), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMetadata() {
        byte[] bytes = new byte[0];

        try {
            if(Files.exists(QuickPreferences.getPathToContainer()))
                bytes = Files.readAllBytes(QuickPreferences.getPathToContainer());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(bytes.length > 0) {

            String json = null;
            try {
                json = new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();

            storage = gson.fromJson(json, Storage.class);
        }
    }

    private class Storage {
        private Map<String, FileMetadata> mapMetadata = new HashMap<>();
    }

}
