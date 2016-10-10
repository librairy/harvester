/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component.strategy;

import org.apache.camel.Exchange;
import org.librairy.harvester.component.GenericFile;
import org.librairy.harvester.component.GenericFileOperations;
import org.librairy.harvester.component.GenericFileEndpoint;

public class GenericFileRenameProcessStrategy<T> extends GenericFileProcessStrategySupport<T> {
    private GenericFileRenamer<T> beginRenamer;
    private GenericFileRenamer<T> failureRenamer;
    private GenericFileRenamer<T> commitRenamer;

    public GenericFileRenameProcessStrategy() {
    }

    @Override
    public boolean begin(GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint, Exchange exchange, GenericFile<T> file) throws Exception {
        // must invoke super
        boolean result = super.begin(operations, endpoint, exchange, file);
        if (!result) {
            return false;
        }

        // okay we got the file then execute the begin renamer
        if (beginRenamer != null) {
            GenericFile<T> newName = beginRenamer.renameFile(exchange, file);
            GenericFile<T> to = renameFile(operations, file, newName);
            if (to != null) {
                to.bindToExchange(exchange);
            }
        }

        return true;
    }

    @Override
    public void rollback(GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint, Exchange exchange, GenericFile<T> file) throws Exception {
        try {
            operations.releaseRetreivedFileResources(exchange);

            if (failureRenamer != null) {
                // create a copy and bind the file to the exchange to be used by the renamer to evaluate the file name
                Exchange copy = exchange.copy();
                file.bindToExchange(copy);
                // must preserve message id
                copy.getIn().setMessageId(exchange.getIn().getMessageId());
                copy.setExchangeId(exchange.getExchangeId());

                GenericFile<T> newName = failureRenamer.renameFile(copy, file);
                renameFile(operations, file, newName);
            }
        } finally {
            if (exclusiveReadLockStrategy != null) {
                exclusiveReadLockStrategy.releaseExclusiveReadLockOnRollback(operations, file, exchange);
            }
            deleteLocalWorkFile(exchange);
        }
    }

    @Override
    public void commit(GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint, Exchange exchange, GenericFile<T> file) throws Exception {
        try {
            if (commitRenamer != null) {
                // create a copy and bind the file to the exchange to be used by the renamer to evaluate the file name
                Exchange copy = exchange.copy();
                file.bindToExchange(copy);
                // must preserve message id
                copy.getIn().setMessageId(exchange.getIn().getMessageId());
                copy.setExchangeId(exchange.getExchangeId());

                GenericFile<T> newName = commitRenamer.renameFile(copy, file);
                renameFile(operations, file, newName);
            }
        } finally {
            // must invoke super
            super.commit(operations, endpoint, exchange, file);
        }
    }

    public GenericFileRenamer<T> getBeginRenamer() {
        return beginRenamer;
    }

    public void setBeginRenamer(GenericFileRenamer<T> beginRenamer) {
        this.beginRenamer = beginRenamer;
    }

    public GenericFileRenamer<T> getCommitRenamer() {
        return commitRenamer;
    }

    public void setCommitRenamer(GenericFileRenamer<T> commitRenamer) {
        this.commitRenamer = commitRenamer;
    }

    public GenericFileRenamer<T> getFailureRenamer() {
        return failureRenamer;
    }

    public void setFailureRenamer(GenericFileRenamer<T> failureRenamer) {
        this.failureRenamer = failureRenamer;
    }
}
