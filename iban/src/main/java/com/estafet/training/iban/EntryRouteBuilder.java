package com.estafet.training.iban;

import com.estafet.training.model.Account;
import com.estafet.training.model.AccountsWrapper;
import com.estafet.training.model.IbanWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Delcho Delov on 21.11.2016 г.
 */
public class EntryRouteBuilder extends RouteBuilder {
    private static final String IBAN_TIMESTAMP_OF_REQUEST = "IbanTimestampOfRequest";
//    private final AggregationStrategy enrichStrategy = new AccountEnrich();
    private final AggregationStrategy accAggregationStrategy = new ArrayListAggregationStrategy<Account>();//AccountAggregationStrategy();
    private final AggregationStrategy stringAgregationStrategy = new ArrayListAggregationStrategy<String>();
    private final Logger ddLog = LoggerFactory.getLogger(EntryRouteBuilder.class);
    private final Logger cvsReportLog = LoggerFactory.getLogger(EntryRouteBuilder.class);

    private EnrichProcessor enrichProcessor;

    public void setEnrichProcessor(EnrichProcessor enrichProcessor) {
        this.enrichProcessor = enrichProcessor;
    }

//    public EntryRouteBuilder(){
//
//    }
    public void configure() throws Exception {
        configureGlobalErrorHandling();
        from("jetty:http://{{entry.route.from.host}}:{{entry.route.from.port}}{{entry.route.from.dir}}?httpMethodRestrict=POST")
                .routeId("entry")
                .streamCaching()
                .unmarshal().json(JsonLibrary.Jackson, IbanWrapper.class)
                .log(LoggingLevel.DEBUG, ddLog, "Route started ${routeId}")
                .setHeader(IBAN_TIMESTAMP_OF_REQUEST).constant("${date:now}")
                .split(simple("${body.getIbans()}"))
        .to("activemq:{{entry.route.to}}")
                .end()
                .log(LoggingLevel.DEBUG, ddLog, "Route ${routeId} finished")
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(200);
        // processing
        final long aggregateIntervalInMillis = TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS);
        ConstantExpression correlationExpression = new ConstantExpression("${date:now}");
        from("activemq:{{processing.route.from}}")
                .log(LoggingLevel.DEBUG, ddLog, "Route started ${routeId}")
                .routeId("processing")
                // filter out duplicate messages by IBAN
                .idempotentConsumer(simple("${body}") , MemoryIdempotentRepository.memoryIdempotentRepository(200))
                    .skipDuplicate(false)
                    .filter(/*exchangeProperty*/property(Exchange.DUPLICATE_MESSAGE).isEqualTo(true))
                    .log(LoggingLevel.DEBUG, ddLog, "\n\n===============duplicate found ${body}")
                    .stop()
                    .end()
                .log(LoggingLevel.INFO, cvsReportLog, "${body}")
                .log(LoggingLevel.DEBUG, ddLog, "body after multicast is ${body}")
                .multicast()
                .to("direct:enr", "direct:csv");

        from("direct:enr")
                .process(enrichProcessor)
                .log(LoggingLevel.DEBUG, ddLog, "body after enrich is ${body}")
                .aggregate(correlationExpression, accAggregationStrategy)
                .completionTimeout(aggregateIntervalInMillis)
                .marshal().json(JsonLibrary.Jackson, AccountsWrapper.class)
        .to("file:{{reports.file.dir}}?autoCreate=true&charset=UTF-8&fileName=${date:now:yyyy MM dd HH mm ss SSS}.txt")
//        .to("sftp://{{sftp.username}}@{{sftp.host}}:{{sftp.port}}/{{sftp.upload.directory}}?password=demo-user&fileName=${date:now:yyyy MM dd HH mm ss SSS}.txt")
                .log(LoggingLevel.DEBUG, ddLog, "Route ${routeId} finished")
                .end();


        // cron task executed every minute that checks for new files in directory and logs them (if any)
        from("file:{{reports.file.dir}}?noop=true&scheduler=quartz2&scheduler.cron=7+0/1+*+*+*+?")
                .multicast()
        .process(new NewFilesLogger()).end();

