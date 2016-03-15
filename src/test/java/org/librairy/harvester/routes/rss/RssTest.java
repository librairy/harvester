package org.librairy.harvester.routes.rss;

import es.cbadenes.lab.test.IntegrationTest;
import org.librairy.harvester.Config;
import org.librairy.model.Event;
import org.librairy.model.domain.resources.Resource;
import org.librairy.model.domain.resources.Source;
import org.librairy.model.modules.EventBus;
import org.librairy.model.modules.RoutingKey;
import org.librairy.storage.UDM;
import org.librairy.model.utils.ResourceUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
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
@TestPropertySource(properties = { "librairy.eventbus.uri = localhost", "librairy.hoarder.storage.path = hoarder/target/storage", "librairy.upf.miner.config = harvester/src/test/resources/DRIconfig.properties" })
public class RssTest {

    private static final Logger LOG = LoggerFactory.getLogger(RssTest.class);

    @Autowired
    EventBus eventBus;

    @Autowired
    UDM udm;

    @Test
    public void readFolder() throws Exception {

        Source source = new Source();
        source.setUri("/sources/rss");
        source.setUrl("rss://rss.slashdot.org/Slashdot/slashdot");

        if (!udm.exists(Resource.Type.SOURCE).withUri(source.getUri())){
            udm.save(ResourceUtils.map(source, org.librairy.model.domain.resources.Source.class));
        }


        LOG.info("trying to send a 'source.created' event: " + source);
        this.eventBus.post(Event.from(source), RoutingKey.of(Resource.Type.SOURCE, Resource.State.CREATED));
        LOG.info("event sent. Now going to sleep...");
        Thread.sleep(120000);

    }
}
