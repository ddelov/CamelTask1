package com.estafet.training.iban;

import com.estafet.training.model.AccountsWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Delcho Delov on 02/12/16.
 */
public class ConvertAccountsWrapperToMapProcessor implements Processor {
    private final Logger ddLog = LoggerFactory.getLogger(ConvertAccountsWrapperToMapProcessor.class);
    // out: Map<String, String> where key=value= current entry of wrapper. Empty or null entries ar voided
    @Override
    public void process(Exchange exchange) throws Exception {
        ddLog.info("ConvertAccountToMapProcessor exchange = " + exchange);
        if(exchange!=null){
            ddLog.info("ConvertAccountToMapProcessor exchange payload is "+ exchange.getIn().getBody().getClass());
        }
        AccountsWrapper<String> ibanList = exchange != null ? exchange.getIn().getBody(AccountsWrapper.class) : null;
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
