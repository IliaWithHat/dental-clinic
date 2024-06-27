package org.ilia.userservice.exception.handler;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ExceptionResponse {

    String message;
    int status;
    LocalDateTime timestamp;

    public ExceptionResponse(RuntimeException exception, HttpStatus status) {
        this.message = exception.getMessage();
        this.status = status.value();
        this.timestamp = LocalDateTime.now();
    }
}
