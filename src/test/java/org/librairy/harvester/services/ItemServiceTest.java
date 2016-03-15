package org.librairy.harvester.services;

import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.librairy.harvester.Config;
import org.librairy.harvester.annotator.upf.AnnotatedDocument;
import org.librairy.harvester.serializer.AnnotatedDocumentSerializer;
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
public class ItemServiceTest {


    @Test
    public void deserialize() throws InterruptedException {

        AnnotatedDocument annotatedDocument = null;
        try {
            annotatedDocument = AnnotatedDocumentSerializer.from("src/test/resources/workspace/serialized/");

        }catch (Exception e){
            e.printStackTrace();
        }
        Assert.assertNotNull(annotatedDocument);

    }



}
