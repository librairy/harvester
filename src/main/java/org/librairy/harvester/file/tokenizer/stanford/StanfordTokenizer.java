/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.tokenizer.stanford;

import org.librairy.harvester.file.tokenizer.Token;

import java.util.List;

/**
 * Created on 01/05/16:
 *
 * @author cbadenes
 */
public interface StanfordTokenizer {

    List<Token> tokenize(String text);
}
