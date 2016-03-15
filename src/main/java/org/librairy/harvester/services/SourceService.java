package org.librairy.harvester.services;

import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.librairy.harvester.executor.ParallelExecutor;
import org.librairy.harvester.routes.RouteDefinitionFactory;
import org.librairy.model.domain.relations.Relation;
import org.librairy.model.domain.resources.Domain;
import org.librairy.model.domain.resources.Resource;
import org.librairy.model.domain.resources.Source;
import org.librairy.model.utils.ResourceUtils;
import org.librairy.storage.UDM;
import org.librairy.storage.generator.URIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

/**
 * Created by cbadenes on 01/12/15.
 */
@Component
public class SourceService {

    private static final Logger LOG = LoggerFactory.getLogger(SourceService.class);

    @Autowired
    SpringCamelContext camelContext;

    @Autowired
    RouteDefinitionFactory routeDefinitionFactory;

    @Autowired
    UDM udm;

    @Autowired
    URIGenerator uriGenerator;

    @Autowired
    ParallelExecutor executor;

    public SourceService(){

    }

    // TODO
    @PostConstruct
    public void setup(){
        LOG.info("Restoring sources from ddbb..");
        udm.find(Resource.Type.SOURCE).all().stream().map(uri -> udm.read(Resource.Type.SOURCE).byUri(uri)).filter(res -> res.isPresent()).map(res -> res.get()).forEach(source -> {
            try {
                List<String> res = udm.find(Resource.Type.DOMAIN).from(Resource.Type.SOURCE,source.getUri());
                if (res != null && !res.isEmpty()){
                    Optional<Resource> domain = udm.read(Resource.Type.DOMAIN).byUri(res.get(0));
                    if (domain.isPresent()) addRoute((Source) source, (Domain) domain.get());
                }
            } catch (Exception e) {
                LOG.error("Error initializing source from ddbb:" + source, e);
            }
        });
        LOG.info("All sources were restored from ddbb.");
    }


    public void handleParallel(Resource resource){
        executor.execute(() -> handle(resource));
    }


    public void handle(Resource resource){

        try{
            Optional<Resource> res = udm.read(Resource.Type.SOURCE).byUri(resource.getUri());

            if (!res.isPresent()){
                LOG.warn("Source not found by uri: " + resource.getUri());
                return;
            }

            Source source = res.get().asSource();

            List<String> domains = udm.find(Resource.Type.DOMAIN).from(Resource.Type.SOURCE,resource.getUri());

            Domain domain;
            if (domains == null || domains.isEmpty()){
                LOG.info("creating a new domain associated to source: " + resource.getUri());
                domain = Resource.newDomain();
                domain.setUri(uriGenerator.newFor(Resource.Type.DOMAIN));
                domain.setName(source.getName());
                domain.setDescription("attached to source: " + source.getUri());
                udm.save(domain);
                LOG.info("Domain: " + domain + " attached to source: " + source);
                udm.save(Relation.newComposes(source.getUri(),domain.getUri()));

            }else{
                domain = ResourceUtils.map(udm.read(Resource.Type.DOMAIN).byUri(domains.get(0)).get(),Domain.class);
            }

            addRoute(source,domain);
        }catch (Exception e){
            LOG.error("Error adding source: " + resource.getUri(), e);
        }
    }

    private void addRoute(Source source, Domain domain) throws Exception {
        // Create a new route for harvesting this source
        RouteDefinition route = routeDefinitionFactory.newRoute(source,domain);
        LOG.info("adding route to harvest: " + route);
        camelContext.addRouteDefinition(route);
    }

}
