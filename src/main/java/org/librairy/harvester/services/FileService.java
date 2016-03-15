package org.librairy.harvester.services;

import org.apache.commons.lang.StringUtils;
import org.librairy.harvester.executor.ParallelExecutor;
import org.librairy.harvester.parser.IParser;
import org.librairy.harvester.parser.MetaInformation;
import org.librairy.harvester.parser.ParserFactory;
import org.librairy.model.domain.relations.Relation;
import org.librairy.model.domain.resources.Document;
import org.librairy.model.domain.resources.File;
import org.librairy.model.domain.resources.Resource;
import org.librairy.model.utils.TimeUtils;
import org.librairy.storage.UDM;
import org.librairy.storage.generator.URIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by cbadenes on 11/02/16.
 */
@Component
public class FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    @Autowired
    UDM udm;

    @Autowired
    URIGenerator uriGenerator;

    @Autowired
    ParserFactory parserFactory;

    @Autowired
    ParallelExecutor executor;

    @Value("${librairy.harvester.folder.input}")
    protected String inputFolder;

    public void handleParallel(File file){
        executor.execute(() -> handle(file));
    }

    public void handle(File file){
        // Create a new Document
        try{
            LOG.info("Processing file: " + file );

            // Domain URI
            String domainUri        = file.getDomain();

            // Source URI
            String sourceUri        = file.getSource();

            // Document File
            String path             = file.getUrl();


            java.io.File ioFile = new java.io.File(path);

            if (!ioFile.exists()){
                LOG.warn("File does not exist: " + ioFile.getAbsolutePath() + " from: " + file);
                return;
            }

            // Parser
            IParser parser = parserFactory.parserOf(ioFile);

            // Metainformation
            MetaInformation metaInformation = parser.getMetaInformation();
            metaInformation.setSourceUri(sourceUri);

            // Check if exist document based on title
            List<String> docs = udm.find(Resource.Type.DOCUMENT).by(Document.TITLE,metaInformation.getTitle());

            if (docs != null && !docs.isEmpty()){
                LOG.warn("Document titled: '"+metaInformation.getTitle()+"' exists in ddbb with uri: " + docs);
                return;
            }

            // Document
            String description = parser.getDescription();
            Document document = Resource.newDocument();
            document.setUri(uriGenerator.basedOnContent(Resource.Type.DOCUMENT,description));
            document.setPublishedOn(metaInformation.getPublished());
            document.setPublishedBy(metaInformation.getSourceUri());
            document.setAuthoredOn(metaInformation.getAuthored());
            document.setAuthoredBy(metaInformation.getCreators());
            document.setContributedBy(metaInformation.getContributors());

            String fileName = StringUtils.substringAfter(ioFile.getAbsolutePath(),inputFolder);
            document.setRetrievedFrom(fileName);

            document.setRetrievedOn(TimeUtils.asISO());
            document.setFormat(metaInformation.getPubFormat());
            document.setLanguage(metaInformation.getLanguage());
            document.setTitle(metaInformation.getTitle());
            document.setSubject(metaInformation.getSubject());
            document.setDescription(description);
            document.setRights(metaInformation.getRights());
            document.setType(metaInformation.getType());


            LOG.info("Adding new document: " + document.getUri());
            udm.save(document);
            // Relate it to Source
            udm.save(Relation.newProvides(sourceUri,document.getUri()));
            // Relate it to Domain
            udm.save(Relation.newContains(domainUri,document.getUri()));

        }catch (RuntimeException e){
            LOG.error("Error processing resource: " + file, e);
        }
    }

}
