package com.jcryptosync.container.domain;

import java.util.ArrayList;
import java.util.List;

public class Folder extends BaseFile {

    private List<BaseFile> fileList = new ArrayList<>();

    public Folder(String name, String folder) {
        super(name, folder);
    }

    public Folder() {
    }

    public List<BaseFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<BaseFile> fileList) {
        this.fileList = fileList;
    }

    public void addFile(BaseFile file) {
        fileList.add(file);
    }

    public void removeFile(BaseFile file) {
        fileList.remove(file);
    }

    public List<BaseFile> getFiles() {
        return fileList;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isFolder() {
        return true;
    }
}
