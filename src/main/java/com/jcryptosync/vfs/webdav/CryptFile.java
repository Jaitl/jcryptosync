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
import io.milton.http.Auth;
import io.milton.http.Range;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.CollectionResource;
import io.milton.resource.CopyableResource;
import io.milton.resource.GetableResource;
import io.milton.resource.ReplaceableResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class CryptFile extends AbstractFile implements ReplaceableResource, GetableResource, CopyableResource {

    private Long length;
    private String contentType;
    private byte[] key;
    private byte[] iv;

    public CryptFile() {}


    public CryptFile(String name, String parentId, Long length, String contentType) {
        super(name, parentId);
        this.length = length;
        this.contentType = contentType;
    }

    @Override
    public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException {
        log.debug("get content file: " + getName());

        CryptFileSystem fileSystem = CryptFileSystem.getInstance();
        fileSystem.getFileContent(this, out);
    }

    @Override
    public Long getMaxAgeSeconds(Auth auth) {
        return null;
    }

    @Override
    public String getContentType(String s) {
        return null;
    }

    @Override
    public Long getContentLength() {
        return length;
    }

    @Override
    public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
        log.debug("update file: " + getName());

        this.length = length;

        CryptFileSystem fileSystem = CryptFileSystem.getInstance();
        fileSystem.updateFile(this, in);
    }

    @Override
    public void copyTo(CollectionResource toCollection, String name) throws NotAuthorizedException, BadRequestException, ConflictException {
        log.debug("copy to: " + toCollection.getName());
    }

    @Override
    public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
        log.debug("delete file: " + getName());

        CryptFileSystem fileSystem = CryptFileSystem.getInstance();
        fileSystem.deleteFile(this);
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
}
