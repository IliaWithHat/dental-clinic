package org.ilia.mailschedulerservice.configuration;

import feign.RequestInterceptor;
import lombok.experimental.FieldDefaults;
import org.ilia.mailschedulerservice.service.TokenService;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@EnableFeignClients(basePackages = "org.ilia.mailschedulerservice.feign")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FeignInterceptorConfiguration {

    TokenService tokenService;

    public FeignInterceptorConfiguration(@Lazy TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String token = tokenService.getToken();
            if (token != null) {
                requestTemplate.header("Authorization", String.format("Bearer %s", token));
            }
        };
    }
}
