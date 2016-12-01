package com.estafet.training.iban;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Delcho Delov on 30/11/16.
 */
class DirectProcessor<Payload> implements Processor {
//    private EntryRouteBuilder entryRouteBuilder;
    private final Logger ddLog = LoggerFactory.getLogger(DirectProcessor.class);
    protected Payload entry;

//    public DirectProcessor(EntryRouteBuilder entryRouteBuilder) {
//        this.entryRouteBuilder = entryRouteBuilder;
//    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ddLog.debug("DirectProcessor exchange = " + exchange);
        if (exchange != null) {
            ddLog.debug("DirectProcessor exchange payload is " + exchange.getIn().getBody().getClass());
            entry = (Payload) exchange.getIn().getBody();
        }
    }

    public Payload getEntry() {
        return entry;
    }
}
