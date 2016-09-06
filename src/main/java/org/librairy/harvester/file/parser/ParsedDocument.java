package org.librairy.harvester.file.parser;

import lombok.Data;

import java.io.Serializable;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
@Data
public class ParsedDocument implements Serializable{

    private String text;

    //private List<Image> images;
}
