/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component.strategy;

import java.io.File;

import org.apache.camel.Exchange;
import org.librairy.harvester.component.GenericFile;
import org.librairy.harvester.component.GenericFileOperations;

/**
 * Acquires exclusive read lock to the given file. Will wait until the lock is granted.
 * After granting the read lock it is released, we just want to make sure that when we start
 * consuming the file its not currently in progress of being written by third party.
 * <p/>
 * This implementation is only supported by the File component, that leverages the {@link MarkerFileExclusiveReadLockStrategy}
 * as well, to ensure only acquiring locks on files, which is not already in progress by another process,
 * that have marked this using the marker file.
 * <p/>
 * Setting the option {@link #setMarkerFiler(boolean)} to <tt>false</tt> allows to turn off using marker files.
 */
public class FileRenameExclusiveReadLockStrategy extends GenericFileRenameExclusiveReadLockStrategy<File> {

    private MarkerFileExclusiveReadLockStrategy marker = new MarkerFileExclusiveReadLockStrategy();
    private boolean markerFile = true;

    @Override
    public boolean acquireExclusiveReadLock(GenericFileOperations<File> operations, GenericFile<File> file, Exchange exchange) throws Exception {
        // must call marker first
        if (markerFile && !marker.acquireExclusiveReadLock(operations, file, exchange)) {
            return false;
        }

        return super.acquireExclusiveReadLock(operations, file, exchange);
    }

    @Override
    public void releaseExclusiveReadLockOnAbort(GenericFileOperations<File> operations, GenericFile<File> file, Exchange exchange) throws Exception {
        // must call marker first
        try {
            if (markerFile) {
                marker.releaseExclusiveReadLockOnAbort(operations, file, exchange);
            }
        } finally {
            super.releaseExclusiveReadLockOnAbort(operations, file, exchange);
        }
    }

    @Override
    public void releaseExclusiveReadLockOnRollback(GenericFileOperations<File> operations, GenericFile<File> file, Exchange exchange) throws Exception {
        // must call marker first
        try {
            if (markerFile) {
                marker.releaseExclusiveReadLockOnRollback(operations, file, exchange);
            }
        } finally {
            super.releaseExclusiveReadLockOnRollback(operations, file, exchange);
        }
    }

    @Override
    public void releaseExclusiveReadLockOnCommit(GenericFileOperations<File> operations, GenericFile<File> file, Exchange exchange) throws Exception {
        // must call marker first
        try {
            if (markerFile) {
                marker.releaseExclusiveReadLockOnCommit(operations, file, exchange);
            }
        } finally {
            super.releaseExclusiveReadLockOnCommit(operations, file, exchange);
        }
    }

    @Override
    public void setMarkerFiler(boolean markerFile) {
        this.markerFile = markerFile;
    }

}
