package org.librairy.harvester.file.helper;

import lombok.Getter;
import lombok.Setter;
import org.librairy.harvester.file.annotator.TextAnnotator;
import org.librairy.model.modules.EventBus;
import org.librairy.storage.UDM;
import org.librairy.storage.generator.URIGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by cbadenes on 24/02/16.
 */
@Component
public class HarvesterHelper {

    @Getter @Setter
    @Value("${harvester.input.folder.serial}")
    String serializationDirectory;

    @Getter
    @Autowired
    UDM udm;

    @Getter
    @Autowired
    EventBus eventBus;

    @Getter
    @Autowired
    TextAnnotator textMiner;

    @Getter
    @Autowired
    URIGenerator uriGenerator;

}
