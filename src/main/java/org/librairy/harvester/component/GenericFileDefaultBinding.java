/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component;

import java.io.IOException;

import org.apache.camel.Exchange;

/**
 * Default binding for generic file.
 */
public class GenericFileDefaultBinding<T> implements GenericFileBinding<T> {
    private Object body;

    public Object getBody(GenericFile<T> file) {
        return body;
    }

    public void setBody(GenericFile<T> file, Object body) {
        this.body = body;
    }

    public void loadContent(Exchange exchange, GenericFile<?> file) throws IOException {
        // noop as the body is already loaded
    }
}
