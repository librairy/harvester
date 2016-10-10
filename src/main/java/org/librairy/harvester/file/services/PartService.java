/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.services;

import org.librairy.harvester.file.tokenizer.Language;
import org.librairy.harvester.file.tokenizer.Tokenizer;
import org.librairy.model.domain.resources.Part;
import org.librairy.storage.UDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Created by cbadenes on 11/02/16.
 */
@Component
public class PartService {

    private static final Logger LOG = LoggerFactory.getLogger(PartService.class);

    @Autowired
    UDM udm;

    @Autowired
    Tokenizer tokenizer;

    public void tokenize(Part part){
        // TODO Handle multiple languages
        // Language language = LanguageHelper.getLanguageFrom(file);
        String tokens = tokenizer.tokenize(part.getContent(),Language.EN).stream().
                filter(token -> token.isValid()).
                map(token -> token.getLemma()).
                collect(Collectors.joining(" "));
        part.setTokens(tokens);
        udm.update(part);
        LOG.debug("Part: " + part.getUri() + " tokenized!");
    }
}
