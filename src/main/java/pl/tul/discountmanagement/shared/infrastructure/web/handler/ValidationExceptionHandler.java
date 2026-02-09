package pl.tul.discountmanagement.shared.infrastructure.web.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The class of {@link ControllerAdvice} responsible for validation exceptions.
 */
@ControllerAdvice
public class ValidationExceptionHandler {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(ZoneId.systemDefault());
    private static final String TIMESTAMP_ENTRY = "timestamp";
    private static final String STATUS_ENTRY = "status";
    private static final String ERROR_ENTRY = "error";
    private static final String MESSAGE_ENTRY = "message";
    private static final String PATH_ENTRY = "path";
    private static final String FIELD_MESSAGE_DELIMITER = " - ";
    private static final String ERROR_DELIMITER = "; ";

    /**
     * Handler for {@link MethodArgumentNotValidException}.
     *
     * @param ex {@link MethodArgumentNotValidException}
     * @return bad request error code with appropriate message.
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError ->
                        errorMessage.append(fieldError.getField())
                                .append(FIELD_MESSAGE_DELIMITER)
                                .append(fieldError.getDefaultMessage())
                                .append(ERROR_DELIMITER));
        return ResponseEntity.badRequest().body(buildBodyWithBadRequestData(errorMessage));
    }

    /**
     * Handler for {@link ConstraintViolationException}.
     *
     * @param ex {@link ConstraintViolationException}
     * @return bad request error code with appropriate message.
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getConstraintViolations()
                .forEach(constraintViolation -> errorMessage.append(extractPropertyName(constraintViolation))
                        .append(FIELD_MESSAGE_DELIMITER)
                        .append(constraintViolation.getMessage())
                        .append(ERROR_DELIMITER));
        return ResponseEntity.badRequest().body(buildBodyWithBadRequestData(errorMessage));
    }

    private Map<String, Object> buildBodyWithBadRequestData(StringBuilder errorMessage) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP_ENTRY, DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(System.currentTimeMillis())));
        body.put(STATUS_ENTRY, HttpStatus.BAD_REQUEST.value());
        body.put(ERROR_ENTRY, HttpStatus.BAD_REQUEST.name());
        body.put(MESSAGE_ENTRY, errorMessage.toString());
        body.put(PATH_ENTRY, extractUrl());
        return body;
    }

    private String extractPropertyName(ConstraintViolation<?> constraintViolation) {
        String propertyName = null;
        for (Path.Node node : constraintViolation.getPropertyPath()) {
            propertyName = node.getName();
        }
        return propertyName;
    }

    private String extractUrl() {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return httpServletRequest.getServletPath();
    }
}
