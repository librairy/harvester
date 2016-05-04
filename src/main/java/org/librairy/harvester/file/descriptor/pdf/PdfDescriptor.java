package org.librairy.harvester.file.descriptor.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandler;
import org.apache.pdfbox.util.PDFTextStripper;
import org.librairy.harvester.file.descriptor.Descriptor;
import org.librairy.harvester.file.descriptor.FileDescription;
import org.librairy.harvester.file.helper.LanguageHelper;
import org.librairy.harvester.file.tokenizer.Language;
import org.librairy.model.domain.resources.MetaInformation;
import org.springframework.stereotype.Component;

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
            metainformation.setLanguage(LanguageHelper.getLanguageFrom(file).name());
            fileDescription.setMetaInformation(metainformation);

            // Summary
            fileDescription.setSummary(retrieveSummary(pdDocument));

            // File Name


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
        //metaInformation.setContributors("unknown");
        SecurityHandler securityHandler = document.getSecurityHandler();
        //metaInformation.setRights("unknown");
        if (securityHandler != null && securityHandler.getCurrentAccessPermission() != null){
            metaInformation.setRights(securityHandler.getCurrentAccessPermission().toString());
        }
        return metaInformation;
    }

    private String retrieveSummary(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(0);
        stripper.setEndPage(1);
        return stripper.getText(document);
    }
}
