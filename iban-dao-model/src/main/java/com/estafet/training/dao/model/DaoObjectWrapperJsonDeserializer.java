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
 * Created by Delcho Delov on 13/12/16.
 */
public class DaoObjectWrapperJsonDeserializer extends JsonDeserializer<DaoObjectWrapper> {
    private final Logger ddLog = LoggerFactory.getLogger(DaoObjectWrapperJsonDeserializer.class);

    @Override
    public DaoObjectWrapper deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        DaoOperation operation = null;
//        IbanBalanceTuple ibanBalanceTuple=null;
        DaoObject payload = null;
        JsonToken currentToken = null;
        while ((currentToken = jp.nextValue()) != null) {
            ddLog.debug("currentToken = " + currentToken);
            ddLog.debug("jp.getCurrentName() = "+ jp.getCurrentName());
            switch (currentToken) {
                case VALUE_STRING:
                    ddLog.debug(jp.getCurrentName()+ " = "+ jp.getText());
                    if(jp.getCurrentName().equals("operation")){
                        operation= DaoOperation.valueOf(jp.getText());
                    }
                    break;
                case START_OBJECT:
                    if(jp.getCurrentName().equals("payload")){
                        switch (operation){
                            case PERSIST: payload = jp.readValueAs(DbAccount.class);break;
                            case UPDATE_BALANCE: payload = jp.readValueAs(IbanBalanceTuple.class);break;
                            case FIND_UPDATED_SINCE: payload = jp.readValueAs(UpdatedSince.class);break;
                        }
//                        ibanBalanceTuple = jp.readValueAs(IbanBalanceTuple.class);
                    }
                    break;
                case VALUE_NUMBER_FLOAT:
                    ddLog.debug("jp.getDecimalValue() = "+ jp.getDecimalValue());break;
                case VALUE_NUMBER_INT:
                    ddLog.debug("jp.getBigIntegerValue() = "+ jp.getBigIntegerValue());break;
            }
        }

        return new DaoObjectWrapper(operation, payload);
    }
}
