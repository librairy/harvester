package org.librairy.harvester.file.annotator;

import java.io.File;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
public interface Annotator {


    AnnotatedDocument annotate(File file);

    AnnotatedDocument annotate(String text);

}