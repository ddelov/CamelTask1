package com.estafet.training.dao.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

/**
 * Created by Delcho Delov on 12/12/16.
 */
@JsonDeserialize(using = IbanBalanceTupleJsonDeserializer.class)
public class IbanBalanceTuple implements DaoObject {
//    private static final BigDecimal DUMMY_BALANCE = BigDecimal.ZERO;
//    private static final String DUMMY_IBAN = "";
    private Tuple<String, BigDecimal> _holder;

    IbanBalanceTuple(){}

    IbanBalanceTuple(String key, BigDecimal value) {
        _holder = new Tuple<>(key, value);
    }

    public String getIban() {
        return _holder.getKey();
    }

    public void setIban(String iban) {
        _holder.key = iban;
    }

    public BigDecimal getNewBalance() {
        return _holder.getValue();
    }

    public void setNewBalance(BigDecimal newBalance) {
        _holder.value = newBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IbanBalanceTuple that = (IbanBalanceTuple) o;

        return _holder.equals(that._holder);

    }

    @Override
    public int hashCode() {
        return _holder.hashCode();
    }

    @Override
    public String toString() {
        return "IbanBalanceTuple{\"iban\":\""+ getIban() +"\",\"newBalance\":"+ getNewBalance()+"}";
    }

    public static void main(String[] args) throws JsonProcessingException {
        final IbanBalanceTuple iban_1200 = new IbanBalanceTuple("IBAN_1200", BigDecimal.valueOf(10234567.89));
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(iban_1200);
        System.out.println("iban_1200 = " + json);
    }

}
