package com.estafet.training.iban;

import com.estafet.training.model.AccountsWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by Delcho Delov on 02/12/16.
 */
public class ConvertAccountsWrapperToMapProcessorTest extends CamelTestSupport {
    private static final String ONE = "OnE";
    private static final String TWO = "TWo";
    private static final String EMPTY_STRING = "";
    private final ConvertAccountsWrapperToMapProcessor processor = new ConvertAccountsWrapperToMapProcessor();
    @Test
    public void process() throws Exception {
        // in :  exchange.getIn().getBody AccountsWrapper<String>
        // out: Map<String, String> where key=value= current entry of wrapper.
        AccountsWrapper<String> inBody = new AccountsWrapper<>();
        inBody.addAccount(ONE);
        inBody.addAccount(TWO);
        inBody.addAccount(null);
        inBody.addAccount(EMPTY_STRING);
        final Exchange inExchange = createExchangeWithBody(inBody);
//        Object body = inExchange.getIn().getBody();
//        assertIsInstanceOf(AccountsWrapper.class, body);
        processor.process(inExchange);
        assertNotNull(inExchange);
        Object body = inExchange.getIn().getBody();
        assertNotNull(body);
        assertIsInstanceOf(java.util.Map.class, body);
        Map outMap = inExchange.getIn().getBody(Map.class);
        assertThat(outMap.size(), is(2));
        assertTrue(outMap.containsKey(ONE));
        assertTrue(outMap.containsKey(TWO));
        assertIsInstanceOf(String.class, outMap.get(ONE));
        String firstVal = (String) outMap.get(ONE);
        assertThat(firstVal, is(ONE));
        String secVal = (String) outMap.get(TWO);
        assertThat(secVal, is(TWO));
    }

}