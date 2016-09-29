/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component;

import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;

/**
 * Generic file message
 */
public class GenericFileMessage<T> extends DefaultMessage {
    private GenericFile<T> file;

    public GenericFileMessage() {
    }

    public GenericFileMessage(GenericFile<T> file) {
        this.file = file;
    }

    @Override
    protected Object createBody() {
        return file != null ? file.getBody() : super.createBody();
    }

    public GenericFile<T> getGenericFile() {
        return file;
    }

    public void setGenericFile(GenericFile<T> file) {
        this.file = file;
    }

    @Override
    public GenericFileMessage<T> newInstance() {
        return new GenericFileMessage<T>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void copyFrom(Message that) {
        super.copyFrom(that);

        if (that instanceof GenericFileMessage) {
            setGenericFile(((GenericFileMessage) that).getGenericFile());
        }
    }

    @Override
    public String toString() {
        // only output the filename as body can be big
        if (file != null) {
            return file.getFileName();
        }
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
}
