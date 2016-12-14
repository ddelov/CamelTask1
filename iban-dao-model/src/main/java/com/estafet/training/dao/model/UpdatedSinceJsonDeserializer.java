package com.estafet.training.dao.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by Delcho Delov on 13/12/16.
 */
public class UpdatedSinceJsonDeserializer extends JsonDeserializer<UpdatedSince> {
    private final Logger ddLog = LoggerFactory.getLogger(UpdatedSinceJsonDeserializer.class);

    @Override
    public UpdatedSince deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken currentToken;
        Calendar calendar = Calendar.getInstance(UpdatedSince.BG_LOCALE);
        while ((currentToken = jp.nextValue()) != null) {
            ddLog.debug("currentToken = " + currentToken);
            ddLog.debug("jp.getCurrentName() = "+ jp.getCurrentName());
            switch (currentToken) {
                case VALUE_STRING:
                    final String stampParameter = jp.getText();
                    ddLog.info(jp.getCurrentName()+ " = "+ stampParameter);
                    if(jp.getCurrentName().equals("timestamp")){
                        try {
                            calendar.setTime(UpdatedSince.BG_STAMP_FORMAT.parse(stampParameter));
                        } catch (ParseException e) {
                            ddLog.error("Could not parse timestamp value "+ stampParameter + " in format " + UpdatedSince.STAMP_PATTERN);
                        }
                        return  new UpdatedSince(calendar);
                    }
                    break;
            }
        }
        return null;
    }
}
