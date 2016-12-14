package com.estafet.training.dao.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Delcho Delov on 12/12/16.
 */
public class IbanBalanceTupleJsonDeserializer extends JsonDeserializer<IbanBalanceTuple> {
    private final Logger ddLog = LoggerFactory.getLogger(IbanBalanceTupleJsonDeserializer.class);

    @Override
    public IbanBalanceTuple deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken currentToken = null;
        String iban = null;
        BigDecimal newBalance = BigDecimal.ZERO;
        while ((currentToken = jp.nextValue()) != null) {
            ddLog.info("currentToken = " + currentToken);
            ddLog.info("jp.getCurrentName() = "+ jp.getCurrentName());
            switch (currentToken) {
                case VALUE_STRING:
                    ddLog.info(jp.getCurrentName()+ " = "+ jp.getText());
                    if(jp.getCurrentName().equals("iban")){
                        iban=jp.getText();
                    }
                    break;
                case VALUE_NUMBER_FLOAT:
                    ddLog.info("jp.getDecimalValue() = "+ jp.getDecimalValue());
                    if(jp.getCurrentName().equals("newBalance")){
                        newBalance = jp.getDecimalValue();
                    }
                    break;
            }
        }
        return new IbanBalanceTuple(iban, newBalance);
    }
}
