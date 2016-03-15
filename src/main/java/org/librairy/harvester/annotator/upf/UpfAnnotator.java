package org.librairy.harvester.annotator.upf;

import edu.upf.taln.dri.lib.Factory;
import edu.upf.taln.dri.lib.exception.DRIexception;
import edu.upf.taln.dri.lib.loader.PDFloaderImpl;
import edu.upf.taln.dri.lib.model.Document;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Created by cbadenes on 07/01/16.
 */
@Component
public class UpfAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(UpfAnnotator.class);

    @Setter
    @Value("${librairy.upf.miner.config}")
    File driConfigPath;

    @Setter
    @Value("${librairy.upf.miner.proxy}")
    Boolean proxyEnabled;

    @PostConstruct
    public void setup() throws DRIexception {

        LOG.info("Initializing UPF Text Mining Framework from: " + driConfigPath + " ..");

        // Enable the PDFX proxy service
        //PDFloaderImpl.PDFXproxyEnabled = true;
        PDFloaderImpl.PDFXproxyEnabled = proxyEnabled;

        // Set property file path
        Factory.setDRIPropertyFilePath(driConfigPath.getAbsolutePath());

        // Enable bibliography entry parsing
        Factory.setEnableBibEntryParsing(false);

        // Initialize
        Factory.initFramework();

        LOG.info("UPF Text Mining Framework initialized successfully");
    }

    public AnnotatedDocument annotateFile(String documentPath) throws DRIexception {
        String extension = FilenameUtils.getExtension(documentPath.toString()).toLowerCase();
        Document document;
        switch(extension){
            case "pdf":
                LOG.info("parsing document as PDF document: " + documentPath);
                document = Factory.getPDFloader().parsePDF(documentPath);
                break;
            case "xml":
            case "htm":
            case "html":
                LOG.info("parsing document as structured document: " + documentPath);
                document = Factory.createNewDocument(documentPath);
                break;
            default:
                LOG.info("parsing document as plain text document: " + documentPath);
                document = Factory.getPlainTextLoader().parsePlainText(new File(documentPath));
                break;
        }
        if (document == null) throw new RuntimeException("Text Mining Library can not parse document: " + documentPath);
        return new AnnotatedDocument(document);
    }

    public AnnotatedDocument annotateText(String text) throws DRIexception {
        Document document = Factory.getPlainTextLoader().parseString(text,null);
        if (document == null) throw new RuntimeException("Text Mining Library can not parse text: " + StringUtils.substring(text,0,10));
        return new AnnotatedDocument(document);
    }

}
