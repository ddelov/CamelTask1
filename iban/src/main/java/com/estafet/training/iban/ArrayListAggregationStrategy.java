package com.estafet.training.iban;

import com.estafet.training.model.AccountsWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Delcho Delov on 30/11/16.
 */
public class ArrayListAggregationStrategy<T> implements AggregationStrategy {
    private final Logger ddLog = LoggerFactory.getLogger(ArrayListAggregationStrategy.class);

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        ddLog.debug("oldExchange is " + (oldExchange!=null?oldExchange.getIn():"null"));
        ddLog.debug("newExchange is " + (newExchange!=null?newExchange.getIn():"null"));
        ddLog.debug("ArrayListAggregationStrategyl.aggregate newExchange payload is "+ newExchange.getIn().getBody().getClass());
        final T entry = (T) newExchange.getIn().getBody(/*Account.class*/);
        ddLog.debug("T entry " + (entry!=null?entry:"NULL"));
        AccountsWrapper<T> accountsWrapper = new AccountsWrapper<T>();
        if(oldExchange!=null){
            accountsWrapper = oldExchange.getIn().getBody(AccountsWrapper.class);
            if(accountsWrapper==null){
                accountsWrapper = new AccountsWrapper();
            }
        }else{
            oldExchange = newExchange.copy();
        }
        accountsWrapper.addAccount(entry);

        oldExchange.getIn().setBody(accountsWrapper);
        return oldExchange;
    }
}
