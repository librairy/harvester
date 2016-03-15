package org.librairy.harvester.parser;

import com.google.common.io.Files;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by cbadenes on 14/03/16.
 */
@Component
public class ParserFactory {


    public IParser parserOf(File file){
        String extension = Files.getFileExtension(file.getAbsolutePath()).toLowerCase();
        switch (extension){
            case "pdf":
                return new PDFParser(file);
            default: throw new RuntimeException("Not parser for file type: " + file.getAbsolutePath());
        }
    }

}
