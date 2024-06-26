package org.ilia.appointmentservice.feign;

import org.ilia.appointmentservice.configuration.FeignInterceptorConfiguration;
import org.ilia.appointmentservice.feign.response.WorkingTimeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "time-service", configuration = FeignInterceptorConfiguration.class)
public interface TimeServiceClient {

    @GetMapping("/v1/working-time/{doctorId}")
    List<WorkingTimeDto> findByDoctorId(@PathVariable UUID doctorId);
}
