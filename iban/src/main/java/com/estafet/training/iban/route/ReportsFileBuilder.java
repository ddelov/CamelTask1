package com.estafet.training.iban.route;

import com.estafet.training.iban.strategy.ArrayListAggregationStrategy;
import com.estafet.training.model.Account;
import com.estafet.training.model.AccountsWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Delcho Delov on 02/12/16.
 */
public class ReportsFileBuilder extends RouteBuilder {
    private final Logger ddLog = LoggerFactory.getLogger(ReportsFileBuilder.class);
    public static final String ROUTE_DIRECT_ENR = "directEnr";
    private final AggregationStrategy accAggregationStrategy = new ArrayListAggregationStrategy<Account>();//AccountAggregationStrategy();

    @Override
    public void configure() throws Exception {
        configureGlobalErrorHandling();
        final long aggregateIntervalInMillis = TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS);
        ConstantExpression correlationExpression = new ConstantExpression("${date:now}");
        ddLog.info("ReportsFileBuilder started");
        from("direct:enr")
                .routeId(ROUTE_DIRECT_ENR)
                .processRef("enrichProcessor")
                .log(LoggingLevel.DEBUG, ddLog, "body after enrich is ${body}")
                .aggregate(correlationExpression, accAggregationStrategy)
                .completionTimeout(aggregateIntervalInMillis)
                .marshal().json(JsonLibrary.Jackson, AccountsWrapper.class)
        .to("file:{{reports.file.dir}}?autoCreate=true&charset=UTF-8&fileName=${date:now:yyyy MM dd HH mm ss SSS}.txt")
//        .to("sftp://{{sftp.username}}@{{sftp.host}}:{{sftp.port}}/{{sftp.upload.directory}}?password=demo-user&fileName=${date:now:yyyy MM dd HH mm ss SSS}.txt")
                .log(LoggingLevel.DEBUG, ddLog, "Route ${routeId} finished")
                .end();
    }
    private void configureGlobalErrorHandling() {
        onException(Throwable.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(500)
                .log(LoggingLevel.ERROR, ddLog, "Exception message: ${exception.message}\n${exception.stacktrace}")
        ;
    }

}
