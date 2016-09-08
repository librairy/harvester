package org.librairy.harvester.file.parser.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.librairy.harvester.file.parser.ParsedDocument;
import org.librairy.harvester.file.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by cbadenes on 14/03/16.
 */
public class PDFParser implements Parser {

    private static final Logger LOG = LoggerFactory.getLogger(PDFParser.class);

    private SimpleDateFormat timeFormatter;


    @PostConstruct
    public void setup(){
        this.timeFormatter  = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ssZ");
    }


    @Override
    public ParsedDocument parse(File file) {

        try {
            this.timeFormatter  = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ssZ");
            PDDocument pdDocument = PDDocument.load(file);

            ParsedDocument document = new ParsedDocument();
            document.setText(new PDFTextStripper().getText(pdDocument));
            return document;
        } catch (IOException e) {
            throw new RuntimeException("Error getting content from pdf file: " + file.getAbsolutePath(),e);
        }

    }


}
