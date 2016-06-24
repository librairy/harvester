package org.librairy.harvester.file.annotator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 20/04/16:
 *
 * @author cbadenes
 */
@Configuration
@ConditionalOnClass(Annotator.class)
public class AnnotatorConfig {

    @Bean
    @ConditionalOnMissingBean
    public Annotator defaultAnnotator(){
        return new DefaultAnnotator();
    }
}
