package org.ilia.appointmentservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ilia.appointmentservice.feign.errordecoder.UserServiceErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignUserServiceErrorDecoderConfiguration {

    @Bean
    public UserServiceErrorDecoder userServiceErrorDecoder(ObjectMapper objectMapper) {
        return new UserServiceErrorDecoder(objectMapper);
    }
}
