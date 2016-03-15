package org.librairy.harvester.services;

import lombok.Getter;
import lombok.Setter;
import org.librairy.harvester.annotator.TextAnnotator;
import org.librairy.harvester.annotator.upf.AnnotatedDocument;
import org.librairy.harvester.executor.ParallelExecutor;
import org.librairy.harvester.serializer.AnnotatedDocumentSerializer;
import org.librairy.model.domain.relations.Relation;
import org.librairy.model.domain.resources.Item;
import org.librairy.model.domain.resources.Part;
import org.librairy.model.domain.resources.Resource;
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
public class PartsService {

    private static final Logger LOG = LoggerFactory.getLogger(PartsService.class);

    @Autowired
    UDM udm;

    @Autowired
    URIGenerator uriGenerator;

    @Autowired
    TextAnnotator textMiner;

    @Autowired
    ParallelExecutor executor;

    @Getter
    @Setter
    @Value("${parser.serializer.directory}")
    String serializationDirectory;

    @Value("${librairy.harvester.folder.input}")
    protected String inputFolder;


    public void handleParallel(Resource resource){
        executor.execute(() -> handle(resource));
    }


    public void handle(Resource resource){
        try{

            // Read Item
            Optional<Resource> res = udm.read(Resource.Type.ITEM).byUri(resource.getUri());

            if (!res.isPresent()){
                LOG.warn("No document found by uri: " + resource.getUri());
                return;
            }

            Item item = res.get().asItem();

            String id = uriGenerator.retrieveId(item.getUri());

            // Check if exists a previously serialized file
            String serializedPath   = new StringBuilder().append(serializationDirectory).append(java.io.File.separator).append(id).append(".ser").toString();

            AnnotatedDocument annotatedDocument;
            if (new File(serializedPath).exists()){
                // Load Serialized Document
                annotatedDocument = AnnotatedDocumentSerializer.from(serializedPath);
            }else{
                String path = item.getUrl();
                // Parse document
                annotatedDocument = textMiner.annotateFile(path);
                // Save serialized file
                AnnotatedDocumentSerializer.to(annotatedDocument,serializedPath);
            }


            // Part:: Abstract
            createAndSavePart("abstract", annotatedDocument.getAbstractContent(), item.getUri());

            // Part:: Approach
            createAndSavePart("approach", annotatedDocument.getApproachContent(), item.getUri());

            // Part:: Background
            createAndSavePart("background", annotatedDocument.getBackgroundContent(), item.getUri());

            // Part:: Challenge
            createAndSavePart("challenge", annotatedDocument.getChallengeContent(), item.getUri());

            // Part:: Outcome
            createAndSavePart("outcome", annotatedDocument.getOutcomeContent(), item.getUri());

            // Part:: FutureWork
            createAndSavePart("futureWork", annotatedDocument.getFutureWorkContent(), item.getUri());

            // Part:: Summary by Centroid
            createAndSavePart("summaryCentroid", annotatedDocument.getSummaryByCentroidContent(25), item.getUri());

            // Part:: Summary by Title Similarity
            createAndSavePart("summaryTitle", annotatedDocument.getSummaryByTitleSimContent(25), item.getUri());


        }catch (Exception e){
            LOG.error("Error adding parts from: " + resource.getUri(), e);
        }
    }

    private Part createAndSavePart(String sense, String rawContent, String itemUri) {
        Part part = Resource.newPart();
        part.setUri(uriGenerator.basedOnContent(Resource.Type.PART,rawContent + sense));
        part.setSense(sense);
        part.setContent(rawContent);

        String tokens   = textMiner.tokenize(rawContent).stream().filter(token -> token.isValid()).map(token -> token.getLemma()).collect(Collectors.joining(" "));
        part.setTokens(tokens);

        udm.save(part);
        udm.save(Relation.newDescribes(part.getUri(),itemUri));

        return part;
    }


}
