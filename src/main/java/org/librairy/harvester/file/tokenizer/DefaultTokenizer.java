package org.librairy.harvester.file.tokenizer;

import org.apache.commons.lang3.StringUtils;
import org.librairy.harvester.file.tokenizer.stanford.StanfordTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
public class DefaultTokenizer implements Tokenizer{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTokenizer.class);

    private StanfordTokenizer stanfordTokenizer;

    @PostConstruct
    public void setup(){
        stanfordTokenizer = new StanfordTokenizer();
        stanfordTokenizer.setup();
    }


    public List<Token> tokenize(String text){
        try {
            return stanfordTokenizer.tokenize(text);
        } catch (Exception e) {
            LOG.error("Error extracting tokens from text: " + StringUtils.substring(text,0,10) + " ...",e);
            return new ArrayList<>();
        }
    }
}
