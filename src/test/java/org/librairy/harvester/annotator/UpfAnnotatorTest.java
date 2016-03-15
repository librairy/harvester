package org.librairy.harvester.annotator;

import edu.upf.taln.dri.lib.exception.DRIexception;
import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.librairy.harvester.Config;
import org.librairy.harvester.annotator.upf.UpfAnnotator;
import org.librairy.harvester.executor.ParallelExecutor;
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
        "librairy.harvester.folder.input = target/sample-1111",
        "librairy.hoarder.storage.path = target/sample-1111",
        "parser.serializer.directory = target/sample-1111",
        "librairy.cassandra.contactpoints = wiig.dia.fi.upm.es",
        "librairy.cassandra.port = 5011",
        "librairy.cassandra.keyspace = research",
        "librairy.elasticsearch.contactpoints = wiig.dia.fi.upm.es",
        "librairy.elasticsearch.port = 5021",
        "librairy.neo4j.contactpoints = wiig.dia.fi.upm.es",
        "librairy.neo4j.port = 5030",
        "librairy.eventbus.host = wiig.dia.fi.upm.es"
})
public class UpfAnnotatorTest {

    @Autowired
    UpfAnnotator upfAnnotator;

    @Autowired
    ParallelExecutor executor;

    @Test
    public void parseFile() throws InterruptedException {

        executor.execute(() -> {
            try {
                upfAnnotator.annotateFile("src/test/resources/workspace/uploaded/sample/siggraph/p473-kovar.pdf");
            } catch (DRIexception drIexception) {
                drIexception.printStackTrace();
            }
        });


        Thread.sleep(600000);



    }

}
