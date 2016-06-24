package org.librairy.harvester.file.parser;

import java.io.File;

/**
 * Created by cbadenes on 14/03/16.
 */
public interface Parser {

    ParsedDocument parse(File file);
}
