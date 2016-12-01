package com.estafet.training.iban;

import com.estafet.training.api.AccountServiceApi;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by Delcho Delov on 01/12/16.
 */
public class EntryRouteBuilderTest extends CamelTestSupport {
    private final EntryRouteBuilder routeBuilder = new EntryRouteBuilder();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        final AccountServiceApi dummyAccServiceImpl = new EnrichProcessorTest.ConstAccService();
        final EnrichProcessor enrichProcessor = new EnrichProcessor();
        enrichProcessor.setAccountEnricherService(dummyAccServiceImpl);
        routeBuilder.setEnrichProcessor(enrichProcessor);
    }

    @Test
    public void testOne() throws Exception {
        assertNotNull(context);
        List<RouteDefinition> routeDefinitions = context.getRouteDefinitions();
        for (RouteDefinition routeDefinition : routeDefinitions) {
            String routeDefinitionId = routeDefinition.getId();
            System.out.println("routeDefinitionId = " + routeDefinitionId);
        }

//        // advice the first route using the inlined route builder
//        routeDefinitions.get(0).adviceWith(context, new RouteBuilder() {
//            @Override
//            public void configure() throws Exception {
//                // intercept sending to mock:foo and do something else
//                interceptSendToEndpoint("mock:foo")
//                        .skipSendToOriginalEndpoint()
//                        .to("log:foo")
//                        .to("mock:advised");
//            }
//        });
//
//        getMockEndpoint("mock:foo").expectedMessageCount(0);
//        getMockEndpoint("mock:advised").expectedMessageCount(1);
//        getMockEndpoint("mock:result").expectedMessageCount(1);
//
//        template.sendBody("direct:start", "Hello World");
//
//        assertMockEndpointsSatisfied();
    }

}