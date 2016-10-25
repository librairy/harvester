/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.services;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.librairy.harvester.file.descriptor.Descriptor;
import org.librairy.harvester.file.descriptor.FileDescription;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    Descriptor fileDescriptor;

    @Autowired
    EventBus eventBus;

    @Value("${LIBRAIRY_FILE_URI:false}")
    Boolean useFileNameAsUri;

    @Value("#{environment['LIBRAIRY_HOME']?:'${librairy.home}'}")
    String homeFolder;

    @Value("${librairy.harvester.folder}")
    String inputFolder;

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
            // Document File

            String fileExtension    = Files.getFileExtension(file.getUrl());
            String fileName         = Files.getNameWithoutExtension(file.getUrl());
            String tmpFileName      = System.currentTimeMillis()+"-"+fileName+"."+fileExtension;

            Path tmpFilePath        = Paths.get(homeFolder, inputFolder,tmpFileName);

            retrieveFile(file.getUrl(), tmpFilePath.toFile());

            FileDescription fileDescription = fileDescriptor.describe(tmpFilePath.toFile());

            // Document

            Document document = Resource.newDocument();
            // -> uri
            if (useFileNameAsUri){
                document.setUri(uriGenerator.from(Resource.Type.DOCUMENT, fileName));
            }else{
                document.setUri(uriGenerator.basedOnContent(Resource.Type.DOCUMENT,file.getUrl()));
            }
            String title = (Strings.isNullOrEmpty(fileDescription.getMetaInformation().getTitle()))? fileName
                :fileDescription.getMetaInformation().getTitle();
            document.setTitle(title);
            // -> publishedOn
            document.setPublishedOn(fileDescription.getMetaInformation().getPublished());
            // -> publishedBy
//            document.setPublishedBy(sourceUri);
            // -> authoredOn
            document.setAuthoredOn(fileDescription.getMetaInformation().getAuthored());
            // -> authoredBy
            document.setAuthoredBy(fileDescription.getMetaInformation().getCreators());
            // -> contributedBy
            document.setContributedBy(fileDescription.getMetaInformation().getContributors());
            // -> retrievedFrom
            document.setRetrievedFrom(file.getUrl());
            // -> retrievedOn
            document.setRetrievedOn(TimeUtils.asISO());
            // -> format
            document.setFormat(fileDescription.getMetaInformation().getPubFormat());
            // -> language
            document.setLanguage(fileDescription.getMetaInformation().getLanguage());
            // -> subject
            document.setSubject(fileDescription.getMetaInformation().getSubject());
            // -> description
            document.setDescription(fileDescription.getSummary());
            // -> rights
            document.setRights(fileDescription.getMetaInformation().getRights());
            // -> type
            document.setType(fileDescription.getMetaInformation().getType());

            udm.save(document);
            LOG.info("New document: " + document.getUri() + " from file: " + file.getUrl());

            // Move temporal file to final
            String finalFileName    = URIGenerator.retrieveId(document.getUri())+"."+fileExtension;
            FileUtils.moveFile(tmpFilePath.toFile(), Paths.get(homeFolder, inputFolder,finalFileName).toFile());

            // Relate it to Source
            if (!Strings.isNullOrEmpty(file.getSource()))
                udm.save(Relation.newProvides(file.getSource(),document.getUri()));
            // Relate it to Domain
            if (!Strings.isNullOrEmpty(file.getDomain()))
                udm.save(Relation.newContains(file.getDomain(),document.getUri()));

            // Relate it to Document
            if (!Strings.isNullOrEmpty(file.getAggregatedFrom()))
                udm.save(Relation.newAggregates(file.getAggregatedFrom(),document.getUri()));

            // Aggregated Files
            if (!fileDescription.getAggregatedFiles().isEmpty()){

                for(java.io.File aggregatedFile: fileDescription.getAggregatedFiles()){

                    File aggregatedFileDesc = new File();
                    aggregatedFileDesc.setDomain(file.getDomain());
                    aggregatedFileDesc.setSource(file.getSource());
                    aggregatedFileDesc.setUrl(aggregatedFile.getAbsolutePath());
                    aggregatedFileDesc.setAggregatedFrom(document.getUri());

                    LOG.debug("Publishing event from aggregated file: " + aggregatedFileDesc);
                    eventBus.post(Event.from(aggregatedFileDesc), RoutingKey.of(FileCreatedEventHandler.ROUTING_KEY));
                }

            }

        }catch (RuntimeException e){
            LOG.error("Error adding document from: " + file + ". Reason: " + e.getMessage(),e);
        }catch (Exception e){
            LOG.error("Error adding document from: " + file, e);
        }
    }


    public void retrieveFile(String url, java.io.File output) throws IOException, InterruptedException {
        if (url.startsWith("http")){
            FileUtils.copyURLToFile(new URL(url),output);
        }else{
            int retries = 0;
            IOException error = null;
            do{
                try{
                    FileUtils.copyFile(Paths.get(url).toFile(), output);
                    break;
                }catch (IOException e){
                    retries +=1;
                    error = e;
                    LOG.warn(e.getMessage());
                    Thread.sleep(2000);
                }
            }while(retries < 5);
            if (retries>=5) throw error;
        }
    }

}
