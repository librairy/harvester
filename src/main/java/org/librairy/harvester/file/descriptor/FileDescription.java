/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.descriptor;

import lombok.Data;
import org.librairy.model.domain.resources.MetaInformation;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
@Data
public class FileDescription implements Serializable {

    private MetaInformation metaInformation = new MetaInformation();

    private List<File> aggregatedFiles = new ArrayList<>();

    private String summary;
}
