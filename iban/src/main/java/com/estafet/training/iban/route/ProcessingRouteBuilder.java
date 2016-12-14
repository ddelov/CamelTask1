package com.estafet.training.iban.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Delcho Delov on 02/12/16.
 */
public class ProcessingRouteBuilder extends RouteBuilder {
    private final Logger ddLog = LoggerFactory.getLogger(ProcessingRouteBuilder.class);
    private static final String ROUTE_PROCESSING = "processing";

    @Override
    public void configure() throws Exception {
        configureGlobalErrorHandling();
        from("activemq:{{processing.route.from}}")
                .log(LoggingLevel.DEBUG, ddLog, "Route started ${routeId}")
                .routeId(ROUTE_PROCESSING)
                // filter out duplicate messages by IBAN
                .idempotentConsumer(simple("${body}") , MemoryIdempotentRepository.memoryIdempotentRepository(200))
                    .skipDuplicate(false)
                    .filter(/*exchangeProperty*/property(Exchange.DUPLICATE_MESSAGE).isEqualTo(true))
                    .log(LoggingLevel.DEBUG, ddLog, "\n\n===============duplicate found ${body}")
                    .stop()
                    .end()
                .log(LoggingLevel.DEBUG, ddLog, "body after multicast is ${body}")
                .multicast()
                .to("direct:enr", "direct:csv");
    }
    private void configureGlobalErrorHandling() {
        onException(Throwable.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(500)
                .log(LoggingLevel.ERROR, ddLog, "Exception message: ${exception.message}\n${exception.stacktrace}")
        ;
    }

}
