package org.librairy.harvester.file.helper;

import org.apache.commons.lang.StringUtils;
import org.librairy.harvester.file.tokenizer.Language;

import java.io.File;

/**
 * Created on 04/05/16:
 *
 * @author cbadenes
 */
public class LanguageHelper {

    public static Language getLanguageFrom(File file){

        String fileName = StringUtils.substringBeforeLast(file.getName(), ".");
        return fileName.endsWith("_ES")? Language.ES : Language.EN;
    }
}
