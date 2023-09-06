package es.gobcan.istac.edatos.external.users.rest.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FormatDateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate formatBirthdayStringToLocalDate(String date) {
        if (date != null) {
            return LocalDate.parse(date, DATE_FORMATTER);
        }
        return null;
    }

    public static String formatBirthdayLocalDateToString(LocalDate date) {
        if (date != null) {
            return date.format(DATE_FORMATTER);
        }
        return null;
    }

}
