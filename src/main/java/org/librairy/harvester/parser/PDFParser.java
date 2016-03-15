package org.librairy.harvester.parser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandler;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.util.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by cbadenes on 14/03/16.
 */
public class PDFParser implements IParser {

    private static final Logger LOG = LoggerFactory.getLogger(PDFParser.class);

    private final PDDocument pdDocument;

    private final SimpleDateFormat timeFormatter;

    protected PDFParser(File file){
        try {
            this.timeFormatter  = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ssZ");
            this.pdDocument = PDDocument.load(file);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing pdf file: " + file,e);
        }

    }


    public MetaInformation getMetaInformation(){
        try {
            PDDocumentInformation docInformation = pdDocument.getDocumentInformation();

            MetaInformation metaInformation = new MetaInformation();

            metaInformation.setTitle(docInformation.getTitle());
            metaInformation.setCreators(docInformation.getAuthor());
            metaInformation.setDescription(docInformation.getKeywords());
            metaInformation.setFormat("pdf");
            metaInformation.setPubFormat("pdf");
            metaInformation.setType("file");
            metaInformation.setSubject(docInformation.getSubject());

            metaInformation.setPublished(timeFormatter.format(docInformation.getModificationDate().getTime()));
            metaInformation.setAuthored(timeFormatter.format(docInformation.getCreationDate().getTime()));

            metaInformation.setLanguage("en");
            //metaInformation.setPubURI("unknown");
            //metaInformation.setContributors("unknown");
            SecurityHandler securityHandler = pdDocument.getSecurityHandler();
            //metaInformation.setRights("unknown");
            if (securityHandler != null){
                metaInformation.setRights(securityHandler.getCurrentAccessPermission().toString());
            }

            return metaInformation;
        } catch (Exception e) {
            throw new RuntimeException("Error getting metadata from pdf file: " + this.pdDocument,e);
        }

    }

    @Override
    public String getContent() {
        try {
            return new PDFTextStripper().getText(pdDocument);
        } catch (IOException e) {
            throw new RuntimeException("Error getting content from pdf file: " + this.pdDocument,e);
        }
    }


    @Override
    public String getDescription() {
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(0);
            stripper.setEndPage(1);
            return stripper.getText(pdDocument);
        } catch (IOException e) {
            throw new RuntimeException("Error getting content from pdf file: " + this.pdDocument,e);
        }
    }
}
