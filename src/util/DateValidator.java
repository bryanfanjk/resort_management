package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateValidator {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateValidator() {
    }

    public static boolean isValid(String date) {
        try {
            LocalDate.parse(date, FORMATTER);
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }
}
