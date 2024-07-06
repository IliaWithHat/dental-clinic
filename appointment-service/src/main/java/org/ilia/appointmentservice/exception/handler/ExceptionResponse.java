package org.ilia.appointmentservice.exception.handler;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ExceptionResponse {

    String message;
    int status;
    LocalDateTime timestamp;

    public ExceptionResponse(String message, HttpStatusCode status) {
        this.message = message;
        this.status = status.value();
        this.timestamp = LocalDateTime.now();
    }
}
