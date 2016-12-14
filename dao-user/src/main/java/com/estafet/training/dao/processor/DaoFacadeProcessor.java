package com.estafet.training.dao.processor;

import com.estafet.training.dao.api.IbanDaoApi;
import com.estafet.training.dao.model.*;
import com.estafet.training.model.Account;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by Delcho Delov on 09/12/16.
 */
public class DaoFacadeProcessor implements Processor{
    private final Logger ddLog = LoggerFactory.getLogger(DaoFacadeProcessor.class);
    private IbanDaoApi ibanDaoService;

    public void setIbanDaoService(IbanDaoApi ibanDaoService) {
        this.ibanDaoService = ibanDaoService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ddLog.info("DaoFacadeProcessor.process is called");
//        final UpdateBalance4Iban daoFacade = exchange.getIn().getBody(UpdateBalance4Iban.class);
        final DaoObjectWrapper wrapper = exchange.getIn().getBody(DaoObjectWrapper.class);
//        ddLog.info("\n---------->ibanDaoService "+ ibanDaoService);
//        final IbanBalanceTuple payload = daoFacade.getPayload();
        final DaoOperation operation = wrapper.getOperation();
        ddLog.info("incoming operation "+ operation);
        final DaoObject payload = wrapper.getPayload();
        ddLog.info("incoming peyload "+ payload);
        switch (operation){
            case UPDATE_BALANCE:
                final IbanBalanceTuple tuple = (IbanBalanceTuple) payload;
                ibanDaoService.updateBalance(tuple.getIban(), tuple.getNewBalance());
                break;
            case PERSIST:
                final DbAccount account = (DbAccount)payload;
                ibanDaoService.insertAccount(account.getAccount());
                final String iban = account.getIban();
                ddLog.info("Account inserted. Check out in DB for IBAN " + iban);
                break;
            case FIND_UPDATED_SINCE:
                final UpdatedSince updatedSince = (UpdatedSince) payload;
                final Set<Account> allUpdatedAfter = ibanDaoService.findAllUpdatedAfter(updatedSince.getCalendar());
                int count = allUpdatedAfter!=null?allUpdatedAfter.size():0;
                final String humanReadable = updatedSince.getHumanReadable();
                final String fileName = count + (count == 1 ? " account updated since " : " accounts updated since ") + humanReadable + ".txt";
                ddLog.info("Found " + fileName);
                exchange.getIn().setHeader(Exchange.FILE_NAME, fileName);
                exchange.getIn().setBody(allUpdatedAfter);
                break;
        }
    }
}
