package org.librairy.harvester.file.helper;

import lombok.Getter;
import lombok.Setter;
import org.librairy.harvester.file.annotator.Annotator;
import org.librairy.harvester.file.annotator.DefaultAnnotator;
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

    @Getter
    @Autowired
    UDM udm;

    @Getter
    @Autowired
    EventBus eventBus;

    @Getter
    @Autowired
    Annotator textMiner;

    @Getter
    @Autowired
    URIGenerator uriGenerator;

}
