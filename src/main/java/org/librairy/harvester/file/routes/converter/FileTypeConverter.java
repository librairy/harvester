package org.librairy.harvester.file.routes.converter;

import org.apache.camel.Converter;
import org.apache.camel.TypeConversionException;
import org.librairy.model.Event;
import org.librairy.model.domain.resources.File;

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
