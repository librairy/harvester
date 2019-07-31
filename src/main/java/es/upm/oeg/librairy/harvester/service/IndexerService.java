package es.upm.oeg.librairy.harvester.service;

import es.upm.oeg.librairy.harvester.data.Document;
import es.upm.oeg.librairy.harvester.parser.FileParser;
import es.upm.oeg.librairy.harvester.parser.ParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Optional;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class IndexerService {

    private static final Logger LOG = LoggerFactory.getLogger(IndexerService.class);

    @Autowired
    ParserFactory parserFactory;


    public void index(Path file){


        LOG.info("trying to get parser from: " + file);
        Optional<FileParser> parser = parserFactory.getParser(file);

        if (!parser.isPresent()){
            LOG.warn("No parser found for file: " + file);
            return;
        }

        Document document = parser.get().parse(file);

        LOG.info("ready to index document: " + document);


    }
}
