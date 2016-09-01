package org.librairy.harvester.file.routes.oaipmh;

import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.language.ConstantExpression;
import org.librairy.harvester.file.routes.RouteMaker;
import org.librairy.harvester.file.routes.common.CommonRouteBuilder;
import org.librairy.model.Record;
import org.librairy.model.domain.resources.Source;
import org.librairy.model.domain.resources.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

/**
 * Created by cbadenes on 01/12/15.
 */
@Component
public class OAIPMHRouteMaker implements RouteMaker{

    private static final Logger LOG = LoggerFactory.getLogger(OAIPMHRouteMaker.class);

    @Value("#{environment['LIBRAIRY_HOME']?:'${librairy.home}'}")
    String homeFolder;

    @Value("${librairy.harvester.folder}")
    String inputFolder;

    @Value("${librairy.harvester.folder.hoarder}")
    protected String basedir;

    @Override
    public boolean accept(String protocol) {
        return protocol.equalsIgnoreCase("oaipmh");
    }

    @Override
    public RouteDefinition build(Source source,Domain domain) {

        String uri = new StringBuilder().
                append("file:").
                append(Paths.get(homeFolder,inputFolder,basedir).toFile().getAbsolutePath()).
                append("/oaipmh/").
                append(source.getName()).
                append("?recursive=true&include=.*.xml&doneFileName=${file:name}.done").
                toString();

        LOG.debug("URI created for harvesting purposes: " + uri);

        return new RouteDefinition().
                from(uri).
                setProperty(Record.SOURCE_URI,  new ConstantExpression(source.getUri())).
                setProperty(Record.DOMAIN_URI,  new ConstantExpression(domain.getUri())).
                to(OAIPMHRouteBuilder.URI_RETRIEVE_METAINFORMATION).
                to(CommonRouteBuilder.URI_RO_BUILD);
    }
}
