package org.ilia.timeservice.feign;

import org.ilia.timeservice.configuration.FeignInterceptorConfiguration;
import org.ilia.timeservice.configuration.FeignUserServiceErrorDecoderConfiguration;
import org.ilia.timeservice.enums.Role;
import org.ilia.timeservice.feign.response.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", configuration = {
        FeignInterceptorConfiguration.class, FeignUserServiceErrorDecoderConfiguration.class
})
public interface UserServiceClient {

    @GetMapping("/api/v1/{role}/{id}")
    UserDto findById(@PathVariable Role role, @PathVariable UUID id);
}
