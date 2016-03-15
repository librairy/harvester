package org.librairy.harvester.services;

import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.librairy.harvester.Config;
import org.librairy.model.domain.resources.Domain;
import org.librairy.model.domain.resources.File;
import org.librairy.model.domain.resources.Resource;
import org.librairy.model.domain.resources.Source;
import org.librairy.storage.UDM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by cbadenes on 15/03/16.
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
@TestPropertySource(properties = {
        "librairy.harvester.folder.input = src/test/resources/workspace/uploaded",
        "librairy.hoarder.storage.path = src/test/resources/workspace/collected",
        "parser.serializer.directory = src/test/resources/workspace/serialized",
        "librairy.cassandra.contactpoints = wiig.dia.fi.upm.es",
        "librairy.cassandra.port = 5011",
        "librairy.cassandra.keyspace = research",
        "librairy.elasticsearch.contactpoints = wiig.dia.fi.upm.es",
        "librairy.elasticsearch.port = 5021",
        "librairy.neo4j.contactpoints = wiig.dia.fi.upm.es",
        "librairy.neo4j.port = 5030",
        "librairy.eventbus.host = wiig.dia.fi.upm.es"
})
public class FileServiceTest {

    @Autowired
    FileService fileService;

    @Autowired
    UDM udm;

    @Test
    public void addFile() throws InterruptedException {


        Source source = Resource.newSource();
        source.setUri("http://org.librairy/sources/test");
        source.setName("sample");
        udm.save(source);

        Thread.sleep(3000000);

    }



}
