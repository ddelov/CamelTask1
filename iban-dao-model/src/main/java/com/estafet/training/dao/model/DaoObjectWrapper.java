package com.estafet.training.dao.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * Created by Delcho Delov on 12/12/16.
 */
@JsonDeserialize(using = DaoObjectWrapperJsonDeserializer.class)
@JsonSerialize(using = DaoObjectWrapperJsonSerialiser.class)
public class DaoObjectWrapper<Operation extends DaoOperation, Payload extends DaoObject> implements Serializable{
    protected final Operation operation;// = DaoOperation.UPDATE_BALANCE;
    protected Payload payload; // json

//    public UpdateBalance4Iban(){}
    public DaoObjectWrapper(Operation op, Payload payload)
    {
        this.operation = op;
        this.payload = payload;
    }

    public Operation getOperation() {
        return operation;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    //    public static void main(String[] args) throws JsonProcessingException {
//        final IbanBalanceTuple iban_1200 = new IbanBalanceTuple("IBAN_1200", BigDecimal.valueOf(10234567.89));
//        ObjectMapper mapper = new ObjectMapper();
//        final DaoObjectWrapper balance4Iban = new DaoObjectWrapper(iban_1200);
//        String json = mapper.writeValueAsString(balance4Iban);
//        System.out.println("UpdateBalance4Iban = " + json);
//
////        DaoFacade df = new UpdateBalance4Iban(iban_1200);
////        json = mapper.writeValueAsString(df);
////        System.out.println("DaoFacade = " + json);
//    }
}
