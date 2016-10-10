/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.routes;

import org.apache.camel.model.RouteDefinition;
import org.librairy.model.domain.resources.Source;
import org.librairy.model.domain.resources.Domain;

/**
 * Created by cbadenes on 01/12/15.
 */
public interface RouteMaker {

    boolean accept(String protocol);

    RouteDefinition build(Source source,Domain domain);
}
