package org.librairy.harvester.services;

import org.librairy.harvester.annotator.TextAnnotator;
import org.librairy.harvester.executor.ParallelExecutor;
import org.librairy.harvester.parser.ParserFactory;
import org.librairy.model.domain.relations.Relation;
import org.librairy.model.domain.resources.Document;
import org.librairy.model.domain.resources.Item;
import org.librairy.model.domain.resources.Resource;
import org.librairy.model.utils.ResourceUtils;
import org.librairy.storage.UDM;
import org.librairy.storage.generator.URIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
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
    TextAnnotator textMiner;

    @Autowired
    ParserFactory parserFactory;

    @Autowired
    ParallelExecutor executor;

    @Value("${librairy.harvester.folder.input}")
    protected String inputFolder;

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

            String path = inputFolder + document.getRetrievedFrom();

            File file = new File(path);

            // Textual Item
            Item item = ResourceUtils.map(document,Item.class);
            item.setUri(uriGenerator.basedOnContent(Resource.Type.ITEM,document.getDescription()));
            item.setFormat("txt");
            item.setUrl(path);
            item.setContent(parserFactory.parserOf(file).getContent());

            String tokens   = textMiner.tokenize(item.getContent()).stream().filter(token -> token.isValid()).map(token -> token.getLemma()).collect(Collectors.joining(" "));
            item.setTokens(tokens);

            LOG.info("Adding new item: " + item.getUri());
            udm.save(item);
            udm.save(Relation.newBundles(document.getUri(),item.getUri()));
        }catch (Exception e){
            LOG.error("Error adding item from: " + resource.getUri(), e);
        }

    }

}
