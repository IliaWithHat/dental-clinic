package org.ilia.userservice.exception.handler;

import org.ilia.userservice.exception.exceptions.InvalidIsWorkingFieldException;
import org.ilia.userservice.exception.exceptions.UserAlreadyExistException;
import org.ilia.userservice.exception.exceptions.UserNotFoundException;
import org.ilia.userservice.exception.exceptions.UserNotHavePermissionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public final ResponseEntity<Object> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        HttpStatus status = CONFLICT;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex, status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        HttpStatus status = NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex, status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(UserNotHavePermissionException.class)
    public final ResponseEntity<Object> handleUserNotHavePermissionException(UserNotHavePermissionException ex) {
        HttpStatus status = FORBIDDEN;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex, status);
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(InvalidIsWorkingFieldException.class)
    public final ResponseEntity<Object> handleInvalidIsWorkingFieldException(InvalidIsWorkingFieldException ex) {
        Map<String, String> error = Map.of("isWorking", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(error);
    }
}
