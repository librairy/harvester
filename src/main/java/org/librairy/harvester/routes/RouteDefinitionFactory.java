package org.librairy.harvester.routes;

import com.google.common.base.Strings;
import org.apache.camel.model.RouteDefinition;
import org.librairy.model.domain.resources.Domain;
import org.librairy.model.domain.resources.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by cbadenes on 01/12/15.
 */
@Component
public class RouteDefinitionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RouteDefinitionFactory.class);

    @Autowired
    List<RouteMaker> routeMakers;

    @PostConstruct
    public void init(){
        routeMakers.stream().forEach(routeMaker -> LOG.info("Route Maker registered: " + routeMaker));
    }

    public RouteDefinition newRoute(Source source,Domain domain){

        String protocol = Strings.isNullOrEmpty(source.getProtocol())? "file" : source.getProtocol().toLowerCase();

        List<RouteMaker> handlers = routeMakers.stream().filter(routeMaker -> routeMaker.accept(protocol)).collect(Collectors.toList());

        if (handlers == null || handlers.isEmpty()){
            throw new RuntimeException("Protocol: '" + protocol+"' not handled");
        }else if (handlers.size() > 1){
            LOG.warn("More than one builder for handling '" + protocol + "' sources: " + handlers);
        }

        return handlers.get(0).build(source,domain);
    }

}
