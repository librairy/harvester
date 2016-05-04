package org.librairy.harvester.file.routes.file;

import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.librairy.harvester.file.Config;
import org.librairy.model.domain.relations.Relation;
import org.librairy.model.domain.resources.Document;
import org.librairy.model.domain.resources.Domain;
import org.librairy.model.domain.resources.Resource;
import org.librairy.model.domain.resources.Source;
import org.librairy.model.utils.TimeUtils;
import org.librairy.storage.UDM;
import org.librairy.storage.generator.URIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

/**
 * Created by cbadenes on 14/01/16.
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
@TestPropertySource(properties = {
        "harvester.input.folder                 = src/test/resources/sample",
        "harvester.input.folder.default         = src/test/resources/sample/default",
        "harvester.input.folder.meta            = src/test/resources/sample/meta",
        "harvester.input.folder.external        = src/test/resources/sample/custom",
        "harvester.input.folder.hoarder         = src/test/resources/sample/collected",
        "librairy.cassandra.contactpoints       = wiig.dia.fi.upm.es",
        "librairy.cassandra.port                = 5011",
        "librairy.cassandra.keyspace            = research",
        "librairy.elasticsearch.contactpoints   = wiig.dia.fi.upm.es",
        "librairy.elasticsearch.port            = 5021",
        "librairy.neo4j.contactpoints           = wiig.dia.fi.upm.es",
        "librairy.neo4j.port                    = 5030",
        "librairy.eventbus.host                 = localhost",
        "librairy.eventbus.port                 = 5041"
})
public class DefaultFolderTest {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultFolderTest.class);

    @Value("${harvester.input.folder.external}")
    protected String inputFolder;

    @Value("${harvester.input.folder.default}")
    protected String defaultFolder;
    
    @Test
    public void run() throws InterruptedException {
        Assert.assertTrue(new File(inputFolder).exists());
        Assert.assertTrue(new File(defaultFolder).exists());
    }

}
