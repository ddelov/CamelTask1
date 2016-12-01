package com.estafet.training.iban;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Delcho Delov on 30/11/16.
 */
class NewFilesLogger extends DirectProcessor<org.apache.camel.component.file.GenericFile> {
    private final Logger ddLog = LoggerFactory.getLogger(NewFilesLogger.class);

//    public NewFilesLogger(EntryRouteBuilder entryRouteBuilder) {
//        super(entryRouteBuilder);
//    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);
        ddLog.info("New file named [" + entry.getFileName() + "] detected");
    }
}

