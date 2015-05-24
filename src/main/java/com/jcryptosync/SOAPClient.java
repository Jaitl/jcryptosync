package com.jcryptosync;


import com.jcryptosync.container.webdav.AbstractFile;
import com.jcryptosync.container.webdav.CryptFile;
import com.jcryptosync.container.webdav.ListCryptFiles;
import com.jcryptosync.sync.SyncFiles;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.FileOutputStream;
import java.net.URL;

public class SOAPClient {

    public static void main(String[] args) throws Exception {

        int port = UserPreferences.getStartPort();

        URL url = new URL(String.format("http://localhost:%s/api/SyncFiles?wsdl", port));

        //1st argument service URI, refer to wsdl document above
        //2nd argument is service name, refer to wsdl document above
        QName qname = new QName("http://sync.jcryptosync.com/", "SyncFilesImplService");

        Service service = Service.create(url, qname);

        SyncFiles hello = service.getPort(SyncFiles.class);

        System.out.println(hello.test("Jaitl"));

        ListCryptFiles listCryptFiles = hello.getAllFiles();

        listCryptFiles.getFileList().forEach(f -> System.out.println(f.getName()));

        CryptFile file = (CryptFile) listCryptFiles.getFileList().get(1);


        DataHandler dataHandler = hello.getFile(file);

        try(FileOutputStream outputStream =
                    new FileOutputStream("/home/jaitl/Documents/container/test/" + file.getUniqueId())) {

            dataHandler.writeTo(outputStream);
        }
    }

}