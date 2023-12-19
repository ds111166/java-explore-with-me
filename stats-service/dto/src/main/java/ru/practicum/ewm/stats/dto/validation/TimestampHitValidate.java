package ru.practicum.ewm.stats.dto.validation;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.PARAMETER, ElementType.TYPE, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = TimestampHitValidator.class)
@Documented
public @interface TimestampHitValidate {
    String message() default "{Timestamp Hit.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
