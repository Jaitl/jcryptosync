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
package com.jcryptosync.vfs.webdav.config;

import com.jcryptosync.preferences.ContainerPreferences;
import com.jcryptosync.domain.User;
import com.jcryptosync.vfs.filesystem.CryptFileSystem;
import com.jcryptosync.vfs.webdav.Folder;
import io.milton.common.Path;
import io.milton.http.ResourceFactory;
import io.milton.resource.Resource;

import java.util.HashMap;
import java.util.Map;

public class TResourceFactory implements ResourceFactory {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TResourceFactory.class);
    public static final Folder ROOT;
    public static final Map<String, User> users = new HashMap<>();

    static {
        User user = ContainerPreferences.getInstance().getUser();
        users.put(user.getName(), user);

        CryptFileSystem fs = CryptFileSystem.getInstance();

        ROOT = fs.getRoot();
    }

    private String contextPath = "webdav";


    @Override
    public Resource getResource(String host, String url) {
        log.debug("getResource: url: " + url);
        url = stripContext(url);
        Path path = Path.path(url);
        Resource r = find(path);
        log.debug("_found: " + r + " for url: " + url + " and path: " + path);
        return r;
    }

    private String stripContext(String url) {
        if (this.contextPath != null && contextPath.length() > 0) {
            url = url.replaceFirst('/' + contextPath, "");
            log.debug("stripped context: " + url);
            return url;
        } else {
            return url;
        }
    }

    private Resource find(Path path) {
        if (path.isRoot()) {
            return ROOT;
        }
        Resource rParent = find(path.getParent());
        if (rParent == null) {
            return null;
        }
        if (rParent instanceof Folder) {
            Folder folder = (Folder) rParent;
            for (Resource rChild : folder.getChildren()) {
                if (rChild.getName().equals(path.getName())) {
                    return rChild;
                }
            }
            log.warn("Resource: " + path.getName() + " not found in collection: " + path.getParent() + " of type: " + rParent.getClass());
        } else {
            log.warn("parent not found: " + path.getParent());
        }
        return null;
    }    
}
