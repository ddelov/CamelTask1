package com.estafet.training.iban;

import com.estafet.training.iban.strategy.ArrayListAggregationStrategy;
import com.estafet.training.model.AccountsWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by Delcho Delov on 02/12/16.
 */
public class ArrayListAggregationStrategyTest extends CamelTestSupport{
    private static final String IBANS[] = {"BG66 ESTF 0616 0000 0000 21", "BG66 ESTF 0616 0000 0000 22", "BG66 ESTF 0616 0000 0000 23"};
    private static final Mechka MECHKI[] = {new Mechka("Big", false), new Mechka("Small", true)};

    @Test
    public void testAggregateStrings() throws Exception {
        final ArrayListAggregationStrategy<String> stringStrategy = new ArrayListAggregationStrategy<String>();
        final Exchange firstExchange = createExchangeWithBody(IBANS[0]);
        Exchange oldExchange = null;
        oldExchange = stringStrategy.aggregate(oldExchange, firstExchange);
        assertNotNull(oldExchange);
        Object body = oldExchange.getIn().getBody();
        assertIsInstanceOf(AccountsWrapper.class, body);
        AccountsWrapper accountsWrapper = oldExchange.getIn().getBody(AccountsWrapper.class);
        List accounts = accountsWrapper.getAccounts();
        assertNotNull(accounts);
        assertTrue(!accounts.isEmpty());
        assertThat(accounts.size(), is(1));
        assertIsInstanceOf(String.class, accounts.get(0));
        String  iban = (String) accounts.get(0);
        assertThat(iban, is(IBANS[0]));
        oldExchange = stringStrategy.aggregate(oldExchange, createExchangeWithBody(IBANS[1]));
        accountsWrapper = oldExchange.getIn().getBody(AccountsWrapper.class);
        accounts = accountsWrapper.getAccounts();
        assertThat(accounts.size(), is(2));
        String  iban2 = (String) accounts.get(1);
        assertThat(iban2, is(IBANS[1]));
    }

    @Test
    public void testAggregateMechki() throws Exception {
        final ArrayListAggregationStrategy<Mechka> mechaStrategy = new ArrayListAggregationStrategy<Mechka>();
        final Exchange firstExchange = createExchangeWithBody(MECHKI[0]);
        Exchange oldExchange = null;
        oldExchange = mechaStrategy.aggregate(oldExchange, firstExchange);
        assertNotNull(oldExchange);
        Object body = oldExchange.getIn().getBody();
        assertIsInstanceOf(AccountsWrapper.class, body);
        AccountsWrapper accountsWrapper = oldExchange.getIn().getBody(AccountsWrapper.class);
        List accounts = accountsWrapper.getAccounts();
        assertNotNull(accounts);
        assertTrue(!accounts.isEmpty());
        assertThat(accounts.size(), is(1));
        assertIsInstanceOf(Mechka.class, accounts.get(0));
        Mechka bigOne = (Mechka) accounts.get(0);
        assertThat(bigOne.getName(), is("Big"));
        assertFalse(bigOne.isMeatEating());
        assertThat(bigOne, is(MECHKI[0]));

        oldExchange = mechaStrategy.aggregate(oldExchange, createExchangeWithBody(MECHKI[1]));
        accountsWrapper = oldExchange.getIn().getBody(AccountsWrapper.class);
        accounts = accountsWrapper.getAccounts();
        assertThat(accounts.size(), is(2));
        Mechka bear2 = (Mechka) accounts.get(1);
        assertThat(bear2, is(MECHKI[1]));
    }

}