package com.estafet.training.dao.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Delcho Delov on 12/12/16.
 */
@JsonDeserialize(using = UpdateBalanceJsonDeserializer.class)
public class UpdateBalance4Iban implements Serializable{
    private final DaoOperation operation = DaoOperation.UPDATE_BALANCE;
    private IbanBalanceTuple payload; // json

//    public UpdateBalance4Iban(){}
    public UpdateBalance4Iban(IbanBalanceTuple ibanBalanceTuple) {
        this.payload = ibanBalanceTuple;
    }

    public static void main(String[] args) throws JsonProcessingException {
        final IbanBalanceTuple iban_1200 = new IbanBalanceTuple("IBAN_1200", BigDecimal.valueOf(10234567.89));
        ObjectMapper mapper = new ObjectMapper();
        final UpdateBalance4Iban balance4Iban = new UpdateBalance4Iban(iban_1200);
        String json = mapper.writeValueAsString(balance4Iban);
        System.out.println("UpdateBalance4Iban = " + json);

//        DaoFacade df = new UpdateBalance4Iban(iban_1200);
//        json = mapper.writeValueAsString(df);
//        System.out.println("DaoFacade = " + json);
    }

//    @Override
    public DaoOperation getOperation() {
        return DaoOperation.UPDATE_BALANCE;
    }

//    @Override
//    public void setOperation(DaoOperation operation) {
//        this.operation = DaoOperation.UPDATE_BALANCE;
//    }

//    @Override
    public IbanBalanceTuple getPayload() {
        return payload;
    }

//    @Override
    public void setPayload(IbanBalanceTuple payload) {
        this.payload = payload;
    }
}
