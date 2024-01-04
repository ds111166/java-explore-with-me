package ru.practicum.ewm.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.PARAMETER, ElementType.TYPE, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = UpdateEventTimeStampValidator.class)
@Documented
public @interface UpdateEventTimeStampValidate {
    String message() default "{Update Event Timestamp.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
