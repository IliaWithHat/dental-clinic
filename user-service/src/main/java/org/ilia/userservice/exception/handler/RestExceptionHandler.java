package org.ilia.userservice.exception.handler;

import org.ilia.userservice.exception.*;
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
import java.util.Map;
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

    @ExceptionHandler(UserAlreadyExistException.class)
    public final ResponseEntity<Object> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        HttpStatus status = CONFLICT;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        HttpStatus status = NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(UserNotHavePermissionException.class)
    public final ResponseEntity<Object> handleUserNotHavePermissionException(UserNotHavePermissionException ex) {
        HttpStatus status = FORBIDDEN;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(UserDeletedException.class)
    public final ResponseEntity<Object> handleUserDeletedException(UserDeletedException ex) {
        HttpStatus status = CONFLICT;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(InvalidIsWorkingFieldException.class)
    public final ResponseEntity<Object> handleInvalidIsWorkingFieldException(InvalidIsWorkingFieldException ex) {
        Map<String, String> error = Map.of("isWorking", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(error);
    }
}
