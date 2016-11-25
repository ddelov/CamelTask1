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
public class EnrichProcessor implements Processor {
    private AccountServiceApi accountEnricherService;
    protected Logger log = LoggerFactory.getLogger(getClass());


    public EnrichProcessor(){
//        this.accountEnricherService = service;
    }

    public void setAccountEnricherService(AccountServiceApi accountEnricherService) {
        this.accountEnricherService = accountEnricherService;
    }

    public void process(Exchange exchange) throws Exception {
        final String iban = (String) exchange.getIn().getBody();
        log.debug("incoming IBAN "+ iban);
        final Account acc = accountEnricherService.getAccountByIban(iban);
        exchange.getIn().setBody(acc);
        log.debug("After aggregation " + acc.toString());
    }
}

