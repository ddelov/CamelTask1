package com.estafet.training.dao.processor;

import com.estafet.training.dao.api.IbanDaoApi;
import com.estafet.training.model.Account;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Delcho Delov on 08/12/16.
 */
@Deprecated
public class PersistAccountProcessor implements Processor {
    private final Logger ddLog = LoggerFactory.getLogger(PersistAccountProcessor.class);
    private IbanDaoApi ibanDaoService;

    public void setIbanDaoService(IbanDaoApi ibanDaoService) {
        this.ibanDaoService = ibanDaoService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ddLog.info("PersistAccountProcessor.process is called");
        final Account account = exchange.getIn().getBody(Account.class);
        ddLog.info("incoming account "+ account);
        ddLog.info("\n---------->ibanDaoService "+ ibanDaoService);
        ibanDaoService.insertAccount(account);
        final String iban = account.getIban();
        ddLog.info("Account inserted. Check out in DB for IBAN " + iban);
//        final Account foundRecord = ibanDaoService.findAccountByIban(iban);
//        ddLog.info("Found record "+ foundRecord);
//        exchange.getIn().setBody(foundRecord);
    }
}