        Expression csvCorrelationExpression = new ConstantExpression("${body}!=null");
        from("direct:csv")
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

//    private class ConvertStringToMapProcessor implements Processor {
//        @Override
//        public void process(Exchange exchange) throws Exception {
//            ddLog.debug("ConvertAccountToMapProcessor exchange = " + exchange);
//            if(exchange!=null){
//                ddLog.debug("ConvertAccountToMapProcessor exchange payload is "+ exchange.getIn().getBody().getClass());
//            }
//            String iban = exchange.getIn().getBody(String.class);
//            final java.util.Map<String, Object> entry = new HashMap<>(1);
//            if(iban!=null && !iban.isEmpty()) {
//                iban = iban.replaceAll(" ", "");
//                entry.put(iban, iban);
//            }
//            if(entry.isEmpty()){
//                exchange.getIn().setBody(null);
//            }
//            exchange.getIn().setBody(entry);
//        }
//    }
    private class ConvertAccountsWrapperToMapProcessor implements Processor{
        @Override
        public void process(Exchange exchange) throws Exception {
            ddLog.info("ConvertAccountToMapProcessor exchange = " + exchange);
            if(exchange!=null){
                ddLog.info("ConvertAccountToMapProcessor exchange payload is "+ exchange.getIn().getBody().getClass());
            }
            AccountsWrapper<String> ibanList = exchange.getIn().getBody(AccountsWrapper.class);
//            final List<Map<String, Object>> dummyIbanList = new ArrayList<>(3);
            final List<String> accounts = ibanList.getAccounts();
            final java.util.Map<String, Object> entry = new HashMap<>(accounts.size());
            ddLog.info("accounts.size() = " + accounts.size());
            for (String iban : accounts) {
                if(iban!=null && !iban.isEmpty())
                entry.put(iban, iban);
            }
            if(entry.isEmpty()){
                exchange.getIn().setBody(null);
            }
            exchange.getIn().setBody(entry);
        }
    }

    private void configureGlobalErrorHandling() {
        onException(Throwable.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(500)
                .log(LoggingLevel.ERROR, ddLog, "Exception message: ${exception.message}\n${exception.stacktrace}")
        ;
    }
//    private class AccountEnrich implements AggregationStrategy {
//        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
//            ddLog.info("AccountEnrich.aggregate is called");
//            ddLog.info("AccountEnrich.aggregate newExchange payload is "+ newExchange.getIn().getBody().getClass());
////            String iban = newExchange.getIn().getBody(String.class);
//            // newBody is enriched by enrichProcessor (previously called)
//            Account newBody = newExchange.getIn().getBody(Account.class);
//            oldExchange.getIn().setBody(newBody);
//           return oldExchange;
//        }
//    }

//    private class AccountAggregationStrategy implements AggregationStrategy {
//        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
//            ddLog.debug("oldExchange is " + (oldExchange!=null?oldExchange.getIn():"null"));
//            ddLog.debug("newExchange is " + (newExchange!=null?newExchange.getIn():"null"));
//            ddLog.debug("ArrayListAggregationStrategyl.aggregate newExchange payload is "+ newExchange.getIn().getBody().getClass());
//            final Account entry =  newExchange.getIn().getBody(Account.class);
//            ddLog.debug("Account entry " + (entry!=null?entry:"NULL"));
//            AccountsWrapper<Account> accountsWrapper = new AccountsWrapper<Account>();
//            if(oldExchange!=null){
//                accountsWrapper = oldExchange.getIn().getBody(AccountsWrapper.class);
//                if(accountsWrapper==null){
//                    accountsWrapper = new AccountsWrapper();
//                }
//            }else{
//                oldExchange = newExchange.copy();
//            }
//            accountsWrapper.addAccount(entry);
//
//            oldExchange.getIn().setBody(accountsWrapper);
//            return oldExchange;
//        }
//    }
//    private class ArrayListAggregationStrategy<T> implements AggregationStrategy {
//        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
//            ddLog.debug("oldExchange is " + (oldExchange!=null?oldExchange.getIn():"null"));
//            ddLog.debug("newExchange is " + (newExchange!=null?newExchange.getIn():"null"));
//            ddLog.debug("ArrayListAggregationStrategyl.aggregate newExchange payload is "+ newExchange.getIn().getBody().getClass());
//            final T entry = (T) newExchange.getIn().getBody(/*Account.class*/);
//            ddLog.debug("T entry " + (entry!=null?entry:"NULL"));
//            AccountsWrapper<T> accountsWrapper = new AccountsWrapper<T>();
//            if(oldExchange!=null){
//                accountsWrapper = oldExchange.getIn().getBody(AccountsWrapper.class);
//                if(accountsWrapper==null){
//                    accountsWrapper = new AccountsWrapper();
//                }
//            }else{
//                oldExchange = newExchange.copy();
//            }
//            accountsWrapper.addAccount(entry);
//
//            oldExchange.getIn().setBody(accountsWrapper);
//            return oldExchange;
//        }
//    }
}
