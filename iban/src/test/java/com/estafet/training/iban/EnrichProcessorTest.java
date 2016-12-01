package com.estafet.training.iban;

import com.estafet.training.api.AccountServiceApi;
import com.estafet.training.model.Account;
import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by Delcho Delov on 01/12/16.
 */
public class EnrichProcessorTest extends CamelTestSupport {
//    private static final Logger LOGGER = Logger.getLogger(EnrichProcessorTest.class.getName());
    private final EnrichProcessor processor = new EnrichProcessor();
    private final static String IBAN_ONE = "BG66 ESTF 0616 0000 0000 01";
    private final static String OWNER = "D. Delov";
    private static final BigDecimal BALANCE = new BigDecimal(7737.73).setScale(2, BigDecimal.ROUND_HALF_UP);
    private static final String CURRENCY = "£";
    private static final AccountServiceApi dummyAccServiceImpl = new ConstAccService();

    @Test
    public void testAccountEnricherService() {
        Account account = dummyAccServiceImpl.getAccountByIban(IBAN_ONE);
        assertIsInstanceOf(Account.class, account);
        assertNotNull(account);
        assertThat(account.getIban(), is(IBAN_ONE));
        assertThat(account.getName(), is(OWNER));
        assertThat(account.getBalance(), is(BALANCE.doubleValue()));
        assertThat(account.getCurrency(), is(CURRENCY));
    }
    @Test()//depends on testAccountEnricherService
    public void testProcess() throws Exception {
        final Exchange exchange = createExchangeWithBody(IBAN_ONE);
        try {
            processor.setAccountEnricherService(dummyAccServiceImpl);
            processor.process(exchange);
            Object body = exchange.getIn().getBody();
            assertNotNull(body);
            assertThat(Account.class.getName(), is(body.getClass().getName()));
            Account account = (Account) body;
            assertThat(account.getIban(), is(IBAN_ONE));
            assertThat(account.getName(), is(OWNER));
            assertThat(account.getBalance(), is(BALANCE.doubleValue()));
            assertThat(account.getCurrency(), is(CURRENCY));
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }
    }
    public static final class ConstAccService implements AccountServiceApi{
        @Override
        public Account getAccountByIban(String iban) {
            return new Account(iban, OWNER, BALANCE.doubleValue(), CURRENCY);
        }
    }
}