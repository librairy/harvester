/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.routes.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.librairy.boot.model.Record;
import org.librairy.boot.model.domain.resources.File;
import org.librairy.boot.model.domain.resources.MetaInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by cbadenes on 01/12/15.
 */
@Component
public class ResourceBuilder implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceBuilder.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        try{
            // Domain URI
            String domainUri        = exchange.getProperty(Record.DOMAIN_URI,String.class);

            // Source URI
            String sourceUri        = exchange.getProperty(Record.SOURCE_URI,String.class);

            LOG.debug("Processing resource: " + exchange + " from source: " + sourceUri + " and domain: " + domainUri);

            // Attached file
            // TODO Handle multiple attached files
            String path             = exchange.getProperty(Record.PUBLICATION_URL_LOCAL,String.class);

            // MetaInformation
            MetaInformation metaInformation = new MetaInformation();
            metaInformation.setRights(exchange.getProperty(Record.PUBLICATION_RIGHTS,String.class));
            metaInformation.setDescription(exchange.getProperty(Record.PUBLICATION_DESCRIPTION,String.class));
            metaInformation.setLanguage(exchange.getProperty(Record.PUBLICATION_LANGUAGE,String.class));
            metaInformation.setContributors(exchange.getProperty(Record.PUBLICATION_CONTRIBUTORS,String.class));
            metaInformation.setCreators(exchange.getProperty(Record.PUBLICATION_CREATORS,String.class));
            metaInformation.setAuthored(exchange.getProperty(Record.PUBLICATION_AUTHORED,String.class));
            metaInformation.setFormat(exchange.getProperty(Record.PUBLICATION_FORMAT,String.class));
            metaInformation.setPubFormat(exchange.getProperty(Record.PUBLICATION_METADATA_FORMAT,String.class));
            metaInformation.setPublished(exchange.getProperty(Record.PUBLICATION_PUBLISHED,String.class));
            metaInformation.setPubURI(exchange.getProperty(Record.PUBLICATION_URI,String.class));
            metaInformation.setTitle(exchange.getProperty(Record.PUBLICATION_TITLE,String.class));
            metaInformation.setSubject(exchange.getProperty(Record.PUBLICATION_SUBJECT,String.class));
            metaInformation.setType(exchange.getProperty(Record.PUBLICATION_TYPE,String.class));

            // File
            File file = new File();
            file.setDomain(domainUri);
            file.setSource(sourceUri);
            file.setUrl(path);
            file.setMetaInformation(metaInformation);
            LOG.debug("file created: " + file);

            //TODO publish to event-bus

            // Put in camel flow
            exchange.getIn().setBody(file, File.class);

        }catch (RuntimeException e){
            LOG.error("Error creating resources", e);
        }

    }
}
