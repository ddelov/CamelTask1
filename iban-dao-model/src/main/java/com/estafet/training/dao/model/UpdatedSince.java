package com.estafet.training.dao.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Delcho Delov on 13/12/16.
 */
@JsonDeserialize(using = UpdatedSinceJsonDeserializer.class)
public final class UpdatedSince implements DaoObject {
    private final Calendar calendar;
    public static final String STAMP_PATTERN = "dd/MM/yy HH:mm:ss +0200";
    public static final SimpleDateFormat BG_STAMP_FORMAT = new SimpleDateFormat(STAMP_PATTERN);
    public static final String H_PATTERN = "yy_MM_dd HH:mm:ss";
    public static final SimpleDateFormat H_FORMAT = new SimpleDateFormat(H_PATTERN);
    public static final Locale BG_LOCALE = new Locale("bg_BG");

    public UpdatedSince(Calendar calendar) {
        this.calendar = calendar;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public String getHumanReadable() {
        return H_FORMAT.format(calendar.getTime());
    }

    public static void main(String[] args) throws ParseException {
        String input = "13/12/16 15:01:29 +0200";
        Date date = BG_STAMP_FORMAT.parse(input);
        System.out.println("date = " + date);
    }
}
