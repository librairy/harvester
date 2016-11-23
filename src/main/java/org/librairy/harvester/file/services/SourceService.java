/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.services;

import com.google.common.base.Strings;
import lombok.Setter;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.lang.StringUtils;
import org.librairy.boot.storage.system.column.repository.UnifiedColumnRepository;
import org.librairy.harvester.file.executor.ParallelExecutor;
import org.librairy.harvester.file.routes.RouteDefinitionFactory;
import org.librairy.boot.model.domain.relations.Relation;
import org.librairy.boot.model.domain.resources.Domain;
import org.librairy.boot.model.domain.resources.Resource;
import org.librairy.boot.model.domain.resources.Source;
import org.librairy.boot.model.utils.ResourceUtils;
import org.librairy.boot.storage.UDM;
import org.librairy.boot.storage.generator.URIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
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

    @Autowired @Setter
    UnifiedColumnRepository columnRepository;

    @Autowired
    UDM udm;

    @Autowired
    URIGenerator uriGenerator;

    private ParallelExecutor executor;

    // TODO
    @PostConstruct
    public void setup(){
        this.executor = new ParallelExecutor();
        LOG.info("Restoring sources from ddbb..");

        udm.find(Resource.Type.SOURCE)
                .all()
                .stream()
                .map(res -> udm.read(Resource.Type.SOURCE).byUri(res.getUri()))
                .filter(res -> res.isPresent())
                .map(res -> res.get().asSource())
                .forEach(source -> {
                    try {

                        Iterable<Relation> rels = columnRepository.findBy(Relation.Type.COMPOSES, "start", source.getUri());

                        Iterator<Relation> iter = rels.iterator();

                        if (!iter.hasNext()){
                            Domain domain = createDefaultDomain(source);
                            addRoute(source, domain);
                            return;
                        }

                        // Adding routes by domain
                        rels.forEach(rel -> {
                            Optional<Resource> resource = udm.read(Resource.Type.DOMAIN).byUri(rel.getEndUri());
                            if (resource.isPresent()) try {
                                addRoute(source, resource.get().asDomain());
                            } catch (Exception e) {
                                LOG.error("Error restoring domains", e);
                            }
                        });
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
                LOG.debug("Source not found by uri: " + resource.getUri());
                return;
            }

            Source source = res.get().asSource();


            Iterable<Relation> rels = columnRepository.findBy(Relation.Type.COMPOSES, "start", source.getUri());


            Domain domain = !rels.iterator().hasNext()? createDefaultDomain(source)
                    : udm.read(Resource.Type.DOMAIN).byUri(rels.iterator().next().getEndUri()).get().asDomain() ;

            addRoute(source,domain);
        }catch (Exception e){
            LOG.error("Error adding source: " + resource.getUri(), e);
        }
    }

    private void addRoute(Source source, Domain domain) throws Exception {
        // Create a new route for harvesting this source
        if (Strings.isNullOrEmpty(source.getUrl())){
            LOG.warn("Source has not valid URL: " + source);
            return;
        }
        RouteDefinition route = routeDefinitionFactory.newRoute(source,domain);
        LOG.info("adding route to harvest: " + route);
        camelContext.addRouteDefinition(route);
    }


    private Domain createDefaultDomain(Source source){
        LOG.debug("creating a new domain associated to source: " + source.getUri());
        Domain domain = Resource.newDomain(source.getName());

        String domainUri    = uriGenerator.basedOnContent(Resource.Type.DOMAIN,source.getName());
        String sourceId     = URIGenerator.retrieveId(source.getUri());
        if (sourceId.equalsIgnoreCase("default")){
            domainUri = uriGenerator.from(Resource.Type.DOMAIN,"default");
        }
        domain.setUri(domainUri);
        domain.setDescription("attached to source: " + source.getUri());
        udm.save(domain);
        LOG.info("A new Domain has been created: " + domain.getUri() + " attached to Source: " + source.getUri());
        udm.save(Relation.newComposes(source.getUri(),domain.getUri()));
        return domain;
    }
}
