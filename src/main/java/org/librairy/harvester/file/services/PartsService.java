package org.librairy.harvester.file.services;

import org.librairy.harvester.file.annotator.AnnotatedDocument;
import org.librairy.harvester.file.annotator.Annotator;
import org.librairy.harvester.file.executor.ParallelExecutor;
import org.librairy.harvester.file.tokenizer.Language;
import org.librairy.harvester.file.tokenizer.Tokenizer;
import org.librairy.model.domain.relations.Relation;
import org.librairy.model.domain.resources.Part;
import org.librairy.model.domain.resources.Resource;
import org.librairy.storage.UDM;
import org.librairy.storage.generator.URIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    Annotator annotator;

    @Autowired
    Tokenizer tokenizer;

    private ParallelExecutor executor;

    @PostConstruct
    public void setup(){
        this.executor = new ParallelExecutor();
    }

    public void handleParallel(Resource resource){
        executor.execute(() -> handle(resource));
    }


    public void handle(Resource resource){
        try{

            AnnotatedDocument annotatedDocument = annotator.annotate(resource.getUri());

            //Rhethorical Classes
            annotatedDocument.getRhetoricalClasses().entrySet().stream().forEach(rclass -> createAndSavePart(rclass
                    .getKey(),rclass.getValue(),resource.getUri()));

            //Sections
            annotatedDocument.getSections().entrySet().stream().forEach(rclass -> createAndSavePart(rclass.getKey(),rclass
                    .getValue(),resource.getUri()));


        }catch (Exception e){
            LOG.error("Error adding parts from: " + resource.getUri(), e);
        }
    }

    private Part createAndSavePart(String sense, String rawContent, String itemUri) {
        Part part = Resource.newPart(rawContent);
        part.setUri(uriGenerator.basedOnContent(Resource.Type.PART,rawContent + sense));
        part.setSense(sense);

        String tokens   = tokenizer.tokenize(rawContent, Language.EN).stream().
                filter(token -> token.isValid()).
                map(token -> token.getLemma()).
                collect(Collectors.joining(" "));
        part.setTokens(tokens);

        udm.save(part);
        udm.save(Relation.newDescribes(part.getUri(),itemUri));
        LOG.info("A new part has been created: " + part.getUri() + " from Item: " + itemUri);
        return part;
    }


}
