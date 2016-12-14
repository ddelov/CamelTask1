package com.estafet.training.dao.model;

import com.estafet.training.model.Account;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Created by Delcho Delov on 13/12/16.
 */
public class DbAccountJsonDeserializer extends JsonDeserializer<DbAccount> {
    @Override
    public DbAccount deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new DbAccount(jp.readValueAs(Account.class));
    }
}
