package org.ilia.appointmentservice.configuration;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

@Configuration
public class FeignInterceptorConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof BearerTokenAuthentication bearerToken) {
                requestTemplate.header("Authorization", String.format("Bearer %s", bearerToken.getToken().getTokenValue()));
            }
        };
    }
}