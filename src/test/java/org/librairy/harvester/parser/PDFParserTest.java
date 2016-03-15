package org.librairy.harvester.parser;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by cbadenes on 14/03/16.
 */
public class PDFParserTest {

    @Test
    public void simplePdf (){

        File file = new File("src/test/resources/inbox/siggraph/a99-kopf.pdf");

        PDFParser parser = new PDFParser(file);

        String description = parser.getDescription();

        MetaInformation metaInformation = parser.getMetaInformation();

        Assert.assertEquals("Depixelizing pixel art", metaInformation.getTitle());

    }

}
