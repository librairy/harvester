/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.routes.file;

import es.cbadenes.lab.test.IntegrationTest;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by cbadenes on 31/12/15.
 */
@Category(IntegrationTest.class)
public class FileTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Test
    public void oaipmhMessage() throws Exception {

        resultEndpoint.expectedMessageCount(3);

        Thread.sleep(60000);

//        template.sendBody(xml);
        resultEndpoint.assertIsSatisfied();
    }

//    @Override
//    protected RouteBuilder createRouteBuilder() {
//        return new RouteBuilder() {
//            public void configure() {
//
//                Path path = Paths.get("src/test/resources/inbox/siggraph-2015");
//
//
//                /**
//                 * Route[[
//                 * idempotentRepository=#fileStore]] -> [To[log:org.librairy.harvester.file.routes.file.FileRouteMaker?level=INFO], SetProperty[librairy.domain.uri, constant{}], SetProperty[librairy.source.url, constant{file://siggraph-2006}], SetProperty[librairy.source.uri, constant{http://librairy.org/sources/48afa130-28c1-4bb3-bbc2-dac5da760fa1}], SetProperty[librairy.source.protocol, constant{file}], SetProperty[librairy.source.name, constant{siggraph-2006}], SetProperty[librairy.publication.published, simple{${header.CamelFileLastModified}}], SetProperty[librairy.publication.reference.format, constant{pdf}], SetProperty[librairy.publication.format, constant{pdf}], SetProperty[librairy.publication.url.local, simple{${header.CamelFileAbsolutePath}}], SetProperty[librairy.publication.reference.url, simple{${header.CamelFileAbsolutePath}}], SetProperty[librairy.publication.uri, simple{${header.CamelFileAbsolutePath}}], To[direct:common.ro.build]]]
//
//                 */
//                from("file://"+path.toFile().getAbsolutePath()+"?"+
//                        "recursive=true&" +
//                        "noop=true&"+
//                        "chmod=777&" +
//                        "delete=false&" +
//                        "readLock=changed" +
//                        "readLockCheckInterval=2000" +
//                        "idempotent=true&" +
//                        "idempotentKey=${file:name}-${file:size}").
//                        to("log:org.librairy.harvester.file.routes.FileTest?level=INFO").
//                        to("mock:result");
//
//            }
//        };
//    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {

                Path path = Paths.get("/Users/cbadenes/Downloads/papers");




                /**
                 * Route[[
                 * idempotentRepository=#fileStore]] -> [To[log:org.librairy.harvester.file.routes.file.FileRouteMaker?level=INFO], SetProperty[librairy.domain.uri, constant{}], SetProperty[librairy.source.url, constant{file://siggraph-2006}], SetProperty[librairy.source.uri, constant{http://librairy.org/sources/48afa130-28c1-4bb3-bbc2-dac5da760fa1}], SetProperty[librairy.source.protocol, constant{file}], SetProperty[librairy.source.name, constant{siggraph-2006}], SetProperty[librairy.publication.published, simple{${header.CamelFileLastModified}}], SetProperty[librairy.publication.reference.format, constant{pdf}], SetProperty[librairy.publication.format, constant{pdf}], SetProperty[librairy.publication.url.local, simple{${header.CamelFileAbsolutePath}}], SetProperty[librairy.publication.reference.url, simple{${header.CamelFileAbsolutePath}}], SetProperty[librairy.publication.uri, simple{${header.CamelFileAbsolutePath}}], To[direct:common.ro.build]]]

                 */
                from("file2i://"+path.toFile().getAbsolutePath()+"?"+
                        "autoCreate=true&" +
                        "recursive=true&" +
                        "noop=false&"+
                        "chmod=777&" +
                        "delete=false&" +
                        "move=.done&" +
                        "moveFailed=.error&" +
//                        "processStrategy=#customProcessStrategy&" +
                        "readLock=changed&" +
                        "readLockCheckInterval=2000&" +
                        "idempotent=true&" +
                        "idempotentKey=${file:name}-${file:size}").
                        to("log:org.librairy.harvester.file.routes.FileTest?level=INFO").
                        to("mock:result");

            }
        };
    }



}
