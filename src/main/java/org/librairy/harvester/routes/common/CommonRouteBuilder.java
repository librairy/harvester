package org.librairy.harvester.routes.common;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.language.SimpleExpression;
import org.librairy.harvester.routes.processor.ErrorHandler;
import org.librairy.harvester.routes.processor.ResourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by cbadenes on 01/12/15.
 */
@Component
public class CommonRouteBuilder extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(CommonRouteBuilder.class);

    public static final String URI_RO_BUILD        = "direct:common.ro.build";

    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    ResourceBuilder resourceBuilder;


    @Value("${librairy.hoarder.storage.path}")
    protected String outdir;


    @Override
    public void configure() throws Exception {

        onException(MalformedURLException.class)
                .process(errorHandler).stop();

        onException(IOException.class)
                .maximumRedeliveries(3)
                .process(errorHandler).stop();

        from(URI_RO_BUILD).
                process(resourceBuilder).
                convertBodyTo(byte[].class).
                setHeader("rabbitmq.DELIVERY_MODE",new SimpleExpression("2")).
                setHeader("rabbitmq.EXCHANGE_NAME",new SimpleExpression("librairy.eventbus")).
                setHeader("rabbitmq.ROUTING_KEY",new SimpleExpression("file.created")).
                to("rabbitmq://localhost:5672/ex2?connectionFactory=#customConnectionFactory");

    }
}

