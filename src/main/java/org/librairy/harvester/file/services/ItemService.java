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
import org.apache.commons.lang.StringUtils;
import org.librairy.harvester.file.descriptor.Descriptor;
import org.librairy.harvester.file.descriptor.FileDescription;
import org.librairy.harvester.file.executor.ParallelExecutor;
import org.librairy.harvester.file.helper.LanguageHelper;
import org.librairy.harvester.file.parser.ParsedDocument;
import org.librairy.harvester.file.parser.Parser;
import org.librairy.harvester.file.tokenizer.Language;
import org.librairy.harvester.file.tokenizer.Tokenizer;
import org.librairy.model.domain.relations.Relation;
import org.librairy.model.domain.resources.Document;
import org.librairy.model.domain.resources.Item;
import org.librairy.model.domain.resources.Resource;
import org.librairy.model.utils.TimeUtils;
import org.librairy.storage.UDM;
import org.librairy.storage.generator.URIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    Tokenizer tokenizer;

    @Value("#{environment['LIBRAIRY_HOME']?:'${librairy.home}'}")
    String homeFolder;

    @Value("${librairy.harvester.folder}")
    String inputFolder;

    @Autowired
    Parser parser;

    @Autowired
    Descriptor fileDescriptor;

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
            String path = document.getRetrievedFrom();
            File file;
            if (path.startsWith("http")){
                // download file
                Path fileStorePath = Paths.get(homeFolder, inputFolder, "downloaded",URIGenerator.retrieveId(document
                        .getUri())+"."+ Files.getFileExtension(path));
                file = fileStorePath.toFile();
                FileUtils.copyURLToFile(new URL(path), file);
            }else{
                file = new File(path);
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
                udm.save(item);
                udm.save(Relation.newBundles(document.getUri(),item.getUri()));
                LOG.info("New (textual) Item: " + item.getUri() + " from Document: " + document.getUri());
            }

            // Override meta-inf of document
            FileDescription fileDescription = fileDescriptor.describe(file);

            if (Strings.isNullOrEmpty(document.getTitle()))
                document.setTitle(fileDescription.getMetaInformation().getTitle());
            if (Strings.isNullOrEmpty(document.getDescription()))
                document.setDescription(fileDescription.getSummary());
            if (Strings.isNullOrEmpty(document.getFormat()))
                document.setFormat(fileDescription.getMetaInformation().getFormat());
            if (Strings.isNullOrEmpty(document.getAuthoredBy()))
                document.setAuthoredBy(fileDescription.getMetaInformation().getCreators());
            if (Strings.isNullOrEmpty(document.getAuthoredOn()))
                document.setAuthoredOn(fileDescription.getMetaInformation().getAuthored());
            if (Strings.isNullOrEmpty(document.getRetrievedOn()))
                document.setRetrievedOn(TimeUtils.asISO());
            if (Strings.isNullOrEmpty(document.getRetrievedFrom()))
                document.setRetrievedOn(fileDescription.getMetaInformation().getPubURI());
            if (Strings.isNullOrEmpty(document.getLanguage()))
                document.setLanguage(fileDescription.getMetaInformation().getLanguage());
            if (Strings.isNullOrEmpty(document.getPublishedOn()))
                document.setPublishedOn(fileDescription.getMetaInformation().getPublished());
            if (Strings.isNullOrEmpty(document.getRights()))
                document.setRights(fileDescription.getMetaInformation().getRights());
            if (Strings.isNullOrEmpty(document.getContributedBy()))
                document.setContributedBy(fileDescription.getMetaInformation().getContributors());
            if (Strings.isNullOrEmpty(document.getPublishedBy()))
                document.setPublishedBy(fileDescription.getMetaInformation().getPubURI());

            udm.update(document);

            // -> Image Item

            // -> Workflow Item

        }catch (Exception e){
            LOG.error("Error adding item from: " + resource.getUri(), e);
        }

    }

    public void tokenize(Item item){
        // TODO Handle multiple languages
        // Language language = LanguageHelper.getLanguageFrom(file);
        String tokens = tokenizer.tokenize(item.getContent(),Language.from(item.getLanguage())).stream().
                filter(token -> token.isValid()).
                map(token -> token.getLemma()).
                collect(Collectors.joining(" "));
        item.setTokens(tokens);
        udm.update(item);
        LOG.info("Item " + item.getUri() +  " tokenized");
    }
}
