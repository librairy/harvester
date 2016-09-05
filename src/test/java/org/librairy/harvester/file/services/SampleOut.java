package org.librairy.harvester.file.services;


import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

/**
 * Created on 08/04/16:
 *
 * @author cbadenes
 */
public class SampleOut {


    public static void main(String[] args) throws IOException {

        File file = new File("/Users/cbadenes/Projects/librairy/harvester-file/pom.xml");

        System.out.println("Name: " + file.getName());
        System.out.println("Path: " + file.getPath());
        System.out.println("CanonicalPath: " + file.getCanonicalPath());
        //System.out.println("FileName without extension"  + Files.getFileNameWithoutExtension(file.getAbsolutePath()));

        System.out.println("FileName without extension"  + Files.getNameWithoutExtension(file.getAbsolutePath()));


    }



}
