package org.librairy.harvester.file.descriptor.txt;

import org.apache.commons.lang.StringUtils;
import org.librairy.harvester.file.descriptor.Descriptor;
import org.librairy.harvester.file.descriptor.FileDescription;
import org.librairy.model.domain.resources.MetaInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
public class TxtDescriptor implements Descriptor{

    private static final Logger LOG = LoggerFactory.getLogger(TxtDescriptor.class);

    @Override
    public FileDescription describe(File file) {

        FileDescription fileDescription = new FileDescription();

        try {
            fileDescription.setAggregatedFiles(Collections.EMPTY_LIST);

            fileDescription.setSummary(readSummary(file));

            fileDescription.setMetaInformation(readMetaInformation(file));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileDescription;
    }


    private String readSummary(File file) throws IOException {

        StringBuilder summary = new StringBuilder();
        try(Stream<String> stream = Files.lines(file.toPath())) {
            stream.limit(10).forEach(line -> summary.append(line));

        } catch (IOException e) {
            throw new AssertionError(e);
        }

        return summary.toString();

    }

    private MetaInformation readMetaInformation(File file){
        MetaInformation metaInformation = new MetaInformation();

        metaInformation.setFormat("txt");
        String name = StringUtils.substringBefore(file.getName(),".txt");
        metaInformation.setTitle(name);

        return metaInformation;
    }


}
