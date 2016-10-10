/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.descriptor;

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
@ConditionalOnClass(Descriptor.class)
public class DescriptorConfig {

    @Bean
    @ConditionalOnMissingBean
    public Descriptor defaultDescriptor(){
        return new DefaultFileDescriptor();
    }
}
