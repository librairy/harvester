/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.annotator;

import org.librairy.storage.UDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by cbadenes on 04/01/16.
 */
public class DefaultAnnotator implements Annotator{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAnnotator.class);

    @Autowired
    UDM udm;

    public AnnotatedDocument annotate(String itemURI){
        LOG.debug("Annotation is not enabled by default");
        return new AnnotatedDocument();
//
//
//        try {
//
//
//
//            LOG.debug("Annotating document: " + file);
//            Long start = System.currentTimeMillis();
//
//            Period period = new Interval(start, System.currentTimeMillis()).toPeriod();
//            LOG.debug("Time annotating document: " + period.getMinutes() + " minutes, " + period.getSeconds() + " " +
//                    "seconds");
//            return document;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

}
