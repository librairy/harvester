/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.routes.converter;

import org.apache.camel.Converter;
import org.apache.camel.TypeConversionException;
import org.librairy.boot.model.Event;
import org.librairy.boot.model.domain.resources.File;

/**
 * Created by cbadenes on 11/02/16.
 */
@Converter
public class FileTypeConverter {

    @Converter
    public static byte[] toByteArray(File file) throws TypeConversionException {
        return Event.from(file).getBytes();
    }
}
