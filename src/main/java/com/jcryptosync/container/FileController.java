package com.jcryptosync.container;

import com.jcryptosync.container.domain.*;
import com.jcryptosync.container.domain.File;
import com.jcryptosync.container.domain.Folder;
import io.milton.annotations.*;

import java.io.*;
import java.util.List;

@ResourceController
public class FileController {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileController.class);
    FileSystem fileSystem = new FileSystem();

    @Root
    public Folder getRoot() {
        return fileSystem.getRootString();
    }

    @Name
    public java.lang.String getRootName(Folder root) {
        return root.getName();
    }

    @ChildrenOf
    public List<BaseFile> getListFiles(Folder folder) {
        log.info("get folder: " + folder.getName());

        return folder.getFiles();
    }

    @MakeCollection
    public Folder createFolderInFolder(Folder folder, java.lang.String name) {
        log.info("create folder in folder: " + folder.getName());

        return fileSystem.addNewFolder(name, folder);
    }

    @PutChild
    public File createFileInFolder(Folder folder, java.lang.String name, InputStream is) {
        log.info("create new file in folder:" + name);

        return fileSystem.addNewFile(name, folder, is);
    }

    @Move
    public void moveFile(BaseFile file, Folder folder, java.lang.String newName) {


        if(!file.getFolderId().equals(folder.getFileId())) {
            log.info(java.lang.String.format("move file from %s to %s", file.getName(), folder.getName()));
            fileSystem.moveFile(file, folder, newName);
        } else {
            log.info(java.lang.String.format("rename file from %s to %s", file.getName(), newName));
            fileSystem.renameFile(file, newName);
        }
    }

    @Delete
    public void deleteFile(File file) {
        log.info("delete file: " + file.getName());
        fileSystem.deleteFile(file);
    }

    @Delete
    public void deleteFolder(Folder file) {
        log.info("delete folder: " + file.getName());
        fileSystem.deleteFolder(file);
    }

    @Get
    public void getFile(File file, OutputStream os) {

       log.info("get file content: " + file.getName());

        fileSystem.getFile(file, os);
    }

    @PutChild
    public File modifyFile(File file, InputStream is) {

        log.info(java.lang.String.format("modify file: %s", file.getName()));

        return file;
    }
}
