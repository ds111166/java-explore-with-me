package ru.practicum.ewm.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UpdateEventTimeStampValidator implements ConstraintValidator<UpdateEventTimeStampValidate, String> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(s, formatter);
            return LocalDateTime.now().isBefore(dateTime);
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
