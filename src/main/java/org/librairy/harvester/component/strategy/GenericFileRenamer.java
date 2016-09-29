/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component.strategy;

import org.apache.camel.Exchange;
import org.librairy.harvester.component.GenericFile;

/**
 * Used for renaming files.
 */
public interface GenericFileRenamer<T> {

    /**
     * Renames the given file
     *
     * @param exchange  the exchange
     * @param file      the original file.
     * @return the renamed file name.
     */
    GenericFile<T> renameFile(Exchange exchange, GenericFile<T> file);
}
