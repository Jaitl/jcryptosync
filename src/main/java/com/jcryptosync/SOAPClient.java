package com.jcryptosync;


import com.jcryptosync.sync.SyncFiles;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
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

    }

}