package org.librairy.harvester.file.services;

import lombok.Getter;
import lombok.Setter;
import org.librairy.harvester.file.annotator.TextAnnotator;
import org.librairy.harvester.file.executor.ParallelExecutor;
import org.librairy.harvester.file.tokenizer.TextTokenizer;
import org.librairy.model.domain.relations.Relation;
import org.librairy.model.domain.resources.Part;
import org.librairy.model.domain.resources.Resource;
import org.librairy.storage.UDM;
import org.librairy.storage.generator.URIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    TextAnnotator textAnnotator;

    @Autowired
    TextTokenizer textTokenizer;

    @Getter
    @Setter
    @Value("${harvester.input.folder.serial}")
    String serializationDirectory;

    @Value("${harvester.input.folder.external}")
    protected String inputFolder;


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

            // Read Item
//            Optional<Resource> res = udm.read(Resource.Type.ITEM).byUri(resource.getUri());
//
//            if (!res.isPresent()){
//                LOG.warn("No document found by uri: " + resource.getUri());
//                return;
//            }
//            Item item = res.get().asItem();



        }catch (Exception e){
            LOG.error("Error adding parts from: " + resource.getUri(), e);
        }
    }

    private Part createAndSavePart(String sense, String rawContent, String itemUri) {
        Part part = Resource.newPart();
        part.setUri(uriGenerator.basedOnContent(Resource.Type.PART,rawContent + sense));
        part.setSense(sense);
        part.setContent(rawContent);

        String tokens   = textTokenizer.tokenize(rawContent).stream().
                filter(token -> token.isValid()).
                map(token -> token.getLemma()).
                collect(Collectors.joining(" "));
        part.setTokens(tokens);

        udm.save(part);
        udm.save(Relation.newDescribes(part.getUri(),itemUri));

        return part;
    }


}
