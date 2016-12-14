package com.estafet.training.dao.route;

import com.estafet.training.dao.model.DaoObjectWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.apache.camel.model.dataformat.JsonLibrary.Jackson;


/**
 * Created by Delcho Delov on 08/12/16.
 */
public class DaoFacadeRoute extends RouteBuilder {
    private final Logger ddLog = LoggerFactory.getLogger(DaoFacadeRoute.class);

    @Override
    public void configure() throws Exception {
        configureGlobalErrorHandling();
        from("jetty:http://{{dao.route.from.host}}:{{dao.route.from.port}}{{dao.route.from.facade}}?httpMethodRestrict=POST")
                .routeId("dao.facade.entry")
                .streamCaching()
                .unmarshal().json(Jackson, DaoObjectWrapper.class)
                .log(LoggingLevel.DEBUG, ddLog, "Route started ${routeId}")
//                .setHeader(IBAN_TIMESTAMP_OF_REQUEST).constant("${date:now}")
//                .split(simple("${body.getIbans()}"))
                .to("direct:to.facade")
                .to("direct:checkOutput")
                .end()
                .log(LoggingLevel.DEBUG, ddLog, "Route ${routeId} finished")
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(200);

        from("direct:to.facade").processRef("daoFacadeProcessor");
        from("direct:checkOutput")
                .marshal()
                .json(Jackson, Set.class)
                .to("file:{{reports.file.dir}}?autoCreate=true&fileExist=Append&charset=UTF-8");

    }
    private void configureGlobalErrorHandling() {
        onException(Throwable.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(500)
                .log(LoggingLevel.ERROR, ddLog, "Exception message: ${exception.message}\n${exception.stacktrace}")
        ;
    }

}
