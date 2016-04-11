package org.librairy.harvester.file.routes.oaipmh;

import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.librairy.harvester.file.Config;
import org.librairy.model.modules.EventBus;
import org.librairy.storage.UDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by cbadenes on 04/01/16.
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
@TestPropertySource(properties = { "librairy.eventbus.uri = localhost", "harvester.input.folder.hoarder = " +
        "../hoarder/target/storage", "harvester.input.folder.external = target/workspace", "librairy.upf.miner.config = " +
        "src/test/resources/DRIconfig.properties" })
public class OAIPMHTest {

    private static final Logger LOG = LoggerFactory.getLogger(OAIPMHTest.class);

    @Autowired
    EventBus eventBus;

    @Autowired
    UDM udm;

    @Test
    public void readFolder() throws Exception {

//        Source source = new Source();
//        source.setUri("http://librairy.org/sources/7aa484ca-d968-43b2-b336-2a5af501d1e1");
//        source.setName("oaipmh-bournemouth");
//        source.setUrl("oaipmh://eprints.bournemouth.ac.uk/cgi/oai2?from=2015-01-01T00:00:00Z");
//
//        udm.saveSource(ResourceUtils.map(source, org.librairy.storage.model.Source.class));
//
//        LOG.info("trying to send a 'source.created' event: " + source);
//        this.eventBus.post(Event.from(source), RoutingKey.of(Resource.Type.SOURCE, Resource.State.CREATED));
        LOG.info("event sent. Now going to sleep...");
        Thread.sleep(600000);

    }
}
