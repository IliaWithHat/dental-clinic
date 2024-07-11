package org.ilia.reviewservice.feign;

import org.ilia.reviewservice.configuration.FeignInterceptorConfiguration;
import org.ilia.reviewservice.configuration.FeignUserServiceErrorDecoderConfiguration;
import org.ilia.reviewservice.enums.Role;
import org.ilia.reviewservice.feign.response.UserDto;
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
