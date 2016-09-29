/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.serializer;

import org.librairy.harvester.file.annotator.AnnotatedDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by cbadenes on 24/02/16.
 */
public class AnnotatedDocumentSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedDocumentSerializer.class);

    public static void to(AnnotatedDocument document, String path){
        try{
            LOG.debug("Serializing  in:  " + path);
            File file = new File(path);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(document);
            oos.close();
            LOG.info("Serialized : " + document + " in:  " + path);
        }catch(Exception ex){
            LOG.error("Error serializing annotated document: " + document + " to file: " + path);
        }
    }

    public static AnnotatedDocument from(String path){
        try{
            File file = new File(path);
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fin);
            Object object = ois.readObject();
            AnnotatedDocument annotatedDocument = (AnnotatedDocument) object;
            LOG.info("Deserialized:  " + path);
            return annotatedDocument;
        }catch(Exception e){
            throw new RuntimeException("Error deserializing document: " + path, e);
        }
    }
}
