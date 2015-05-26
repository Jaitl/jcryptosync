package com.jcryptosync.domain;

import com.jcryptosync.vfs.webdav.AbstractFile;
import com.jcryptosync.vfs.webdav.CryptFile;
import com.jcryptosync.vfs.webdav.Folder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import java.util.ArrayList;
import java.util.List;

public class ListCryptFiles {

    private List<AbstractFile> fileList = new ArrayList<>();
    private String rootId;

    public void addToList(AbstractFile file) {
        fileList.add(file);
    }

    @XmlElements({
            @XmlElement(name="cryptfile", type=CryptFile.class),
            @XmlElement(name="folder", type=Folder.class)
    })
    @XmlElementWrapper
    public List<AbstractFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<AbstractFile> fileList) {
        this.fileList = fileList;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }
}
