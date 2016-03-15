package org.librairy.harvester.parser;

/**
 * Created by cbadenes on 14/03/16.
 */
public interface IParser {

    MetaInformation getMetaInformation();

    String getContent();

    String getDescription();
}
