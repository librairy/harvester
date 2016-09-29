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
 * Binding between the generic file and the body content.
 */
public interface GenericFileBinding<T>  {

    /**
     * Gets the body of the file
     *
     * @param file the file
     * @return the body
     */
    Object getBody(GenericFile<T> file);

    /**
     * Sets the body from the given file
     *
     * @param file the file
     * @param body the body
     */
    void setBody(GenericFile<T> file, Object body);

    /**
     * Ensures the content is loaded from the file into memory
     *
     * @param exchange the current exchange
     * @param file the file
     * @throws java.io.IOException is thrown if the content could not be loaded
     */
    void loadContent(Exchange exchange, GenericFile<?> file) throws IOException;
}
