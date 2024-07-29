package org.ilia.appointmentservice.exception.handler;

import org.ilia.appointmentservice.exception.*;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Set<ExceptionResponse> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            errors.add(new ExceptionResponse(error.getDefaultMessage(), status));
        });
        return ResponseEntity.status(status).body(errors);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Set<ExceptionResponse> errors = new HashSet<>();
        List<ParameterValidationResult> validationResults = ex.getValueResults().isEmpty() ? ex.getAllValidationResults() : ex.getValueResults();
        validationResults.forEach(error -> {
            errors.add(new ExceptionResponse(error.getResolvableErrors().getFirst().getDefaultMessage(), status));
        });
        return ResponseEntity.status(status).body(errors);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return buildResponseEntity(status, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return buildResponseEntity(status, ex.getMessage());
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<Object> handleInvalidDateRangeException(InvalidDateRangeException ex) {
        HttpStatus status = BAD_REQUEST;
        Set<ExceptionResponse> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            errors.add(new ExceptionResponse(error.getDefaultMessage(), status));
        });
        return ResponseEntity.status(status).body(errors);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public final ResponseEntity<Object> handleUnsupportedOperationException(RuntimeException ex) {
        return buildResponseEntity(UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            AppointmentNotFoundException.class
    })
    public final ResponseEntity<Object> handleNotFoundException(RuntimeException ex) {
        return buildResponseEntity(NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({
            DoctorNotWorkingException.class,
            AppointmentAlreadyExistException.class,
            CompletedAppointmentDeletionException.class
    })
    public final ResponseEntity<Object> handleConflictException(RuntimeException ex) {
        return buildResponseEntity(CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidAppointmentDateException.class)
    public final ResponseEntity<Object> handleBadRequest(RuntimeException ex) {
        return buildResponseEntity(BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Object> buildResponseEntity(HttpStatusCode status, String message) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }
}
