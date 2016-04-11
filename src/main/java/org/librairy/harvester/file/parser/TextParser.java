package org.librairy.harvester.file.parser;

import com.google.common.io.Files;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
@Component
public class TextParser {

    private static final Logger LOG = LoggerFactory.getLogger(TextParser.class);

    @Autowired
    List<Parser> parsers;

    Map<String,Parser> parserMap;

    @PostConstruct
    public void setup(){
        parserMap = new HashedMap();
        for (Parser parser : parsers){
            String fileExtension = parser.getFileExtension().toLowerCase();
            parserMap.put(fileExtension,parser);
        }
    }


    public ParsedDocument parse(File file){
        LOG.debug("Trying to parse: " + file.getAbsolutePath());

        String fileExtension = Files.getFileExtension(file.getAbsolutePath()).toLowerCase();
        Parser parser = parserMap.get(fileExtension);

        if (parser == null){
            throw new RuntimeException("Parser not found for file extension: " +  fileExtension + " [" + file
                    .getAbsolutePath() + "]");
        }

        return parser.parse(file);

    }

}
