package ru.practicum.ewm.validation;

import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UpdateRequestStatusValidator implements ConstraintValidator<UpdateRequestStatusValidate,
        EventRequestStatusUpdateRequest> {

    @Override
    public boolean isValid(
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        if (eventRequestStatusUpdateRequest == null) {
            return false;
        }
        String status = eventRequestStatusUpdateRequest.getStatus();
        return "CONFIRMED".equalsIgnoreCase(status) || "REJECTED".equalsIgnoreCase(status);
    }
}
