package com.estafet.training.dao.processor;

import com.estafet.training.dao.model.DaoObjectWrapper;
import com.estafet.training.dao.model.DaoOperation;
import com.estafet.training.dao.model.UpdatedSince;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Calendar;

/**
 * Created by Delcho Delov on 14/12/16.
 */
public class Check4UpdatedAccProcessor implements Processor{

    @Override
    public void process(Exchange exchange) throws Exception {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        final DaoObjectWrapper wrapper = new DaoObjectWrapper(DaoOperation.FIND_UPDATED_SINCE, new UpdatedSince(calendar));
        exchange.getIn().setBody(wrapper);
    }

}
