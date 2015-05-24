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
package com.jcryptosync.container.webdav;

import io.milton.http.Request;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.CollectionResource;
import io.milton.resource.MakeCollectionableResource;
import io.milton.resource.PutableResource;
import io.milton.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Folder extends AbstractFile implements PutableResource, MakeCollectionableResource {

    public Folder() {}

    public Folder(String name, String parentId) {
        super(name, parentId);
        log.debug("created new folder: " + name);
    }

    @Override
    public List<? extends Resource> getChildren() {
        log.debug("get children");

        CryptFileSystem fileSystem = CryptFileSystem.getInstance();
        return fileSystem.getChildren(this);
    }

    @Override
    public CollectionResource createCollection(String newName) {
        log.debug("create new folder: " + newName);

        Folder folder = new Folder(newName, getUniqueId());

        CryptFileSystem fileSystem = CryptFileSystem.getInstance();
        fileSystem.createNewFolder(folder);

        return folder;
    }

    @Override
    public Resource createNew(String newName, InputStream inputStream, Long length, String contentType) throws IOException {
        log.debug("create new file: " + newName);

        CryptFile newCryptFile = new CryptFile(newName, getUniqueId(), length, contentType);

        CryptFileSystem fileSystem = CryptFileSystem.getInstance();
        fileSystem.createNewFile(newCryptFile, inputStream);

        return newCryptFile;
    }

    @Override
    public Resource child(String childName) {

        CryptFileSystem fileSystem = CryptFileSystem.getInstance();
        return fileSystem.getChild(this, childName);
    }

    @Override
    public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
        log.debug("delete folder: " + getName());

        CryptFileSystem fileSystem = CryptFileSystem.getInstance();
        fileSystem.deleteFolder(this);
    }

    @Override
    public String checkRedirect(Request request) {
        return null;
    }
}
