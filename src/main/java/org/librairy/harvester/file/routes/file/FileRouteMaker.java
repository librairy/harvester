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

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by cbadenes on 01/12/15.
 */
@Component
public class FileRouteMaker implements RouteMaker{

    private static final Logger LOG = LoggerFactory.getLogger(FileRouteMaker.class);

    @Value("#{environment['LIBRAIRY_HOME']?:'${librairy.home}'}")
    String homeFolder;

    @Value("${librairy.harvester.folder}")
    String inputFolder;

    @Value("${librairy.harvester.folder.external}")
    protected String externalFolder;

    @Value("${librairy.harvester.folder.default}")
    protected String defaultFolder;

    @PostConstruct
    public void setup(){

        File inputF = Paths.get(homeFolder, inputFolder, externalFolder).toFile();
        if (!inputF.exists()) inputF.mkdirs();

        File defaultF = Paths.get(homeFolder, inputFolder, defaultFolder).toFile();
        if (!defaultF.exists()) defaultF.mkdirs();

    }

    @Override
    public boolean accept(String protocol) {
        return protocol.equalsIgnoreCase("file");
    }

    @Override
    public RouteDefinition build(Source source,Domain domain) {

        Path folder = Paths.get(homeFolder,inputFolder,externalFolder, StringUtils.substringAfter(source.getUrl(),
                "//"));
        if (source.getName().equalsIgnoreCase("default")){
            folder = Paths.get(homeFolder,inputFolder,defaultFolder);
        }


        String uri = new StringBuilder().
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
