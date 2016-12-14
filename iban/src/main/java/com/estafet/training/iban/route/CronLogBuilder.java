package com.estafet.training.iban.route;

import com.estafet.training.iban.processor.NewFilesLogger;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Delcho Delov on 02/12/16.
 */
public class CronLogBuilder extends RouteBuilder {
    static final String ROUTE_FILE_CRON = "fileCron";
    private final Logger ddLog = LoggerFactory.getLogger(CronLogBuilder.class);

    @Override
    public void configure() throws Exception {
        configureGlobalErrorHandling();
        // cron task executed every hour that checks for new files in directory and logs them (if any)
        from("file:{{reports.file.dir}}?noop=true&scheduler=quartz2&scheduler.cron=0+0+0/1+*+*+?")
               .routeId(ROUTE_FILE_CRON)
        .process(new NewFilesLogger()).end();

    }
    private void configureGlobalErrorHandling() {
        onException(Throwable.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(500)
                .log(LoggingLevel.ERROR, ddLog, "Exception message: ${exception.message}\n${exception.stacktrace}")
        ;
    }
}
