package com.estafet.training.iban;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Delcho Delov on 29/11/16.
 */
public class Iban2CsvProcessor implements org.apache.camel.Processor {
    private final Logger ddLog = LoggerFactory.getLogger(Iban2CsvProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        ddLog.info("Iban2CvsProcessor exchange = " + exchange);
        if(exchange!=null){
            ddLog.info("Iban2CvsProcessor exchange payload is "+ exchange.getIn().getBody().getClass());
        }
        final String iban =  exchange.getIn().getBody(String.class);
        if(iban!=null){
            final String body = iban.replaceAll(" ", "");
            exchange.getIn().setBody(body);
            ddLog.info("Iban2CvsProcessor output is "+ body);
        }
    }
}
