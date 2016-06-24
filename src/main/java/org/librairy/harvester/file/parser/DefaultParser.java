package org.librairy.harvester.file.parser;

import com.google.common.io.Files;
import org.apache.commons.collections.map.HashedMap;
import org.librairy.harvester.file.parser.pdf.PDFParser;
import org.librairy.harvester.file.parser.txt.TxtParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Map;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
public class DefaultParser implements Parser{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultParser.class);

    Map<String,Parser> parserMap;

    @PostConstruct
    public void setup(){
        parserMap = new HashedMap();
        parserMap.put("pdf",new PDFParser());
        parserMap.put("txt", new TxtParser());
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
