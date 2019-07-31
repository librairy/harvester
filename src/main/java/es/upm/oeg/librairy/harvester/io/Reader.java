package es.upm.oeg.librairy.harvester.io;

import es.upm.oeg.librairy.harvester.data.Document;

import java.util.Optional;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public interface Reader {

    Optional<Document> next();

    void offset(Integer numLines);
}
