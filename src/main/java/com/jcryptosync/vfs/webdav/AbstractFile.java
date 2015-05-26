/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jcryptosync.vfs.webdav;

import com.jcryptosync.vfs.filesystem.CryptFileSystem;
import com.jcryptosync.vfs.webdav.config.TResourceFactory;
import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.http11.auth.DigestGenerator;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.*;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public abstract class  AbstractFile implements Resource, PropFindableResource, DeletableResource,
        MoveableResource, Serializable, DigestResource {

    protected static transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractFile.class);

    @XmlElement
    protected String id;
    protected String name;
    protected Date modDate;
    protected Date createdDate;

    protected String parentId;

    public AbstractFile() {}


    public AbstractFile(String name, String parentId) {
        id = UUID.randomUUID().toString();
        this.parentId = parentId;
        this.name = name;
        modDate = new Date();
        createdDate = new Date();
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }



    public void setName(String name) {
        this.name = name;
    }


    @Override
    public void moveTo(CollectionResource folder, String name) {
        CryptFileSystem fs = CryptFileSystem.getInstance();

        if(folder.getUniqueId().equals(parentId)) {
            log.debug("rename file: " + getName());
            fs.renameFile(this, name);
        } else  {
            log.debug("move file: " + getName());
            fs.moveFile(this, (Folder) folder, name);
        }
    }

    @Override
    public Object authenticate(String user, String requestedPassword) {
        String p = TResourceFactory.users.get(user).getPassword();
        if (p != null) {
            if (p.equals(requestedPassword)) {
                return Boolean.TRUE;
            } else {
                log.warn("that password is incorrect. Try:" + p);
            }
        } else {
            log.warn("user not found: " + user + " - try 'userA'");
        }
        return null;
    }

    @Override
    public Object authenticate(DigestResponse digestRequest) {
        String p = TResourceFactory.users.get(digestRequest.getUser()).getPassword();
        if (p != null) {
            DigestGenerator gen = new DigestGenerator();
            String actual = gen.generateDigest(digestRequest, p);
            if (actual.equals(digestRequest.getResponseDigest())) {
                return p;
            } else {
                log.warn("that password is incorrect. Try 'password'");
            }
        } else {
            log.warn("user not found: " + digestRequest.getUser() + " - try 'userA'");
        }

        return null;
    }

    @Override
    public String getUniqueId() {
        return this.id;
    }

    @Override
    public String checkRedirect(Request request) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean authorise(Request request, Method method, Auth auth) {
        log.debug("authorise");
        return auth != null;
    }

    @Override
    public boolean isDigestAllowed() {
        return true;
    }

    @Override
    public String getRealm() {
        return "CryptFiles";
    }

    @Override
    public Date getModifiedDate() {
        return modDate;
    }

    @Override
    public Date getCreateDate() {
        return createdDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
