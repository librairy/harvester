/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.descriptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.librairy.boot.model.domain.resources.MetaInformation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
public class MineturMetaInfGenerator {


        public static void main(String[] args){

            String baseDir = "/Users/cbadenes/Documents/OEG/Projects/MINETUR/TopicModelling-2016/patentes-TIC-norteamericanas/";

            File outDir = Paths.get(baseDir, "metaFiles").toFile();

            if (!outDir.exists()){
                outDir.mkdirs();
            }


            File metaFiles = Paths.get(baseDir,"cites_uspto.csv").toFile();

            ObjectMapper jsonMapper = new ObjectMapper();
            jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);

            try {
                Files.lines(metaFiles.toPath()).forEach(line -> {


                    StringTokenizer tokenizer = new StringTokenizer(line,"\t");

                    String id       = tokenizer.nextToken();
                    String x        = tokenizer.nextToken();
                    String title    = tokenizer.nextToken();
                    String date     = tokenizer.nextToken();
                    String type     = tokenizer.nextToken();
//                    String cites    = tokenizer.nextToken();

                    MetaInformation metaInformation = new MetaInformation();
                    metaInformation.setTitle(title);
                    metaInformation.setPublished(date);
                    metaInformation.setType(type);



                    File jsonFile   = Paths.get(outDir.getAbsolutePath(),id+".json").toFile();
                    try {
                        jsonFile.createNewFile();
                        jsonMapper.writeValue(jsonFile,metaInformation);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


}
