package org.librairy.harvester.file.services;

import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.librairy.harvester.file.Config;
import org.librairy.model.domain.resources.Resource;
import org.librairy.model.domain.resources.Source;
import org.librairy.storage.UDM;
import org.librairy.storage.generator.URIGenerator;
import org.librairy.storage.system.column.repository.UnifiedColumnRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        "harvester.input.folder                 = src/test/resources/workspace",
        "harvester.input.folder.default         = src/test/resources/workspace/default",
        "harvester.input.folder.meta            = src/test/resources/workspace/meta",
        "harvester.input.folder.external        = src/test/resources/workspace/custom",
        "harvester.input.folder.hoarder         = src/test/resources/workspace/collected",
        "librairy.cassandra.contactpoints       = zavijava.dia.fi.upm.es",
        "librairy.cassandra.port                = 5011",
        "librairy.cassandra.keyspace            = research",
        "librairy.elasticsearch.contactpoints   = zavijava.dia.fi.upm.es",
        "librairy.elasticsearch.port            = 5021",
        "librairy.neo4j.contactpoints           = zavijava.dia.fi.upm.es",
        "librairy.neo4j.port                    = 5030",
        "librairy.eventbus.host                 = zavijava.dia.fi.upm.es",
        "librairy.eventbus.port                 = 5041"
})
public class DocumentServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentServiceTest.class);

    @Autowired
    DocumentService documentService;

    @Autowired
    UDM udm;


    @Autowired
    UnifiedColumnRepository unifiedColumnRepository;

    @Autowired
    URIGenerator uriGenerator;



    @Test
    public void addFile() throws InterruptedException {


        Source source = Resource.newSource();
        source.setUri("http://org.librairy/sources/test");
        source.setName("sample");
        udm.save(source);

        Thread.sleep(3000000);

    }

    @Test
    public void clean(){
        udm.delete(Resource.Type.ANY).all();
    }

    @Test
    public void addingToDefaultFolder() throws InterruptedException {

        Long delay = 3000000l;
        LOG.info("waiting for " + delay);
        Thread.sleep(delay);


    }

    @Test
    public void countDocuments(){

        Iterable<Resource> documents = unifiedColumnRepository.findAll(Resource.Type.DOCUMENT);

        System.out.println("1 Found: " + documents.iterator().next());



    }

}
