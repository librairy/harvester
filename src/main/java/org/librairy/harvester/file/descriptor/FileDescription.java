package org.librairy.harvester.file.descriptor;

import lombok.Data;
import org.librairy.model.domain.resources.MetaInformation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 07/04/16:
 *
 * @author cbadenes
 */
@Data
public class FileDescription {

    private MetaInformation metaInformation = new MetaInformation();

    private List<File> aggregatedFiles = new ArrayList<>();

    private String summary;
}
