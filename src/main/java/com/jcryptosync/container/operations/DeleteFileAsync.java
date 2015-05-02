package com.jcryptosync.container.operations;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.file.FileMetadata;
import com.jcryptosync.container.file.FileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.RecursiveAction;

public class DeleteFileAsync extends RecursiveAction {

    protected static Logger log = LoggerFactory.getLogger(DeleteFileAsync.class);

    private static FileStorage fileStorage = FileStorage.getInstance();

    Path deleteFile;

    public DeleteFileAsync(Path deleteFile) {
        this.deleteFile = deleteFile;
    }

    @Override
    protected void compute() {
        FileMetadata metadata = fileStorage.getMetadata(deleteFile.getFileName().toString());

        deleteFile(metadata);

        fileStorage.deleteFileMetadata(metadata.getName());
        log.info("deleted file: " + metadata.getName());
    }

    public static void deleteFile(FileMetadata metadata) {
        Path pathToCryptFile = QuickPreferences.getPathToCryptDir();
        pathToCryptFile = pathToCryptFile.resolve(metadata.getId());

        try {
            Files.delete(pathToCryptFile);
        } catch (IOException e) {
            log.error("delete error", e);
        }
    }
}
