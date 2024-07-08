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
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
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
    public final ResponseEntity<Object> handleUnsupportedOperationException(UnsupportedOperationException ex) {
        HttpStatus status = UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        HttpStatus status = NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(DoctorNotWorkingException.class)
    public final ResponseEntity<Object> handleDoctorNotWorkingException(DoctorNotWorkingException ex) {
        HttpStatus status = CONFLICT;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(AppointmentAlreadyExistException.class)
    public final ResponseEntity<Object> handleAppointmentAlreadyExistException(AppointmentAlreadyExistException ex) {
        HttpStatus status = CONFLICT;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public final ResponseEntity<Object> handleAppointmentNotFoundException(AppointmentNotFoundException ex) {
        HttpStatus status = NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(InvalidAppointmentDateException.class)
    public final ResponseEntity<Object> handleInvalidAppointmentDateException(InvalidAppointmentDateException ex) {
        HttpStatus status = BAD_REQUEST;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }
}
