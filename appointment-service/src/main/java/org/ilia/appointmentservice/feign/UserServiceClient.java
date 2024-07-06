package org.ilia.appointmentservice.feign;

import org.ilia.appointmentservice.configuration.FeignInterceptorConfiguration;
import org.ilia.appointmentservice.configuration.FeignUserServiceErrorDecoderConfiguration;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.feign.response.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", configuration = {
        FeignInterceptorConfiguration.class, FeignUserServiceErrorDecoderConfiguration.class
})
public interface UserServiceClient {

    @GetMapping("/v1/{role}/{id}")
    UserDto findById(@PathVariable Role role, @PathVariable UUID id);
}
