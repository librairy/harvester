/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component;

import org.apache.camel.Exchange;

/**
 * Represents a pluggable strategy when processing files.
 */
public interface GenericFileProcessStrategy<T>  {

    /**
     * Allows custom logic to be run on first poll preparing the strategy,
     * such as removing old lock files etc.
     *
     * @param operations file operations
     * @param endpoint   the endpoint
     * @throws Exception can be thrown in case of errors which causes poll to fail
     */
    void prepareOnStartup(GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint) throws Exception;

    /**
     * Called when work is about to begin on this file. This method may attempt
     * to acquire some file lock before returning true; returning false if the
     * file lock could not be obtained so that the file should be ignored.
     *
     * @param operations file operations
     * @param endpoint   the endpoint
     * @param exchange   the exchange
     * @param file       the file
     * @return true if the file can be processed (such as if a file lock could be obtained)
     * @throws Exception can be thrown in case of errors
     */
    boolean begin(GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint,
                  Exchange exchange, GenericFile<T> file) throws Exception;

    /**
     * Called when a begin is aborted, for example to release any resources which may have
     * been acquired during the {@link #begin(GenericFileOperations, GenericFileEndpoint, org.apache.camel.Exchange, GenericFile)}
     * operation.
     *
     * @param operations file operations
     * @param endpoint   the endpoint
     * @param exchange   the exchange
     * @param file       the file
     * @throws Exception can be thrown in case of errors
     */
    void abort(GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint,
               Exchange exchange, GenericFile<T> file) throws Exception;

    /**
     * Releases any file locks and possibly deletes or moves the file after
     * successful processing
     *
     * @param operations file operations
     * @param endpoint   the endpoint
     * @param exchange   the exchange
     * @param file       the file
     * @throws Exception can be thrown in case of errors
     */
    void commit(GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint,
                Exchange exchange, GenericFile<T> file) throws Exception;

    /**
     * Releases any file locks and possibly deletes or moves the file after
     * unsuccessful processing
     *
     * @param operations file operations
     * @param endpoint   the endpoint
     * @param exchange   the exchange
     * @param file       the file
     * @throws Exception can be thrown in case of errors
     */
    void rollback(GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint,
                  Exchange exchange, GenericFile<T> file) throws Exception;

}
