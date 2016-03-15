package org.librairy.harvester.serializer;

import edu.upf.taln.dri.lib.model.DocumentImpl;
import gate.Document;
import org.librairy.harvester.annotator.upf.AnnotatedDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Optional;

/**
 * Created by cbadenes on 24/02/16.
 */
public class AnnotatedDocumentSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedDocumentSerializer.class);

    public static void to(AnnotatedDocument document, String path){
        try{
            LOG.debug("Serializing  in:  " + path);
            File file = new File(path);
            Document gateDocument = document.getGateDocument();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(gateDocument);
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
            Document gateDocument = (Document) object;
            AnnotatedDocument annotatedDocument = new AnnotatedDocument(new DocumentImpl(gateDocument));
            LOG.info("Deserialized:  " + path);
            return annotatedDocument;
        }catch(Exception e){
            throw new RuntimeException("Error deserializing document: " + path, e);
        }
    }
}
