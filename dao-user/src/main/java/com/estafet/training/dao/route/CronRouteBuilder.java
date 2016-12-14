package com.estafet.training.dao.route;

import com.estafet.training.dao.model.DaoObjectWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.camel.model.dataformat.JsonLibrary.Jackson;

/**
 * Created by Delcho Delov on 14/12/16.
 */
public class CronRouteBuilder extends RouteBuilder {
    private final Logger ddLog = LoggerFactory.getLogger(DaoFacadeRoute.class);

    @Override
    public void configure() throws Exception {
        configureGlobalErrorHandling();
        from("quartz2://chkUpdtdAcc?cron=0+30+0/1+*+*+?")
                .routeId("checkUpdatedAccountsSinceLastHour")
                .log(LoggingLevel.DEBUG, ddLog, "Route ${routeId} started")
                .processRef("check4UpdatedAccProcessor")
                .marshal().json(Jackson, DaoObjectWrapper.class)
                .to("jetty:http://{{dao.route.from.host}}:{{dao.route.from.port}}{{dao.route.from.facade}}?httpMethodRestrict=POST")
                .log(LoggingLevel.DEBUG, ddLog, "Route ${routeId} finished")
                .end();

    }
    private void configureGlobalErrorHandling() {
        onException(Throwable.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(500)
                .log(LoggingLevel.ERROR, ddLog, "Exception message: ${exception.message}\n${exception.stacktrace}");
    }
}
