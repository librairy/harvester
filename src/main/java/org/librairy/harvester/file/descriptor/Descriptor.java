/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

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
