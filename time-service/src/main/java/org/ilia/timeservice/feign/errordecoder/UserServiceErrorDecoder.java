package org.ilia.timeservice.feign.errordecoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.ilia.timeservice.exception.UserNotFoundException;
import org.ilia.timeservice.exception.handler.ExceptionResponse;

import java.io.InputStream;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserServiceErrorDecoder implements ErrorDecoder {

    ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public Exception decode(String s, Response response) {
        ExceptionResponse exceptionResponse;
        try (InputStream bodyIs = response.body().asInputStream()) {
            exceptionResponse = objectMapper.readValue(bodyIs, ExceptionResponse.class);
        }
        if (exceptionResponse.getStatus() == 404) {
            throw new UserNotFoundException(exceptionResponse.getMessage());
        }
        return new Default().decode(s, response);
    }
}
