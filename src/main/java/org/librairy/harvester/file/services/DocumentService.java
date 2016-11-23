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
import org.librairy.boot.model.Event;
import org.librairy.boot.model.domain.relations.Relation;
import org.librairy.boot.model.domain.resources.File;
import org.librairy.boot.model.domain.resources.Item;
import org.librairy.boot.model.domain.resources.Resource;
import org.librairy.boot.model.modules.EventBus;
import org.librairy.boot.model.modules.RoutingKey;
import org.librairy.boot.storage.UDM;
import org.librairy.boot.storage.generator.URIGenerator;
import org.librairy.harvester.file.descriptor.Descriptor;
import org.librairy.harvester.file.descriptor.FileDescription;
import org.librairy.harvester.file.eventbus.FileCreatedEventHandler;
import org.librairy.harvester.file.executor.ParallelExecutor;
import org.librairy.harvester.file.parser.ParsedDocument;
import org.librairy.harvester.file.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
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

    @Autowired
    Parser parser;

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


            ParsedDocument parsedDocument = parser.parse(tmpFilePath.toFile());

            String textualContent = parsedDocument.getText();

            if (Strings.isNullOrEmpty(textualContent)){
                LOG.warn("Empty file: " + file.getUrl());
                return;
            }

            Item item = Resource.newItem(parsedDocument.getText());

            // -> uri
            if (useFileNameAsUri){
                item.setUri(uriGenerator.from(Resource.Type.ITEM, fileName));
            }else{
                item.setUri(uriGenerator.basedOnContent(Resource.Type.ITEM,file.getUrl()));
            }

            String title = (Strings.isNullOrEmpty(fileDescription.getMetaInformation().getTitle()))? fileName
                :fileDescription.getMetaInformation().getTitle();
            item.setDescription(title);

            // -> authoredBy
            item.setAuthoredBy(fileDescription.getMetaInformation().getCreators());

            // -> retrievedFrom
            item.setUrl(file.getUrl());

            // -> format
            item.setFormat(fileDescription.getMetaInformation().getPubFormat());

            // -> language
            item.setLanguage(fileDescription.getMetaInformation().getLanguage());

            // -> type
            item.setType(fileDescription.getMetaInformation().getType());


            // Move temporal file to final
            String finalFileName    = URIGenerator.retrieveId(item.getUri())+"."+fileExtension;
            FileUtils.moveFile(tmpFilePath.toFile(), Paths.get(homeFolder, inputFolder,finalFileName).toFile());

            udm.save(item);
            LOG.info("New Item: " + item.getUri() + " from file: " + file.getUrl());

            // Relate it to Source
            if (!Strings.isNullOrEmpty(file.getSource()))
                udm.save(Relation.newProvides(file.getSource(),item.getUri()));
            // Relate it to Domain
            if (!Strings.isNullOrEmpty(file.getDomain()))
                udm.save(Relation.newContains(file.getDomain(),item.getUri()));

            // Relate it to Document
            if (!Strings.isNullOrEmpty(file.getAggregatedFrom()))
                udm.save(Relation.newAggregates(file.getAggregatedFrom(),item.getUri()));

            // Aggregated Files
            if (!fileDescription.getAggregatedFiles().isEmpty()){

                for(java.io.File aggregatedFile: fileDescription.getAggregatedFiles()){

                    File aggregatedFileDesc = new File();
                    aggregatedFileDesc.setDomain(file.getDomain());
                    aggregatedFileDesc.setSource(file.getSource());
                    aggregatedFileDesc.setUrl(aggregatedFile.getAbsolutePath());
                    aggregatedFileDesc.setAggregatedFrom(item.getUri());

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
