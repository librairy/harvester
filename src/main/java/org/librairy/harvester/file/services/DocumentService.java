package org.librairy.harvester.file.services;

import com.google.common.base.Strings;
import it.uniroma1.lcl.jlt.util.Files;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;
import org.librairy.harvester.file.descriptor.FileDescription;
import org.librairy.harvester.file.descriptor.FileDescriptor;
import org.librairy.harvester.file.eventbus.FileCreatedEventHandler;
import org.librairy.harvester.file.executor.ParallelExecutor;
import org.librairy.model.Event;
import org.librairy.model.domain.relations.Relation;
import org.librairy.model.domain.resources.Document;
import org.librairy.model.domain.resources.File;
import org.librairy.model.domain.resources.Resource;
import org.librairy.model.modules.EventBus;
import org.librairy.model.modules.RoutingKey;
import org.librairy.model.utils.TimeUtils;
import org.librairy.storage.UDM;
import org.librairy.storage.generator.URIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cbadenes on 11/02/16.
 */
@Component
public class DocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    UDM udm;

    @Autowired
    URIGenerator uriGenerator;

    @Autowired
    FileDescriptor fileDescriptor;

    @Autowired
    EventBus eventBus;

    private ParallelExecutor executor;

    @PostConstruct
    public void setup(){

        this.executor = new ParallelExecutor();
    }

    public void handleParallel(File file){
        this.executor.execute(() -> handle(file));
    }

    public void handle(File file){
        // Create a new Document
        try{
            //TODO Move file to .done/ folder

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

            // Check is exists
            String docURI = uriGenerator.from(Resource.Type.DOCUMENT,Files.getFileNameWithoutExtension(ioFile
                    .getAbsolutePath()));
//            if (udm.exists(Resource.Type.DOCUMENT).withUri(docURI)){
//                LOG.warn("Document from file: '"+ioFile.getAbsolutePath()+"' already exists in" + "ddbb with uri: " +
//                        docURI);
//
//                //TODO Set this domain and this source
//
//                return;
//            }

            // Retrieve File Description
            FileDescription fileDescription = fileDescriptor.describe(ioFile);

//            // Check if exist document based on title
//            List<String> docs = udm.find(Resource.Type.DOCUMENT).by(Document.TITLE,fileDescription.getMetaInformation().getTitle());
//            if (docs != null && !docs.isEmpty()){
//                LOG.warn("Document titled: '"+fileDescription.getMetaInformation().getTitle()+"' already exists in " +
//                        "ddbb with uri: " + docs);
//
//                // Set this domain and this source
//
//                return;
//            }

            // Document
            Document document = Resource.newDocument();
            // -> uri
            //document.setUri(uriGenerator.basedOnContent(Resource.Type.DOCUMENT,fileDescription.getSummary()));
            document.setUri(docURI);
            // -> publishedOn
            document.setPublishedOn(fileDescription.getMetaInformation().getPublished());
            // -> publishedBy
            document.setPublishedBy(sourceUri);
            // -> authoredOn
            document.setAuthoredOn(fileDescription.getMetaInformation().getAuthored());
            // -> authoredBy
            document.setAuthoredBy(fileDescription.getMetaInformation().getCreators());
            // -> contributedBy
            document.setContributedBy(fileDescription.getMetaInformation().getContributors());
            // -> retrievedFrom
            document.setRetrievedFrom(ioFile.getAbsolutePath());
            // -> retrievedOn
            document.setRetrievedOn(TimeUtils.asISO());
            // -> format
            document.setFormat(fileDescription.getMetaInformation().getPubFormat());
            // -> language
            document.setLanguage(fileDescription.getMetaInformation().getLanguage());
            // -> title
            document.setTitle(fileDescription.getMetaInformation().getTitle());
            // -> subject
            document.setSubject(fileDescription.getMetaInformation().getSubject());
            // -> description
            document.setDescription(fileDescription.getSummary());
            // -> rights
            document.setRights(fileDescription.getMetaInformation().getRights());
            // -> type
            document.setType(fileDescription.getMetaInformation().getType());

            udm.save(document);
            LOG.info("Added document: " + document.getUri());


            // Relate it to Source
            udm.save(Relation.newProvides(sourceUri,docURI));
            // Relate it to Domain
            udm.save(Relation.newContains(domainUri,docURI));
            // Relate it to Document
            if (!Strings.isNullOrEmpty(file.getAggregatedFrom())){
                udm.save(Relation.newAggregates(file.getAggregatedFrom(),docURI));
            }



            // Aggregated Files
            if (!fileDescription.getAggregatedFiles().isEmpty()){

                for(java.io.File aggregatedFile: fileDescription.getAggregatedFiles()){

                    File aggregatedFileDesc = new File();
                    aggregatedFileDesc.setDomain(domainUri);
                    aggregatedFileDesc.setSource(sourceUri);
                    aggregatedFileDesc.setUrl(aggregatedFile.getAbsolutePath());
                    aggregatedFileDesc.setAggregatedFrom(docURI);

                    LOG.info("Publishing event from aggregated file: " + aggregatedFileDesc);
                    eventBus.post(Event.from(aggregatedFileDesc), RoutingKey.of(FileCreatedEventHandler.ROUTING_KEY));
                }

            }

        }catch (Exception e){
            LOG.error("Error adding document from: " + file, e);
        }
    }

}
