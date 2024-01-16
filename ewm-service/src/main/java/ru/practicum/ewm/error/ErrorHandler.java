package ru.practicum.ewm.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.stats.dto.ErrorResponse;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RestControllerAdvice
public class ErrorHandler {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
        log.error("{}: {}", errorResponse.getStatus(), errorResponse.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
        log.error("{}: {}", errorResponse.getStatus(), errorResponse.getMessage());
        return errorResponse;
    }

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
        log.error("{}: {}", errorResponse.getStatus(), errorResponse.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException exception) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .reason(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
        log.error("{}: {}", errorResponse.getStatus(), errorResponse.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException exception) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .reason(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
        log.error("{}: {}", errorResponse.getStatus(), errorResponse.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException exception) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason(HttpStatus.CONFLICT.getReasonPhrase())
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
                .message(exception.getMessage() + "[" + exception.getClass() + "]")
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
        log.error("{}: {}", errorResponse.getStatus(), errorResponse.getMessage());
        return errorResponse;
    }

}
