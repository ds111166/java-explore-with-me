package ru.practicum.ewm.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE, FIELD})
@Constraint(validatedBy = UpdateRequestStatusValidator.class)
public @interface UpdateRequestStatusValidate {
    String message() default "{Update Request Status.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
