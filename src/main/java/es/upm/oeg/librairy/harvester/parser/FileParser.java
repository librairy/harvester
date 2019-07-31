package es.upm.oeg.librairy.harvester.parser;

import es.upm.oeg.librairy.harvester.data.Document;

import java.nio.file.Path;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public interface FileParser {

    Document parse(Path path);

    String suffix();
}
