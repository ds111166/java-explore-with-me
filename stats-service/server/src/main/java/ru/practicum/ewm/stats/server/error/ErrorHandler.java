package ru.practicum.ewm.stats.server.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.stats.dto.ErrorResponse;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RestControllerAdvice
public class ErrorHandler {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException exception) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
        log.error("{}: {}", errorResponse.getStatus(), errorResponse.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
        log.error("{}: {}", errorResponse.getStatus(), errorResponse.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable exception) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .reason(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
        log.error("{}: {}", errorResponse.getStatus(), errorResponse.getMessage());
        return errorResponse;
    }
}
