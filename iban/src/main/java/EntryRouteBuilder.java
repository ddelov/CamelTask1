import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
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
    private final Processor enrichProcessor = new EnrichProcessor();
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
        .to("file:/u01/data/iban/reports/?autoCreate=true&fileName=${date:now:yyyy MM dd HH mm ss SSS}.txt")
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
    private class EnrichProcessor implements Processor{
        public void process(Exchange exchange) throws Exception {
            final String iban = (String) exchange.getIn().getBody();
            log.debug("incoming IBAN "+ iban);
            final IbanSingleReportEntity acc = new IbanSingleReportEntity(iban);
            exchange.getIn().setBody(acc);
            log.debug("After aggregation " + acc.toString());
        }
    }
    private class AccountEnrich implements AggregationStrategy {
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            // newBody is enriched
            IbanSingleReportEntity newBody = newExchange.getIn().getBody(IbanSingleReportEntity.class);
            oldExchange.getIn().setBody(newBody);
           return oldExchange;
        }
    }
    private class ArrayListAggregationStrategy implements AggregationStrategy {
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            log.debug("oldExchange is " + oldExchange);
            log.debug("newExchange is " + newExchange);

            final IbanSingleReportEntity entry =  newExchange.getIn().getBody(IbanSingleReportEntity.class);
            log.info("IbanSingleReportEntity entry " + entry.toString());
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
