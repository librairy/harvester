package org.librairy.harvester.file.routes;

import org.librairy.model.domain.resources.Resource;
import org.librairy.model.domain.resources.Source;
import org.librairy.storage.UDM;
import org.librairy.storage.generator.URIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
@Component
public class DefaultSource {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSource.class);

    @Autowired
    UDM udm;

    @Autowired
    URIGenerator uriGenerator;

    @Value("#{environment['LIBRAIRY_HOME']?:'${librairy.home}'}")
    String homeFolder;

    @Value("${librairy.harvester.folder}")
    String inputFolder;

    @Value("${librairy.harvester.folder.default}")
    String defaultFolder;

    @PostConstruct
    public void setup(){

        // Check if exists 'default' source
        if (udm.find(Resource.Type.SOURCE).by(Source.NAME, "default").isEmpty()){
            Source source = Resource.newSource("default");
            source.setUri(uriGenerator.from(Resource.Type.SOURCE, "default"));
            source.setDescription("default");
            Path folderPath = Paths.get(homeFolder, inputFolder, defaultFolder);
            source.setUrl("file:/"+folderPath.toFile().getAbsolutePath());
            LOG.info("Creating default source: " + source);
            udm.save(source);
        }


    }
}
