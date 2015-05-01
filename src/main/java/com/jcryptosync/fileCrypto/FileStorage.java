package com.jcryptosync.fileCrypto;

import com.google.gson.Gson;
import com.jcryptosync.QuickPreferences;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
    private static final FileStorage INSTANCE = new FileStorage();

    private FileStorage() {}

    public static FileStorage getInstance() {
        return INSTANCE;
    }

    Storage storage = new Storage();


    public void addFileMetadata(FileMetadata fileMetadata) {
        storage.fileMetadatas.add(fileMetadata);

        saveMetadata();
    }
    
    public List<FileMetadata> getMetadataList() {
        return storage.fileMetadatas;
    }

    private void saveMetadata() {

        Gson gson = new Gson();
        String json = gson.toJson(storage);

        try {
            Files.write(QuickPreferences.getPathToContainer(), json.getBytes("UTF-8"), StandardOpenOption.CREATE);
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
        private List<FileMetadata> fileMetadatas = new ArrayList<>();
    }

}
