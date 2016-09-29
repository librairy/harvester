/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component;

/**
 * A filter for {@link GenericFile}.
 */
public interface GenericFileFilter<T> {

    /**
     * Tests whether or not the specified generic file should be included
     *
     * @param file the generic file to be tested
     * @return <code>true</code> if and only if <code>file</code> should be included
     */
    boolean accept(GenericFile<T> file);

}
