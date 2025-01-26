package com.intranet.kch.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormatUtil {

    public static String localDateTimeToString(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH);
        return localDateTime.format(formatter);
    }

    public static String localDateToString(LocalDate localDate, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH);
        return localDate.format(formatter);
    }

    public static String stringToLocalDateTimeString(String date, String format) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return localDateTimeToString(dateTime, format);
    }

    public static String stringToLocalDateString(String date, String format) {
        LocalDate localDate = LocalDate.parse(date);
        return localDateToString(localDate, format);
    }
}
