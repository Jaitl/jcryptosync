package com.jcryptosync.container.operations;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.file.FileMetadata;
import com.jcryptosync.container.file.FileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Path;
import java.util.concurrent.RecursiveAction;

public class ModificateFileAsync extends RecursiveAction {

    protected static Logger log = LoggerFactory.getLogger(ModificateFileAsync.class);

    private static FileStorage fileStorage = FileStorage.getInstance();

    Path path;

    public ModificateFileAsync(Path path) {
        this.path = path;
    }

    @Override
    protected void compute() {
        FileMetadata metadata = fileStorage.getMetadata(path.getFileName().toString());

        DeleteFileAsync.deleteFile(metadata);

        byte[] hash = AddFileAsync.compareHash(path);

        metadata.setHash(hash);

        Path pathToFile = QuickPreferences.getPathToCryptDir().resolve(metadata.getId());
        byte[] key = metadata.getKey();
        SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "AES");
        byte[] ivByte = metadata.getIv();

        AddFileAsync.saveCryptFile(path, secretKey, ivByte, pathToFile);

        fileStorage.updateFileMetadata(metadata);

        log.info("modificated file: " + metadata.getName());
    }
}
