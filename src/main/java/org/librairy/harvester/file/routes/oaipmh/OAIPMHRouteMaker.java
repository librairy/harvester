/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.routes.oaipmh;

import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.language.ConstantExpression;
import org.librairy.harvester.file.routes.RouteMaker;
import org.librairy.harvester.file.routes.common.CommonRouteBuilder;
import org.librairy.boot.model.Record;
import org.librairy.boot.model.domain.resources.Domain;
import org.librairy.boot.model.domain.resources.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by cbadenes on 01/12/15.
 */
@Component
public class OAIPMHRouteMaker implements RouteMaker{

    private static final Logger LOG = LoggerFactory.getLogger(OAIPMHRouteMaker.class);

    @Override
    public boolean accept(String protocol) {
        return protocol.equalsIgnoreCase("oaipmh");
    }

    @Override
    public RouteDefinition build(Source source,Domain domain) {

        String uri = source.getUrl();

        LOG.debug("URI created for harvesting purposes: " + uri);

        return new RouteDefinition().
                from(uri).
                setProperty(Record.SOURCE_URI,  new ConstantExpression(source.getUri())).
                setProperty(Record.DOMAIN_URI,  new ConstantExpression(domain.getUri())).
                to(OAIPMHRouteBuilder.URI_RETRIEVE_METAINFORMATION).
                to(CommonRouteBuilder.URI_RO_BUILD);
    }
}
