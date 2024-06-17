package org.ilia.appointmentservice.feign;

import org.ilia.appointmentservice.entity.User;
import org.ilia.appointmentservice.enums.Role;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient("user-service")
public interface UserServiceClient {

    @GetMapping("/v1/{role}/{id}")
    User findById(@PathVariable Role role, @PathVariable UUID id);
}
