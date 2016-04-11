package org.librairy.harvester.file.services;

import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.librairy.harvester.file.annotator.AnnotatedDocument;
import org.librairy.harvester.file.serializer.AnnotatedDocumentSerializer;

/**
 * Created by cbadenes on 15/03/16.
 */
@Category(IntegrationTest.class)
public class PartsServiceTest {


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
