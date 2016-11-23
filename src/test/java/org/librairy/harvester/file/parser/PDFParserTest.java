/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.parser;

import junit.framework.Assert;
import org.junit.Test;
import org.librairy.harvester.file.descriptor.FileDescription;
import org.librairy.harvester.file.descriptor.pdf.PdfDescriptor;
import org.librairy.harvester.file.parser.pdf.PDFParser;
import org.librairy.boot.model.domain.resources.MetaInformation;

import java.io.File;

/**
 * Created by cbadenes on 14/03/16.
 */
public class PDFParserTest {

    @Test
    public void simplePdf (){

        File file = new File("src/test/resources/inbox/siggraph/a99-kopf.pdf");

        PdfDescriptor pdfDescriptor = new PdfDescriptor();

        FileDescription description = pdfDescriptor.describe(file);

        MetaInformation metaInformation = description.getMetaInformation();

        Assert.assertEquals("Depixelizing pixel art", metaInformation.getTitle());

    }

}
