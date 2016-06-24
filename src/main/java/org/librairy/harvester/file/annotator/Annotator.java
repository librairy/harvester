package org.librairy.harvester.file.annotator;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
public interface Annotator {

    /**
     *
     * @param itemURI the URI of the ITEM to be annotated
     * @return
     */
    AnnotatedDocument annotate(String itemURI);

}