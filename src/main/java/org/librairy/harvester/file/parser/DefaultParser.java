package org.librairy.harvester.file.parser;

import com.google.common.io.Files;
import org.apache.commons.collections.map.HashedMap;
import org.librairy.harvester.file.descriptor.FileDescription;
import org.librairy.harvester.file.parser.pdf.PDFParser;
import org.librairy.harvester.file.parser.txt.TxtParser;
import org.librairy.harvester.file.utils.Serializations;
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

        // Check if serialized
        File serializedFile = new File(file.getAbsolutePath()+".1.ser");
        if (serializedFile.exists()){
            return Serializations.deserialize(ParsedDocument.class, serializedFile.getAbsolutePath());
        }

        String fileExtension = Files.getFileExtension(file.getAbsolutePath()).toLowerCase();
        Parser parser = parserMap.get(fileExtension);

        if (parser == null){
            throw new RuntimeException("Parser not found for file extension: " +  fileExtension + " [" + file
                    .getAbsolutePath() + "]");
        }

        ParsedDocument parsedDocument = parser.parse(file);

        // Serialize
        Serializations.serialize(parsedDocument, serializedFile.getAbsolutePath());

        return parsedDocument;

    }

}
