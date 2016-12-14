package com.estafet.training.dao.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Calendar;

import static com.estafet.training.dao.model.UpdatedSince.BG_STAMP_FORMAT;

/**
 * Created by Delcho Delov on 14/12/16.
 */
public class UpdatedSinceJsonSerialiser extends JsonSerializer<UpdatedSince> {

    @Override
    public void serialize(UpdatedSince value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        final Calendar calendar = value.getCalendar();
        jgen.writeStringField("timestamp", BG_STAMP_FORMAT.format(calendar.getTime()));
        jgen.writeEndObject();
    }
}
