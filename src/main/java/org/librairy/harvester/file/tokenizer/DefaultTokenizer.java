package org.librairy.harvester.file.tokenizer;

import org.apache.commons.lang3.StringUtils;
import org.librairy.harvester.file.parser.ParsedDocument;
import org.librairy.harvester.file.tokenizer.stanford.StanfordTokenizer;
import org.librairy.harvester.file.tokenizer.stanford.StanfordTokenizerEN;
import org.librairy.harvester.file.tokenizer.stanford.StanfordTokenizerES;
import org.librairy.harvester.file.utils.Serializations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
public class DefaultTokenizer implements Tokenizer{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTokenizer.class);

    private StanfordTokenizerEN stanfordTokenizerEN;

    private StanfordTokenizerES stanfordTokenizerES;

    private Map<Language, StanfordTokenizer> tokenizers;

    @PostConstruct
    public void setup(){

        tokenizers = new HashMap();

        tokenizers.put(Language.EN,new StanfordTokenizerEN());
        tokenizers.put(Language.ES,new StanfordTokenizerES());
    }


    public List<Token> tokenize(String text, Language language){
        try {

            return tokenizers.get(language).tokenize(text);
        } catch (Exception e) {
            LOG.error("Error extracting tokens from text: " + StringUtils.substring(text,0,10) + " ...",e);
            return new ArrayList<>();
        }
    }
}
