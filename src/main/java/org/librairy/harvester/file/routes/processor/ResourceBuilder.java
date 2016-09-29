/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.routes.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.librairy.model.Record;
import org.librairy.model.domain.resources.File;
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

            // File
            File file = new File();
            file.setDomain(domainUri);
            file.setSource(sourceUri);
            file.setUrl(path);
            LOG.debug("file created: " + file);

            //TODO publish to event-bus

            // Put in camel flow
            exchange.getIn().setBody(file, File.class);

        }catch (RuntimeException e){
            LOG.error("Error creating resources", e);
        }

    }
}
