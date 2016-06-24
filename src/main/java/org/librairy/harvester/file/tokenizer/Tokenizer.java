package org.librairy.harvester.file.tokenizer;

import java.util.List;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
public interface Tokenizer {

    List<Token> tokenize(String text, Language language);

}
