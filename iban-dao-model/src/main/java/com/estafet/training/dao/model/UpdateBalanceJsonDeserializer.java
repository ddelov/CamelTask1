package com.estafet.training.dao.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Delcho Delov on 12/12/16.
 */
public class UpdateBalanceJsonDeserializer extends JsonDeserializer<UpdateBalance4Iban> {
    private final Logger ddLog = LoggerFactory.getLogger(UpdateBalanceJsonDeserializer.class);

    @Override
    public UpdateBalance4Iban deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//        ObjectCodec oc = jp.getCodec();
        DaoOperation operation = null;
//        IbanBalanceTuple ibanBalanceTuple=null;
        DaoObject payload = null;
        JsonToken currentToken = null;
        while ((currentToken = jp.nextValue()) != null) {
            ddLog.info("currentToken = " + currentToken);
            ddLog.info("jp.getCurrentName() = "+ jp.getCurrentName());
            switch (currentToken) {
                case VALUE_STRING:
                    ddLog.info(jp.getCurrentName()+ " = "+ jp.getText());
                    if(jp.getCurrentName().equals("operation")){
                        operation= DaoOperation.valueOf(jp.getText());
                    }
                    break;
                case START_OBJECT:
                    if(jp.getCurrentName().equals("payload")){
                        switch (operation){
                            case UPDATE_BALANCE: payload = jp.readValueAs(IbanBalanceTuple.class);break;
                            case PERSIST: payload = (DaoObject) jp.readValuesAs(DbAccount.class);break;
                        }
//                        ibanBalanceTuple = jp.readValueAs(IbanBalanceTuple.class);
                    }
                    break;
                case VALUE_NUMBER_FLOAT:
                    ddLog.info("jp.getDecimalValue() = "+ jp.getDecimalValue());break;
                case VALUE_NUMBER_INT:
                    ddLog.info("jp.getBigIntegerValue() = "+ jp.getBigIntegerValue());break;
            }
        }

        return new UpdateBalance4Iban((IbanBalanceTuple) payload);
    }
}
