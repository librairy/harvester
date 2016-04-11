package org.librairy.harvester.file.annotator;

import org.joda.time.Interval;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by cbadenes on 04/01/16.
 */
@Component
public class TextAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(TextAnnotator.class);

//    @Autowired
    Annotator annotator;


    public AnnotatedDocument annotate(File file){
        try {
            LOG.info("Annotating document: " + file);
            Long start = System.currentTimeMillis();
            AnnotatedDocument document = annotator.annotate(file);
            Period period = new Interval(start, System.currentTimeMillis()).toPeriod();
            LOG.info("Time annotating document: " + period.getMinutes() + " minutes, " + period.getSeconds() + " seconds");
            return document;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AnnotatedDocument annotate(String text){
        try {
            LOG.info("Annotating text.. ");
            Long start = System.currentTimeMillis();
            AnnotatedDocument document = annotator.annotate(text);
            Period period = new Interval(start, System.currentTimeMillis()).toPeriod();
            LOG.info("Time annotating document: " + period.getMinutes() + " minutes, " + period.getSeconds() + " seconds");
            return document;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
