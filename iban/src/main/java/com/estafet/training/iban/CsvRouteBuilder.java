package com.estafet.training.iban;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Delcho Delov on 02/12/16.
 */
public class CsvRouteBuilder extends RouteBuilder {
    private static final String ROUTE_DIRECT_CSV = "directCsv";
    private final AggregationStrategy stringAgregationStrategy = new ArrayListAggregationStrategy<String>();
    private final Logger ddLog = LoggerFactory.getLogger(CsvRouteBuilder.class);

    @Override
    public void configure() throws Exception {
        configureGlobalErrorHandling();
        Expression csvCorrelationExpression = new ConstantExpression("${body}!=null");
        final long aggregateIntervalInMillis = TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS);
        from("direct:csv")
                .routeId(ROUTE_DIRECT_CSV)
                .process(new Iban2CsvProcessor())
                .aggregate(csvCorrelationExpression, stringAgregationStrategy)
                .completionTimeout(aggregateIntervalInMillis)
                .process(new ConvertAccountsWrapperToMapProcessor())
                .log(LoggingLevel.INFO, ddLog, "CSV processing body before marshal is ${body}")
                .marshal()
                .csv()
        .to("file:{{reports.file.dir}}?autoCreate=true&fileExist=Append&charset=UTF-8&fileName=${date:now:yyyy MM dd}.csv")
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
