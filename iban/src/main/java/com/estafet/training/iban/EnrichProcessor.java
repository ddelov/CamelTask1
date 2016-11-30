package com.estafet.training.iban;

import com.estafet.training.api.AccountServiceApi;
import com.estafet.training.model.Account;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Delcho Delov on 24/11/16.
 */
public final class EnrichProcessor implements Processor {
    private AccountServiceApi accountEnricherService;
    private final Logger ddLog = LoggerFactory.getLogger(EnrichProcessor.class);


//    public EnrichProcessor(){
////        this.accountEnricherService = service;
//    }

    public void setAccountEnricherService(AccountServiceApi accountEnricherService) {
        this.accountEnricherService = accountEnricherService;
    }

    public void process(Exchange exchange) throws Exception {
        ddLog.info("EnrichProcessor.process is called");
        final String iban = (String) exchange.getIn().getBody();
        ddLog.info("incoming IBAN "+ iban);
        final Account acc = accountEnricherService.getAccountByIban(iban);
        exchange.getIn().setBody(acc);
        ddLog.info("After aggregation " + acc.toString());
    }
}

