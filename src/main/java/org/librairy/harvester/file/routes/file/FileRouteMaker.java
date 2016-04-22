package org.librairy.harvester.file.routes.file;

import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.model.language.SimpleExpression;
import org.apache.commons.lang.StringUtils;
import org.librairy.harvester.file.routes.RouteMaker;
import org.librairy.harvester.file.routes.common.CommonRouteBuilder;
import org.librairy.model.Record;
import org.librairy.model.domain.resources.Domain;
import org.librairy.model.domain.resources.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by cbadenes on 01/12/15.
 */
@Component
public class FileRouteMaker implements RouteMaker{

    private static final Logger LOG = LoggerFactory.getLogger(FileRouteMaker.class);

    @Value("${harvester.input.folder.external}")
    protected String inputFolder;

    @Value("${harvester.input.folder.default}")
    protected String defaultFolder;

    @Override
    public boolean accept(String protocol) {
        return protocol.equalsIgnoreCase("file");
    }

    @Override
    public RouteDefinition build(Source source,Domain domain) {

        Path folder = Paths.get(inputFolder, StringUtils.substringAfter(source.getUrl(),"//"));
        if (source.getName().equalsIgnoreCase("default")){
            folder = Paths.get(defaultFolder);
        }


        String uri = new StringBuilder().
                //append("file2i:").
                append("file2i:").
                append(folder.toFile().getAbsolutePath()).
                append("?" +
                        "autoCreate=true&" +
                        "recursive=true&" +
                        "noop=true&" +
                        "chmod=777&" +
                        "exclude=.*\\.ser&" +
                        "delete=false&" +
                        "processStrategy=#customProcessStrategy&" +
                        "readLock=changed&" +
                        "readLockCheckInterval=2000&" +
                        "idempotent=true&" +
                        "idempotentKey=${file:name}-${file:size}&" +
                        "idempotentRepository=#fileStore"
                ).
                toString();

        LOG.debug("URI created for harvesting purposes: " + uri);

        return new RouteDefinition().
                from(uri).
                to("log:org.librairy.harvester.file.routes.file.FileRouteMaker?level=debug").
                setProperty(Record.DOMAIN_URI,                 new ConstantExpression(domain.getUri())).
                setProperty(Record.SOURCE_URL,                 new ConstantExpression(source.getUrl())).
                setProperty(Record.SOURCE_URI,                 new ConstantExpression(source.getUri())).
                setProperty(Record.SOURCE_PROTOCOL,            new ConstantExpression(source.getProtocol())).
                setProperty(Record.SOURCE_NAME,                new ConstantExpression(source.getName())).
//                setProperty(Record.PUBLICATION_PUBLISHED,      new SimpleExpression("${header.CamelFileLastModified}")).
//                setProperty(Record.PUBLICATION_METADATA_FORMAT,new ConstantExpression("pdf")). //TODO get from file extension
//                setProperty(Record.PUBLICATION_FORMAT,         new ConstantExpression("pdf")). //TODO get from file extension
                setProperty(Record.PUBLICATION_URL_LOCAL,      new SimpleExpression("${header.CamelFileAbsolutePath}")).
                setProperty(Record.PUBLICATION_REFERENCE_URL,  new SimpleExpression("${header.CamelFileAbsolutePath}")).
                setProperty(Record.PUBLICATION_URI,            new SimpleExpression("${header.CamelFileAbsolutePath}")).
//                to("stream:out");
                to(CommonRouteBuilder.URI_RO_BUILD);
    }
}
