package com.estafet.training.dao.route;

import com.estafet.training.model.Account;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.camel.model.dataformat.JsonLibrary.Jackson;


/**
 * Created by Delcho Delov on 08/12/16.
 */
@Deprecated // Use DaoFacadeRoute with operation='PERSIST' and Account payload instead
public class IbanPersistRoute extends RouteBuilder {
    private final Logger ddLog = LoggerFactory.getLogger(IbanPersistRoute.class);

    @Override
    public void configure() throws Exception {
        configureGlobalErrorHandling();
        from("jetty:http://{{dao.route.from.host}}:{{dao.route.from.port}}{{dao.route.from.persist}}?httpMethodRestrict=POST")
                .routeId("dao.entry")
                .streamCaching()
                .unmarshal().json(Jackson, Account.class)
                .log(LoggingLevel.DEBUG, ddLog, "Route started ${routeId}")
//                .setHeader(IBAN_TIMESTAMP_OF_REQUEST).constant("${date:now}")
//                .split(simple("${body.getIbans()}"))
                .to("direct:daopersist")
                .end()
                .log(LoggingLevel.DEBUG, ddLog, "Route ${routeId} finished")
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(200);

        from("direct:daopersist").processRef("persistAccountProcessor");
    }
    private void configureGlobalErrorHandling() {
        onException(Throwable.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(500)
                .log(LoggingLevel.ERROR, ddLog, "Exception message: ${exception.message}\n${exception.stacktrace}")
        ;
    }

}
