package org.ilia.mailschedulerservice.feign;

import org.ilia.mailschedulerservice.configuration.FeignInterceptorConfiguration;
import org.ilia.mailschedulerservice.enums.Role;
import org.ilia.mailschedulerservice.feign.response.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "appointment-service", configuration = FeignInterceptorConfiguration.class)
public interface AppointmentServiceClient {

    @GetMapping("/v1/{role}/{userId}/appointment")
    List<AppointmentDto> find(@RequestParam LocalDate from, @RequestParam LocalDate to,
                              @PathVariable Role role, @PathVariable UUID userId);
}
