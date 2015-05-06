package com.jcryptosync.container.operations;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.domain.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.RecursiveAction;

public class DeleteFileAsync  {

    protected static Logger log = LoggerFactory.getLogger(DeleteFileAsync.class);

     File file;

    public DeleteFileAsync(File file) {
        this.file = file;
    }

    public void compute() {
        deleteFile(file);

        log.info("deleted file: " + file.getName());
    }

    public static void deleteFile(File metadata) {
        Path pathToCryptFile = QuickPreferences.getPathToCryptDir();
        pathToCryptFile = pathToCryptFile.resolve(metadata.getFileId());

        try {
            Files.delete(pathToCryptFile);
        } catch (IOException e) {
            log.error("delete error", e);
        }
    }
}
