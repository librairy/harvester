package org.librairy.harvester.file.descriptor;

import java.io.File;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
public interface Descriptor {

    FileDescription describe(File file);
}
