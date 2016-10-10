/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.routes.file;

import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.librairy.harvester.file.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.file.Paths;

/**
 * Created by cbadenes on 14/01/16.
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
@TestPropertySource(properties = {
        "librairy.home                 = src/test/resources"
})
public class DefaultFolderTest {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultFolderTest.class);

    @Value("#{environment['LIBRAIRY_HOME']?:'${librairy.home}'}")
    String homeFolder;

    @Value("${librairy.harvester.folder}")
    String inputFolder;

    @Value("${librairy.harvester.folder.external}")
    protected String externalFolder;

    @Value("${librairy.harvester.folder.default}")
    protected String defaultFolder;
    
    @Test
    public void run() throws InterruptedException {
        Thread.sleep(120000);
        Assert.assertTrue(Paths.get(homeFolder, inputFolder, externalFolder).toFile().exists());
        Assert.assertTrue(Paths.get(homeFolder, inputFolder, defaultFolder).toFile().exists());
    }

}
