package org.ilia.mailschedulerservice.feign;

import org.ilia.mailschedulerservice.configuration.FeignInterceptorConfiguration;
import org.ilia.mailschedulerservice.enums.Role;
import org.ilia.mailschedulerservice.feign.request.LoginRequest;
import org.ilia.mailschedulerservice.feign.response.LoginResponse;
import org.ilia.mailschedulerservice.feign.response.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-service", configuration = FeignInterceptorConfiguration.class)
public interface UserServiceClient {

    @PostMapping("/v1/{role}/login")
    LoginResponse login(@RequestBody LoginRequest loginRequest, @PathVariable Role role);

    @GetMapping("/v1/{role}/{id}")
    User findById(@PathVariable Role role, @PathVariable UUID id);

    @GetMapping("/v1/{role}")
    List<User> findByRole(@PathVariable Role role);
}
