package org.ilia.timeservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ilia.timeservice.feign.errordecoder.UserServiceErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignUserServiceErrorDecoderConfiguration {

    @Bean
    public UserServiceErrorDecoder userServiceErrorDecoder(ObjectMapper objectMapper) {
        return new UserServiceErrorDecoder(objectMapper);
    }
}
