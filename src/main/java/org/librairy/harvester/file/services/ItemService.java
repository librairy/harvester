/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.services;

import com.google.common.base.Strings;
import org.librairy.harvester.file.executor.ParallelExecutor;
import org.librairy.harvester.file.parser.ParsedDocument;
import org.librairy.harvester.file.parser.Parser;
import org.librairy.model.domain.relations.Relation;
import org.librairy.model.domain.resources.Document;
import org.librairy.model.domain.resources.Item;
import org.librairy.model.domain.resources.Resource;
import org.librairy.storage.UDM;
import org.librairy.storage.generator.URIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by cbadenes on 11/02/16.
 */
@Component
public class ItemService {

    private static final Logger LOG = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    UDM udm;

    @Autowired
    URIGenerator uriGenerator;

    @Value("#{environment['LIBRAIRY_HOME']?:'${librairy.home}'}")
    String homeFolder;

    @Value("${librairy.harvester.folder}")
    String inputFolder;

    @Autowired
    Parser parser;

    @Autowired
    DocumentService documentService;

    @PostConstruct
    public void setup(){
        this.executor = new ParallelExecutor();
    }

    private ParallelExecutor executor;

    public void handleParallel(Resource resource){
        executor.execute(() -> handle(resource));
    }

    public void handle(Resource resource){

        try{
            //Read document from db
            Optional<Resource> res = udm.read(Resource.Type.DOCUMENT).byUri(resource.getUri());

            if (!res.isPresent()){
                LOG.warn("Received event of unknown resource: " + resource.getUri());
                return;
            }

            Document document = (Document) res.get();

            // Parsing File of the Document
            String fileName = URIGenerator.retrieveId(document.getUri());
            Path filePath   = Paths.get(homeFolder, inputFolder,fileName+"."+document.getFormat());
            File file       = filePath.toFile();

            if (!file.exists() && !Strings.isNullOrEmpty(document.getRetrievedFrom())) {
                documentService.retrieveFile(document.getRetrievedFrom(), file);
            }else if (!file.exists()){
                LOG.warn("No file associated to document: " + document);
                return;
            }

            ParsedDocument parsedDocument = parser.parse(file);

            // -> Textual Item
            String textualContent = parsedDocument.getText();
            if (!Strings.isNullOrEmpty(textualContent)){
                Item item = Resource.newItem(textualContent);
                item.setFormat("text");
                item.setUrl(file.getAbsolutePath());
                item.setLanguage(document.getLanguage());
                item.setTokens("");
                item.setAuthoredBy(document.getAuthoredBy());
                item.setType("text");
                udm.save(item);
                udm.save(Relation.newBundles(document.getUri(),item.getUri()));
                LOG.info("New (textual) Item: " + item.getUri() + " from Document: " + document.getUri());
            }

            // -> Image Item

            // -> Workflow Item

        }catch (Exception e){
            LOG.error("Error adding item from: " + resource.getUri(), e);
        }

    }
}
