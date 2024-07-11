package org.ilia.reviewservice.feign;

import org.ilia.reviewservice.configuration.FeignInterceptorConfiguration;
import org.ilia.reviewservice.enums.Role;
import org.ilia.reviewservice.feign.response.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "appointment-service", configuration = FeignInterceptorConfiguration.class)
public interface AppointmentServiceClient {

    @GetMapping("/api/v1/{role}/{userId}/appointment")
    List<AppointmentDto> find(@PathVariable Role role, @PathVariable UUID userId);
}
