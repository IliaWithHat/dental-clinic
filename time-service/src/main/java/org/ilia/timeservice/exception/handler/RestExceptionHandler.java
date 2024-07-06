package org.ilia.timeservice.exception.handler;

import org.ilia.timeservice.exception.DuplicateDayException;
import org.ilia.timeservice.exception.UserNotFoundException;
import org.ilia.timeservice.exception.WorkingTimeAlreadyExistException;
import org.ilia.timeservice.exception.WorkingTimeNotFoundException;
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

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        HttpStatus status = NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(WorkingTimeAlreadyExistException.class)
    public final ResponseEntity<Object> handleWorkingTimeAlreadyExistException(WorkingTimeAlreadyExistException ex) {
        HttpStatus status = CONFLICT;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(WorkingTimeNotFoundException.class)
    public final ResponseEntity<Object> handleWorkingTimeNotFoundException(WorkingTimeNotFoundException ex) {
        HttpStatus status = NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(DuplicateDayException.class)
    public final ResponseEntity<Object> handleDuplicateDayException(DuplicateDayException ex) {
        HttpStatus status = BAD_REQUEST;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }
}
