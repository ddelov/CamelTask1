package com.estafet.training.dao.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by Delcho Delov on 14/12/16.
 */
public class DaoObjectWrapperJsonSerialiser extends JsonSerializer<DaoObjectWrapper> {
    @Override
    public void serialize(DaoObjectWrapper value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("operation", value.getOperation().name());
        jgen.writeObjectField("payload", value.getPayload());
        jgen.writeEndObject();
    }
}
