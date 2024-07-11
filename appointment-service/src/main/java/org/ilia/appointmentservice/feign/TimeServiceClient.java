package org.ilia.appointmentservice.feign;

import org.ilia.appointmentservice.configuration.FeignInterceptorConfiguration;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.feign.response.WorkingTimeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "time-service", configuration = FeignInterceptorConfiguration.class)
public interface TimeServiceClient {

    @GetMapping("/api/v1/{role}/{doctorId}/working-time")
    List<WorkingTimeDto> findWorkingTimesByDoctorId(@PathVariable Role role, @PathVariable UUID doctorId);
}
