package org.librairy.harvester.file.tokenizer;

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
@ConditionalOnClass(Tokenizer.class)
public class TokenizerConfig {

    @Bean
    @ConditionalOnMissingBean
    public Tokenizer defaultTokenizer(){
        return new DefaultTokenizer();
    }
}
