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
        "librairy.home                 = src/test/resources/workspace"
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

        Source source = Resource.newSource("files");
        source.setUri("http://org.librairy/sources/test");
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
