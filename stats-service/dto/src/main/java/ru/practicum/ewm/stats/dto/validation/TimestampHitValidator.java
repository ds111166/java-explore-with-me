package ru.practicum.ewm.stats.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimestampHitValidator implements ConstraintValidator<TimestampHitValidate, String> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        }
        try {
            LocalDateTime.parse(s, formatter);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
