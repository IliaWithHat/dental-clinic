package org.ilia.timeservice.configuration;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

@Configuration
@EnableFeignClients(basePackages = "org.ilia.timeservice.feign")
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
