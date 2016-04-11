package org.librairy.harvester.file.annotator;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
@Data
public class AnnotatedDocument implements Serializable{


    private Map<String,String> sections;


    private Map<String,String> rhetoricalClasses;

}
