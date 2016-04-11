package org.librairy.harvester.file.tokenizer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
@Component
public class TextTokenizer {

    private static final Logger LOG = LoggerFactory.getLogger(TextTokenizer.class);

    @Autowired
    Tokenizer tokenizer;

    public List<Token> tokenize(String text){
        try {
            return tokenizer.tokenize(text);
        } catch (Exception e) {
            LOG.error("Error extracting tokens from text: " + StringUtils.substring(text,0,10) + " ...",e);
            return new ArrayList<>();
        }
    }
}
