/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component;

import org.apache.camel.RuntimeCamelException;

/**
 * Exception thrown in case of last file operation failed.
 *
 * @version 
 */
public class GenericFileOperationFailedException extends RuntimeCamelException {
    private static final long serialVersionUID = -64176625836814418L;

    private final int code;
    private final String reason;

    public GenericFileOperationFailedException(String message) {
        super(message);
        this.code = 0;
        this.reason = null;
    }

    public GenericFileOperationFailedException(String message, Throwable cause) {
        super(message, cause);
        this.code = 0;
        this.reason = null;
    }

    public GenericFileOperationFailedException(int code, String reason) {
        super("File operation failed: " + reason + ". Code: " + code);
        this.code = code;
        this.reason = reason;
    }

    public GenericFileOperationFailedException(int code, String reason, Throwable cause) {
        super("File operation failed: " + reason + ". Code: " + code, cause);
        this.code = code;
        this.reason = reason;
    }

    public GenericFileOperationFailedException(int code, String reason, String message) {
        this(code, reason + " " + message);
    }

    public GenericFileOperationFailedException(int code, String reason, String message, Throwable cause) {
        this(code, reason + " " + message, cause);
    }

    /**
     * Return the file failure code (if any)
     */
    public int getCode() {
        return code;
    }

    /**
     * Return the file failure reason (if any)
     */
    public String getReason() {
        return reason;
    }
}
