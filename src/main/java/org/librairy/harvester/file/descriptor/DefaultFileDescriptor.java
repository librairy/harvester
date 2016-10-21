/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.descriptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.librairy.harvester.file.descriptor.pdf.PdfDescriptor;
import org.librairy.harvester.file.descriptor.txt.TxtDescriptor;
import org.librairy.harvester.file.utils.Serializations;
import org.librairy.model.domain.resources.MetaInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
public class DefaultFileDescriptor implements Descriptor {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultFileDescriptor.class);

    Map<String,Descriptor> descriptorMap;

    @Value("#{environment['LIBRAIRY_HOME']?:'${librairy.home}'}")
    String homeFolder;

    @Value("${librairy.harvester.inbox}")
    String inputFolder;

    @Value("${librairy.harvester.inbox.meta}")
    String metaFolder;

    @PostConstruct
    public void setup(){
        descriptorMap = new HashMap();
        descriptorMap.put("pdf", new PdfDescriptor());
        descriptorMap.put("txt", new TxtDescriptor());

    }

    public FileDescription describe(File file){
        LOG.debug("Trying to parse: " + file.getAbsolutePath());

        String fileExtension = Files.getFileExtension(file.getAbsolutePath()).toLowerCase();

        Descriptor descriptor = descriptorMap.get(fileExtension);
        if (descriptor == null){
            throw new RuntimeException("No file descriptor found for: " + fileExtension + " ["+file.getAbsolutePath()
                    +"]");
        }


        // Check if serialized
        String fileName = StringUtils.substringAfter(file.getName(),"-");
        File serializedFile = Paths.get(file.getParent(),fileName+".0.ser").toFile();
        if (serializedFile.exists()){
            return Serializations.deserialize(FileDescription.class, serializedFile.getAbsolutePath());
        }


        FileDescription fileDescription = descriptor.describe(file);

        // Search a meta-file
        String metaFileName = StringUtils.replace(file.getName(), "." + fileExtension, ".json");
        File jsonFile = Paths.get(homeFolder,inputFolder,metaFolder, metaFileName).toFile();

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

        // Serialize file
        Serializations.serialize(fileDescription,serializedFile.getAbsolutePath());

        return fileDescription;
    }

}
