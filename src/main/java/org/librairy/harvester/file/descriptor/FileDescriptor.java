package org.librairy.harvester.file.descriptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import org.apache.camel.Exchange;
import org.apache.commons.beanutils.BeanUtils;
import org.librairy.model.Record;
import org.librairy.model.domain.resources.MetaInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
@Component
public class FileDescriptor {

    private static final Logger LOG = LoggerFactory.getLogger(FileDescriptor.class);

    @Autowired
    List<Descriptor> descriptors;

    Map<String,Descriptor> descriptorMap;

    @Value("${harvester.input.folder.meta}")
    String metaFolder;

    @PostConstruct
    public void setup(){
        descriptorMap = new HashMap();
        for(Descriptor descriptor: descriptors){
            String ext = descriptor.getFileExtension().toLowerCase();
            descriptorMap.put(ext,descriptor);
        }
    }

    public FileDescription describe(File file){
        LOG.debug("Trying to parse: " + file.getAbsolutePath());

        String fileExtension = Files.getFileExtension(file.getAbsolutePath()).toLowerCase();

        Descriptor descriptor = descriptorMap.get(fileExtension);
        if (descriptor == null){
            throw new RuntimeException("No file descriptor found for: " + fileExtension + " ["+file.getAbsolutePath()
                    +"]");
        }


        FileDescription fileDescription = descriptor.describe(file);

        // Search a meta-file
        String metaFileName = StringUtils.replace(file.getName(), "." + fileExtension, ".json");
        File jsonFile = Paths.get(metaFolder, metaFileName).toFile();

        if (jsonFile.exists()){
            LOG.info("Used a referenced meta-information from: " + jsonFile.getAbsolutePath());
            ObjectMapper mapper = new ObjectMapper();

            try {
                MetaInformation metaInformationRef = mapper.readValue(jsonFile, MetaInformation.class);

                MetaInformation metaInformationInf = fileDescription.getMetaInformation();

                BeanUtils.copyProperties(metaInformationInf,metaInformationRef);

                fileDescription.setMetaInformation(metaInformationInf);
            } catch (IOException | InvocationTargetException | IllegalAccessException e) {
                LOG.warn("Error mapping to json the meta-file: " + jsonFile.getAbsolutePath(),e);
            }
        }

        return fileDescription;
    }

}
