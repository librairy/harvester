/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component;

import java.net.URI;

import org.apache.camel.spi.UriParams;
import org.apache.camel.util.FileUtil;

@UriParams
public class GenericFileConfiguration {

    protected String directory;

    public boolean needToNormalize() {
        return true;
    }

    public void configure(URI uri) {
        String path = uri.getPath();
        // strip tailing slash which URI path always start with
        path = FileUtil.stripFirstLeadingSeparator(path);
        setDirectory(path);
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = needToNormalize()
            // must normalize path to cater for Windows and other OS
            ? FileUtil.normalizePath(directory)
            // for the remote directory we don't need to do that
            : directory;

        // endpoint directory must not be null
        if (this.directory == null) {
            this.directory = "";
        }
    }

    public String toString() {
        return directory;
    }

}
