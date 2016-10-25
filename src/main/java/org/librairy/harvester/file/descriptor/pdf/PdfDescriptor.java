/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.descriptor.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.librairy.harvester.file.descriptor.Descriptor;
import org.librairy.harvester.file.descriptor.FileDescription;
import org.librairy.model.domain.resources.MetaInformation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
public class PdfDescriptor implements Descriptor{

    @Override
    public FileDescription describe(File file) {

        FileDescription fileDescription = new FileDescription();
        fileDescription.setAggregatedFiles(Collections.EMPTY_LIST);

        try {
            PDDocument pdDocument = PDDocument.load(file);

            // MetaInformation
            MetaInformation metainformation = retrieveMetaInformation(pdDocument);
            metainformation.setLanguage(pdDocument.getDocumentCatalog().getLanguage());
            fileDescription.setMetaInformation(metainformation);

            // Summary
            fileDescription.setSummary(retrieveSummary(pdDocument));

            // File Name

            pdDocument.close();

        } catch (IOException e) {
            throw new RuntimeException("UnexpectedError reading pdf file",e);
        }

        return fileDescription;
    }

    private MetaInformation retrieveMetaInformation(PDDocument document) throws IOException {
        MetaInformation metaInformation = new MetaInformation();

        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ssZ");
        PDDocumentInformation docInformation = document.getDocumentInformation();
        metaInformation.setTitle(docInformation.getTitle());
        metaInformation.setCreators(docInformation.getAuthor());
        metaInformation.setDescription(docInformation.getKeywords());
        metaInformation.setFormat("pdf");
        metaInformation.setPubFormat("pdf");
        metaInformation.setType("file");
        metaInformation.setSubject(docInformation.getSubject());

        if (docInformation.getModificationDate() != null){
            metaInformation.setPublished(timeFormatter.format(docInformation.getModificationDate().getTime()));
        }

        if (docInformation.getCreationDate() != null){
            metaInformation.setAuthored(timeFormatter.format(docInformation.getCreationDate().getTime()));
        }
        //metaInformation.setPubURI("unknown");
        metaInformation.setContributors(docInformation.getCreator());


        AccessPermission accessPermission = document.getCurrentAccessPermission();
        if (accessPermission != null)
            metaInformation.setRights(accessPermission.toString());

        return metaInformation;
    }

    private String retrieveSummary(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(0);
        stripper.setEndPage(1);
        return stripper.getText(document);
    }
}
