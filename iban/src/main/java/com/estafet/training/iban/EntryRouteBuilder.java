package com.estafet.training.iban;

import com.estafet.training.model.Account;
import com.estafet.training.model.AccountsWrapper;
import com.estafet.training.model.IbanWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.concurrent.TimeUnit;

/**
 * Created by Delcho Delov on 21.11.2016 г.
 */
public class EntryRouteBuilder extends RouteBuilder {
    private static final String IBAN_TIMESTAMP_OF_REQUEST = "IbanTimestampOfRequest";
    private final AggregationStrategy enrichStrategy = new AccountEnrich();
    private final AggregationStrategy aggregationStrategy = new ArrayListAggregationStrategy();

    private EnrichProcessor enrichProcessor;

    public void setEnrichProcessor(EnrichProcessor enrichProcessor) {
        this.enrichProcessor = enrichProcessor;
    }

    public void configure() throws Exception {
        configureGlobalErrorHandling();

        from("jetty:http://localhost:20616/estafet/iban/report?httpMethodRestrict=POST")
                .routeId("entry")
                .streamCaching()
                .unmarshal().json(JsonLibrary.Jackson, IbanWrapper.class)
                .log(LoggingLevel.DEBUG, "Route started ${routeId}")
                .setHeader(IBAN_TIMESTAMP_OF_REQUEST).constant("${date:now}")
                .split(simple("${body.getIbans()}"))
        .to("activemq:queue:estafet.iban.report.splitted.queue")
                .end()
                .log(LoggingLevel.DEBUG, "Route ${routeId} finished")
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(200);
        // processing
        final long aggregateIntervalInMillis = TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS);
        log.info("incoming header is "+header(IBAN_TIMESTAMP_OF_REQUEST));
        ConstantExpression correlationExpression = new ConstantExpression("${date:now}");
        from("activemq:queue:estafet.iban.report.splitted.queue")
                .routeId("processing")
                .log(LoggingLevel.DEBUG, "Route started ${routeId}")
                .enrich("direct:enr", enrichStrategy)
                .aggregate(correlationExpression, aggregationStrategy)
                .completionTimeout(aggregateIntervalInMillis)
                .marshal().json(JsonLibrary.Jackson, AccountsWrapper.class)
//                .convertBodyTo(String.class, "UTF-8")
        .to("file:/u01/data/iban/reports/?autoCreate=true&charset=UTF-8&fileName=${date:now:yyyy MM dd HH mm ss SSS}.txt")
        .end()
        .log(LoggingLevel.DEBUG, "Route ${routeId} finished");

        from("direct:enr").process(enrichProcessor);
    }


    private void configureGlobalErrorHandling() {
        onException(Throwable.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(500)
                .log(LoggingLevel.ERROR, "Exception message: ${exception.message}\n${exception.stacktrace}")
        ;
    }
    private class AccountEnrich implements AggregationStrategy {
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            // newBody is enriched
            Account newBody = newExchange.getIn().getBody(Account.class);
            oldExchange.getIn().setBody(newBody);
           return oldExchange;
        }
    }
    private class ArrayListAggregationStrategy implements AggregationStrategy {
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            log.debug("oldExchange is " + oldExchange);
            log.debug("newExchange is " + newExchange);

            final Account entry =  newExchange.getIn().getBody(Account.class);
            log.info("com.estafet.training.IbanSingleReportEntity entry " + entry.toString());
            log.info("header(IBAN_TIMESTAMP_OF_REQUEST) is " + newExchange.getIn().getHeader(IBAN_TIMESTAMP_OF_REQUEST, String.class));
            AccountsWrapper accountsWrapper = new AccountsWrapper();
            if(oldExchange!=null){
                accountsWrapper = oldExchange.getIn().getBody(AccountsWrapper.class);
                if(accountsWrapper==null){
                    accountsWrapper = new AccountsWrapper();
                }
            }else{
                oldExchange = newExchange.copy();
            }
            accountsWrapper.addAccount(entry);

            oldExchange.getIn().setBody(accountsWrapper);
            return oldExchange;
        }
    }

}
