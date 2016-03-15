package org.librairy.harvester.annotator;

import edu.upf.taln.dri.lib.exception.DRIexception;
import org.apache.commons.lang3.StringUtils;
import org.librairy.harvester.annotator.stanford.StanfordParser;
import org.librairy.harvester.annotator.stanford.Token;
import org.librairy.harvester.annotator.upf.AnnotatedDocument;
import org.librairy.harvester.annotator.upf.UpfAnnotator;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbadenes on 04/01/16.
 * TODO this component should be a client of the NLP internal service
 */
@Component
public class TextAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(TextAnnotator.class);

    @Autowired
    UpfAnnotator annotator;

    @Autowired
    StanfordParser parser;


    public AnnotatedDocument annotateFile(String documentPath){
        try {
            LOG.info("Annotating document: " + documentPath);
            Long start = System.currentTimeMillis();
            AnnotatedDocument document = annotator.annotateFile(documentPath);
            Period period = new Interval(start, System.currentTimeMillis()).toPeriod();
            LOG.info("Time annotating document: " + period.getMinutes() + " minutes, " + period.getSeconds() + " seconds");
            return document;
        } catch (DRIexception drIexception) {
            throw new RuntimeException(drIexception);
        }
    }

    public AnnotatedDocument annotateText(String text){
        try {
            LOG.info("Annotating text.. ");
            Long start = System.currentTimeMillis();
            AnnotatedDocument document = annotator.annotateText(text);
            Period period = new Interval(start, System.currentTimeMillis()).toPeriod();
            LOG.info("Time annotating document: " + period.getMinutes() + " minutes, " + period.getSeconds() + " seconds");
            return document;
        } catch (DRIexception drIexception) {
            throw new RuntimeException(drIexception);
        }
    }

    public List<Token> tokenize(String text){
        try {
            return parser.tokenize(text);
        } catch (Exception e) {
            LOG.error("Error parsing text: " + StringUtils.substring(text,0,10) + " ...",e);
            return new ArrayList<>();
        }
    }

}
