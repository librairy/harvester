package org.librairy.harvester.file.parser.txt;

import org.librairy.harvester.file.parser.ParsedDocument;
import org.librairy.harvester.file.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
public class TxtParser implements Parser {

    private static final Logger LOG = LoggerFactory.getLogger(TxtParser.class);

    @Override
    public ParsedDocument parse(File file) {

        ParsedDocument parsedDocument = new ParsedDocument();

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            parsedDocument.setText(content);
        } catch (IOException e) {
            throw new RuntimeException("Error getting content from txt file: " + file.getAbsolutePath(),e);
        }

        return parsedDocument;
    }
}
